package com.catchupmath.cmre.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds the "problem text" for a pid — the context sent to Claude —
 * from the same module bundles the server already serves
 * ({webRoot}/modules/index.json -> per-subject bundle.json). Loaded
 * once, lazily, on first lookup.
 *
 * For practice-test items the <statement> is nearly empty because the
 * preprocessor lifts the multiple-choice question out into
 * `question` (prompt + choices), so the text here is statement +
 * question prompt + lettered choices. HTML tags are stripped.
 *
 * Some legacy problems author the equation and/or answer choices as
 * an inline {@code <img>} rather than text/MathML (see
 * SOLUTION_INFO.org — ~43% of the corpus references images). Text
 * extraction alone silently drops that content, which is exactly why
 * the "Learn" feature was seen replying "the equation itself isn't
 * showing" (2026-09-04). So alongside the plain text, this also
 * resolves those {@code <img src>} references to real files under
 * webRoot so AiService can attach them to the model call as vision
 * input instead of losing them.
 */
public class SolutionStore {

    private static final String LETTERS = "ABCDEFGH";
    private static final int MAX_IMAGES_PER_PROBLEM = 4;
    private static final Pattern IMG_SRC = Pattern.compile("<img[^>]*\\ssrc=[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);

    /** One problem's context: readable text plus any images its statement/choices embed instead of text. */
    public record ProblemContext(String text, List<Path> images) {}

    private final Path webRoot;
    private final Path modulesDir;
    private volatile Map<String, ProblemContext> problemContextByPid;

    public SolutionStore(Path webRoot) {
        this.webRoot = webRoot.normalize();
        this.modulesDir = this.webRoot.resolve("modules");
    }

    public Optional<String> problemTextFor(String pid) {
        ProblemContext ctx = contextFor(pid);
        return (ctx == null || ctx.text().isBlank()) ? Optional.empty() : Optional.of(ctx.text());
    }

    /** Image files (already resolved, confirmed to exist on disk) referenced by this problem's statement/choices. Empty when there are none. */
    public List<Path> problemImagesFor(String pid) {
        ProblemContext ctx = contextFor(pid);
        return ctx == null ? List.of() : ctx.images();
    }

    private ProblemContext contextFor(String pid) {
        Map<String, ProblemContext> map = problemContextByPid;
        if (map == null) {
            synchronized (this) {
                if (problemContextByPid == null) {
                    problemContextByPid = load();
                }
                map = problemContextByPid;
            }
        }
        return map.get(pid);
    }

    private Map<String, ProblemContext> load() {
        Map<String, ProblemContext> out = new HashMap<>();
        Path index = modulesDir.resolve("index.json");
        if (!Files.isRegularFile(index)) {
            System.err.println("SolutionStore: no " + index + " — problem lookups will miss");
            return out;
        }
        try {
            var subjects = JsonParser.parseString(Files.readString(index, StandardCharsets.UTF_8)).getAsJsonArray();
            for (var s : subjects) {
                String subjectId = s.getAsJsonObject().get("subjectId").getAsString();
                Path bundle = modulesDir.resolve(subjectId).resolve("bundle.json");
                if (!Files.isRegularFile(bundle)) {
                    continue;
                }
                var root = JsonParser.parseString(Files.readString(bundle, StandardCharsets.UTF_8)).getAsJsonObject();
                for (var sol : root.getAsJsonArray("solutions")) {
                    JsonObject o = sol.getAsJsonObject();
                    if (o.has("pid")) {
                        out.put(o.get("pid").getAsString(), composeProblemContext(o, webRoot));
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("SolutionStore: failed to load bundles: " + e);
        }
        System.out.println("SolutionStore: " + out.size() + " problems indexed");
        return out;
    }

    private static ProblemContext composeProblemContext(JsonObject sol, Path webRoot) {
        StringBuilder b = new StringBuilder();
        List<Path> images = new ArrayList<>();
        if (sol.has("statement") && !sol.get("statement").isJsonNull()) {
            String raw = sol.get("statement").getAsString();
            collectImages(raw, webRoot, images);
            String st = plain(raw);
            if (!st.isBlank()) {
                b.append(st).append("\n\n");
            }
        }
        if (sol.has("question") && sol.get("question").isJsonObject()) {
            JsonObject q = sol.getAsJsonObject("question");
            if (q.has("prompt")) {
                String raw = q.get("prompt").getAsString();
                collectImages(raw, webRoot, images);
                b.append(plain(raw)).append('\n');
            }
            if (q.has("choices") && q.get("choices").isJsonArray()) {
                var choices = q.getAsJsonArray("choices");
                for (int i = 0; i < choices.size(); i++) {
                    JsonObject c = choices.get(i).getAsJsonObject();
                    String letter = i < LETTERS.length() ? String.valueOf(LETTERS.charAt(i)) : String.valueOf(i + 1);
                    String raw = c.get("content").getAsString();
                    collectImages(raw, webRoot, images);
                    b.append(letter).append(". ").append(plain(raw)).append('\n');
                }
            }
        }
        return new ProblemContext(b.toString().strip(), images);
    }

    /** Finds {@code <img src="...">} references in authored HTML and resolves them to real files under webRoot, skipping anything missing, external, or outside webRoot. */
    private static void collectImages(String html, Path webRoot, List<Path> out) {
        if (out.size() >= MAX_IMAGES_PER_PROBLEM) {
            return;
        }
        Matcher m = IMG_SRC.matcher(html);
        while (m.find() && out.size() < MAX_IMAGES_PER_PROBLEM) {
            String src = m.group(1);
            if (src.startsWith("http://") || src.startsWith("https://") || src.startsWith("data:")) {
                continue;
            }
            String rel = src.startsWith("/") ? src.substring(1) : src;
            Path p = webRoot.resolve(rel).normalize();
            if (!p.startsWith(webRoot) || !Files.isRegularFile(p)) {
                continue;
            }
            out.add(p);
        }
    }

    /** Authored HTML (may carry MathML) -> readable plain text for the prompt. */
    private static String plain(String html) {
        return html.replaceAll("(?s)<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\s*\\n\\s*", "\n")
                .trim();
    }
}
