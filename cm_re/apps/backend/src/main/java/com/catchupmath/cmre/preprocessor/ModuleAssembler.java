package com.catchupmath.cmre.preprocessor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Second half of the pre-processor pipeline: takes the per-solution
 * <pid>.json files LegacySolutionPreprocessor writes and assembles
 * them into the manifest.json/bundle.json shape apps/tutor actually
 * fetches (see shared-types' ModuleManifest/ModuleBundle). Runs
 * in-place — the moduleDir passed in should be
 * apps/tutor/public/modules/<subjectId>/, i.e. the same directory
 * LegacySolutionPreprocessor was pointed at as its output-dir, so the
 * per-pid asset folders it already wrote there are already correct
 * and untouched by this step.
 *
 * After assembling, the loose per-solution <pid>.json files are
 * deleted — bundle.json is the single source of solution content from
 * here on, matching the existing algebra1/geometry fixture layout
 * (manifest.json + bundle.json only, no loose files alongside).
 *
 * Usage: ModuleAssembler <moduleDir> <subjectId> <version>
 */
public class ModuleAssembler {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: ModuleAssembler <moduleDir> <subjectId> <version>");
            System.exit(1);
            return;
        }

        File moduleDir = new File(args[0]);
        String subjectId = args[1];
        String version = args[2];

        File[] solutionFiles = moduleDir.listFiles((d, name) -> name.endsWith(".json"));
        if (solutionFiles == null || solutionFiles.length == 0) {
            System.err.println("no <pid>.json files found in " + moduleDir);
            System.exit(1);
            return;
        }

        JsonArray solutions = new JsonArray();
        List<String> solutionIds = new ArrayList<>();
        for (File f : solutionFiles) {
            String json = Files.readString(f.toPath(), StandardCharsets.UTF_8);
            JsonObject solution = JsonParser.parseString(json).getAsJsonObject();
            solutions.add(solution);
            solutionIds.add(solution.get("pid").getAsString());
        }
        Collections.sort(solutionIds);

        JsonObject bundle = new JsonObject();
        bundle.add("solutions", solutions);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        String bundleJson = gson.toJson(bundle);
        File bundleFile = new File(moduleDir, "bundle.json");
        Files.writeString(bundleFile.toPath(), bundleJson, StandardCharsets.UTF_8);

        // The loose per-solution files did their job feeding bundle.json;
        // remove them so the module directory matches the existing
        // fixture convention (manifest.json + bundle.json + asset dirs
        // only) — and, just as importantly, BEFORE computing
        // approxSizeBytes below. Computing it first (an earlier version
        // of this code did) double-counted every solution's content:
        // once in the freshly-written bundle.json, once in the not-yet-
        // deleted <pid>.json originals still sitting in the same
        // directory — inflating the real ~5 MB module to a reported
        // ~8.75 MB.
        for (File f : solutionFiles) {
            f.delete();
        }

        long approxSizeBytes = directorySizeExcluding(moduleDir, null);

        JsonObject manifest = new JsonObject();
        manifest.addProperty("subjectId", subjectId);
        manifest.addProperty("version", version);
        JsonArray solutionIdsJson = new JsonArray();
        solutionIds.forEach(solutionIdsJson::add);
        manifest.add("solutionIds", solutionIdsJson);
        manifest.addProperty("approxSizeBytes", approxSizeBytes);
        Files.writeString(new File(moduleDir, "manifest.json").toPath(), gson.toJson(manifest), StandardCharsets.UTF_8);

        System.out.println("Assembled " + solutionIds.size() + " solutions into " + bundleFile);
        System.out.println("approxSizeBytes: " + approxSizeBytes + " (" + (approxSizeBytes / 1024) + " KB)");
    }

    private static long directorySizeExcluding(File dir, File exclude) throws IOException {
        long[] total = {0};
        Files.walk(dir.toPath())
                .filter(Files::isRegularFile)
                .filter(p -> !p.toFile().equals(exclude))
                .forEach(p -> {
                    try {
                        total[0] += Files.size(p);
                    } catch (IOException ignored) {
                    }
                });
        return total[0];
    }
}
