package com.catchupmath.cmre.preprocessor;

import com.catchupmath.cmre.server.ClaudeClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bakes an AI-inferred topic name into each chapter of a module's
 * manifest.json (the legacy export has no chapter-title field). Done at
 * assembly time so the client never makes a call — see shared-types'
 * ChapterInfo and the tutor's ModuleDownloadPrompt chapter list.
 *
 * Grouping keys come from {@link Chapters} (the Java mirror of
 * problemOrder.ts's chapterOf). Best-effort: with no ANTHROPIC_API_KEY,
 * or if a per-chapter call fails, that chapter's name is "" and the
 * client falls back to the bare "Chapter N" label.
 *
 * Two ways in:
 *   - ModuleAssembler calls {@link #inferChapters} during a fresh build.
 *   - Standalone `ChapterNamer <moduleDir> [--force]` (re)names an
 *     already-assembled module without re-running the preprocessor;
 *     existing non-empty names are kept unless --force.
 */
public final class ChapterNamer {

    private ChapterNamer() {}

    private static final int SAMPLE_PER_CHAPTER = 3;
    private static final int SAMPLE_TEXT_CAP = 400;

    public static JsonArray inferChapters(JsonArray solutions, String subjectId,
                                          Map<String, String> existing, boolean force) {
        List<JsonObject> list = new ArrayList<>();
        for (JsonElement e : solutions) list.add(e.getAsJsonObject());
        return inferChapters(list, subjectId, existing, force);
    }

    /**
     * @param existing key -> already-known name, reused unless {@code force} (may be null)
     */
    public static JsonArray inferChapters(List<JsonObject> solutions, String subjectId,
                                          Map<String, String> existing, boolean force) {
        LinkedHashMap<String, Chapters.Ref> refByKey = new LinkedHashMap<>();
        Map<String, List<JsonObject>> byKey = new HashMap<>();
        for (JsonObject s : solutions) {
            Chapters.Ref r = Chapters.of(s.get("pid").getAsString(), subjectId);
            refByKey.putIfAbsent(r.key(), r);
            byKey.computeIfAbsent(r.key(), k -> new ArrayList<>()).add(s);
        }
        List<Chapters.Ref> refs = new ArrayList<>(refByKey.values());
        refs.sort(Comparator.comparingInt(Chapters.Ref::rank));

        ClaudeClient claude = new ClaudeClient();
        boolean canCall = claude.isConfigured();
        if (!canCall) {
            System.out.println("ChapterNamer: ANTHROPIC_API_KEY not set — writing chapter structure with empty names");
        }

        JsonArray out = new JsonArray();
        for (Chapters.Ref r : refs) {
            String name = "";
            String reuse = existing == null ? null : existing.get(r.key());
            if (!force && reuse != null && !reuse.isBlank()) {
                name = reuse;
            } else if (canCall) {
                try {
                    name = inferOne(claude, subjectId, r.label(), byKey.get(r.key()));
                    System.out.println("  " + r.label() + " -> " + (name.isBlank() ? "(none)" : name));
                } catch (Exception e) {
                    System.err.println("  " + r.label() + " -> failed: " + e);
                }
            }
            JsonObject c = new JsonObject();
            c.addProperty("key", r.key());
            c.addProperty("label", r.label());
            c.addProperty("name", name);
            c.addProperty("rank", r.rank());
            out.add(c);
        }
        return out;
    }

    private static String inferOne(ClaudeClient claude, String subjectId, String label, List<JsonObject> pool)
            throws Exception {
        List<JsonObject> sample = pool.size() <= SAMPLE_PER_CHAPTER ? pool : pool.subList(0, SAMPLE_PER_CHAPTER);
        StringBuilder texts = new StringBuilder();
        int i = 1;
        for (JsonObject s : sample) {
            String t = problemText(s);
            if (!t.isBlank()) texts.append(i++).append(". ").append(t).append('\n');
        }
        if (texts.length() == 0) return "";
        String prompt = "Here are sample problems from \"" + label + "\" of a \"" + subjectId + "\" course:\n\n"
                + texts
                + "\nBased on their content, give a short, specific topic name for this chapter"
                + " (2-5 words, e.g. \"Linear Equations\" or \"Quadratic Functions\"). Respond with"
                + " ONLY the topic name — no punctuation, quotes, or explanation.";
        String name = claude.complete(prompt).trim().replaceAll("^[\"'.]+|[\"'.]+$", "");
        return name.length() > 60 ? "" : name; // a paragraph back means the model didn't cooperate
    }

    /** statement + MC prompt + choice bodies, tags stripped, whitespace-collapsed, capped. */
    static String problemText(JsonObject s) {
        StringBuilder sb = new StringBuilder();
        if (s.has("statement") && !s.get("statement").isJsonNull()) {
            sb.append(strip(s.get("statement").getAsString())).append(' ');
        }
        if (s.has("question") && s.get("question").isJsonObject()) {
            JsonObject q = s.getAsJsonObject("question");
            if (q.has("prompt") && !q.get("prompt").isJsonNull()) {
                sb.append(strip(q.get("prompt").getAsString())).append(' ');
            }
            if (q.has("choices") && q.get("choices").isJsonArray()) {
                for (JsonElement ce : q.getAsJsonArray("choices")) {
                    if (ce.isJsonObject() && ce.getAsJsonObject().has("content")) {
                        sb.append(strip(ce.getAsJsonObject().get("content").getAsString())).append(" | ");
                    }
                }
            }
        }
        String out = sb.toString().replaceAll("\\s+", " ").trim();
        return out.length() > SAMPLE_TEXT_CAP ? out.substring(0, SAMPLE_TEXT_CAP) : out;
    }

    private static String strip(String html) {
        return html == null ? "" : html.replaceAll("<[^>]*>", " ").replace("&nbsp;", " ");
    }

    // --- standalone: (re)name chapters in an already-assembled module ---
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("usage: ChapterNamer <moduleDir> [--force]");
            System.exit(1);
            return;
        }
        File moduleDir = new File(args[0]);
        boolean force = Arrays.asList(args).contains("--force");

        File manifestFile = new File(moduleDir, "manifest.json");
        File bundleFile = new File(moduleDir, "bundle.json");
        if (!manifestFile.isFile() || !bundleFile.isFile()) {
            System.err.println("need both manifest.json and bundle.json in " + moduleDir);
            System.exit(1);
            return;
        }
        JsonObject manifest = JsonParser
                .parseString(Files.readString(manifestFile.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonObject bundle = JsonParser
                .parseString(Files.readString(bundleFile.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
        String subjectId = manifest.get("subjectId").getAsString();

        Map<String, String> existing = new HashMap<>();
        if (manifest.has("chapters") && manifest.get("chapters").isJsonArray()) {
            for (JsonElement e : manifest.getAsJsonArray("chapters")) {
                JsonObject c = e.getAsJsonObject();
                existing.put(c.get("key").getAsString(), c.has("name") ? c.get("name").getAsString() : "");
            }
        }

        System.out.println("Naming chapters for " + subjectId
                + " (" + bundle.getAsJsonArray("solutions").size() + " solutions)" + (force ? " [--force]" : ""));
        JsonArray chapters = inferChapters(bundle.getAsJsonArray("solutions"), subjectId, existing, force);
        manifest.add("chapters", chapters);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        Files.writeString(manifestFile.toPath(), gson.toJson(manifest), StandardCharsets.UTF_8);
        System.out.println("Wrote " + chapters.size() + " chapters into " + manifestFile);
    }
}
