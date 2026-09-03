package com.catchupmath.cmre.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
 */
public class SolutionStore {

    private static final String LETTERS = "ABCDEFGH";

    private final Path modulesDir;
    private volatile Map<String, String> problemTextByPid;

    public SolutionStore(Path webRoot) {
        this.modulesDir = webRoot.resolve("modules");
    }

    public Optional<String> problemTextFor(String pid) {
        Map<String, String> map = problemTextByPid;
        if (map == null) {
            synchronized (this) {
                if (problemTextByPid == null) {
                    problemTextByPid = load();
                }
                map = problemTextByPid;
            }
        }
        String t = map.get(pid);
        return (t == null || t.isBlank()) ? Optional.empty() : Optional.of(t);
    }

    private Map<String, String> load() {
        Map<String, String> out = new HashMap<>();
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
                        out.put(o.get("pid").getAsString(), composeProblemText(o));
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("SolutionStore: failed to load bundles: " + e);
        }
        System.out.println("SolutionStore: " + out.size() + " problems indexed");
        return out;
    }

    private static String composeProblemText(JsonObject sol) {
        StringBuilder b = new StringBuilder();
        if (sol.has("statement") && !sol.get("statement").isJsonNull()) {
            String st = plain(sol.get("statement").getAsString());
            if (!st.isBlank()) {
                b.append(st).append("\n\n");
            }
        }
        if (sol.has("question") && sol.get("question").isJsonObject()) {
            JsonObject q = sol.getAsJsonObject("question");
            if (q.has("prompt")) {
                b.append(plain(q.get("prompt").getAsString())).append('\n');
            }
            if (q.has("choices") && q.get("choices").isJsonArray()) {
                var choices = q.getAsJsonArray("choices");
                for (int i = 0; i < choices.size(); i++) {
                    JsonObject c = choices.get(i).getAsJsonObject();
                    String letter = i < LETTERS.length() ? String.valueOf(LETTERS.charAt(i)) : String.valueOf(i + 1);
                    b.append(letter).append(". ").append(plain(c.get("content").getAsString())).append('\n');
                }
            }
        }
        return b.toString().strip();
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
