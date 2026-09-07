package com.catchupmath.cmre.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * File-backed CRUD over a subject's bundle.json, for apps/editor.
 *
 * Source of truth is apps/tutor/public/modules/&lt;subjectId&gt;/bundle.json
 * — the same file LegacySolutionPreprocessor / ModuleAssembler produce
 * and the tutor's dev build serves. The editor reads and rewrites
 * solution entries there; {@link #publish} recomputes manifest.json
 * and copies both into the served web root so a running tutor picks up
 * the change (its ModuleManifest.version bump drives the update check).
 *
 * Deliberately NOT Spring/JPA/Postgres — see SOLUTION_EDITOR.org. This
 * is the file-store stand-in that unblocks the editor UI; the DB
 * migration is a separate later decision. Re-reads bundle.json on
 * every call — no cache, always fresh; a few MB per read is fine for a
 * single-user dev tool.
 */
public final class SolutionSource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private final Path contentRoot;       // apps/tutor/public/modules
    private final Path servedModulesRoot; // <webRoot>/modules  (may be null)

    public SolutionSource(Path contentRoot, Path servedModulesRoot) {
        this.contentRoot = contentRoot;
        this.servedModulesRoot = servedModulesRoot;
    }

    /** Subject ids that have a bundle.json under the content root, sorted. */
    public List<String> subjects() throws IOException {
        List<String> out = new ArrayList<>();
        if (!Files.isDirectory(contentRoot)) {
            return out;
        }
        try (Stream<Path> s = Files.list(contentRoot)) {
            s.filter(Files::isDirectory)
             .filter(p -> Files.isRegularFile(p.resolve("bundle.json")))
             .forEach(p -> out.add(p.getFileName().toString()));
        }
        Collections.sort(out);
        return out;
    }

    /** JSON array of lightweight per-solution summaries for one subject, ordered by pid. */
    public String listSolutions(String subjectId) throws IOException {
        List<JsonObject> sorted = new ArrayList<>();
        readBundle(subjectId).getAsJsonArray("solutions").forEach(e -> sorted.add(e.getAsJsonObject()));
        sorted.sort((a, b) -> pid(a).compareTo(pid(b)));

        JsonArray out = new JsonArray();
        for (JsonObject sol : sorted) {
            JsonObject sum = new JsonObject();
            sum.addProperty("pid", pid(sol));
            sum.addProperty("subjectId", subjectId);
            if (sol.has("identification") && sol.get("identification").isJsonObject()) {
                sum.add("identification", sol.getAsJsonObject("identification"));
            }
            sum.addProperty("statementPreview", preview(optString(sol, "statement")));
            sum.addProperty("hasQuestion", sol.has("question") && sol.get("question").isJsonObject());
            sum.addProperty("hasWidget", sol.has("widgetSlot") && sol.get("widgetSlot").isJsonObject());
            sum.addProperty("stepCount", sol.has("steps") && sol.get("steps").isJsonArray()
                    ? sol.getAsJsonArray("steps").size() : 0);
            out.add(sum);
        }
        return GSON.toJson(out);
    }

    /** The full solution document, or null if no subject's bundle has that pid. */
    public String getSolution(String pid) throws IOException {
        for (String subjectId : subjects()) {
            for (JsonElement e : readBundle(subjectId).getAsJsonArray("solutions")) {
                if (pid.equals(pid(e.getAsJsonObject()))) {
                    return GSON.toJson(e);
                }
            }
        }
        return null;
    }

    /**
     * Replace the entry for {@code pid} in its subject's bundle.json.
     * {@code body} must be a JSON object whose own "pid" equals
     * {@code pid}. Returns the stored document, or null if pid is
     * unknown anywhere. Throws IllegalArgumentException on a malformed
     * body or pid mismatch.
     */
    public String saveSolution(String pid, String body) throws IOException {
        JsonObject incoming;
        try {
            incoming = JsonParser.parseString(body).getAsJsonObject();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("request body is not a JSON object");
        }
        if (!incoming.has("pid") || !pid.equals(incoming.get("pid").getAsString())) {
            throw new IllegalArgumentException("body pid does not match the URL pid");
        }
        for (String subjectId : subjects()) {
            JsonObject bundle = readBundle(subjectId);
            JsonArray solutions = bundle.getAsJsonArray("solutions");
            for (int i = 0; i < solutions.size(); i++) {
                if (pid.equals(pid(solutions.get(i).getAsJsonObject()))) {
                    solutions.set(i, incoming);
                    Files.writeString(bundlePath(subjectId), GSON.toJson(bundle), StandardCharsets.UTF_8);
                    return GSON.toJson(incoming);
                }
            }
        }
        return null;
    }

    /**
     * Recompute manifest.json for the subject in the content root
     * (version = content hash of bundle.json, sorted solutionIds,
     * approxSizeBytes = size of the module dir), then copy bundle.json
     * + manifest.json into the served web root. Returns the manifest
     * JSON. A running tutor sees the version bump on its next update
     * check.
     */
    public String publish(String subjectId) throws IOException {
        Path srcDir = contentRoot.resolve(subjectId);
        Path srcBundle = srcDir.resolve("bundle.json");
        if (!Files.isRegularFile(srcBundle)) {
            throw new IllegalArgumentException("no bundle.json for subject " + subjectId);
        }
        byte[] bundleBytes = Files.readAllBytes(srcBundle);
        JsonObject bundle = JsonParser.parseString(new String(bundleBytes, StandardCharsets.UTF_8)).getAsJsonObject();

        List<String> ids = new ArrayList<>();
        bundle.getAsJsonArray("solutions").forEach(e -> ids.add(pid(e.getAsJsonObject())));
        Collections.sort(ids);

        JsonObject manifest = new JsonObject();
        manifest.addProperty("subjectId", subjectId);
        manifest.addProperty("version", sha256Hex(bundleBytes).substring(0, 12));
        JsonArray idsJson = new JsonArray();
        ids.forEach(idsJson::add);
        manifest.add("solutionIds", idsJson);
        manifest.addProperty("approxSizeBytes", dirSize(srcDir));

        String manifestJson = GSON.toJson(manifest);
        Files.writeString(srcDir.resolve("manifest.json"), manifestJson, StandardCharsets.UTF_8);

        if (servedModulesRoot != null) {
            Path destDir = servedModulesRoot.resolve(subjectId);
            Files.createDirectories(destDir);
            Files.write(destDir.resolve("bundle.json"), bundleBytes);
            Files.writeString(destDir.resolve("manifest.json"), manifestJson, StandardCharsets.UTF_8);
        }
        return manifestJson;
    }

    // ---- internals ----

    private Path bundlePath(String subjectId) {
        return contentRoot.resolve(subjectId).resolve("bundle.json");
    }

    private JsonObject readBundle(String subjectId) throws IOException {
        Path p = bundlePath(subjectId);
        if (!Files.isRegularFile(p)) {
            throw new IllegalArgumentException("unknown subject: " + subjectId);
        }
        JsonObject o = JsonParser.parseString(Files.readString(p, StandardCharsets.UTF_8)).getAsJsonObject();
        if (!o.has("solutions") || !o.get("solutions").isJsonArray()) {
            throw new IOException("bundle.json for " + subjectId + " has no solutions array");
        }
        return o;
    }

    private static String pid(JsonObject sol) {
        return sol.has("pid") && !sol.get("pid").isJsonNull() ? sol.get("pid").getAsString() : "";
    }

    private static String optString(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "";
    }

    private static String preview(String html) {
        String text = html.replaceAll("(?s)<[^>]+>", " ").replace("&nbsp;", " ").replaceAll("\\s+", " ").trim();
        return text.length() > 140 ? text.substring(0, 140) + "…" : text;
    }

    private static long dirSize(Path dir) throws IOException {
        try (Stream<Path> s = Files.walk(dir)) {
            return s.filter(Files::isRegularFile).mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    return 0L;
                }
            }).sum();
        }
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) {
                sb.append(Character.forDigit((b >> 4) & 0xf, 16)).append(Character.forDigit(b & 0xf, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
