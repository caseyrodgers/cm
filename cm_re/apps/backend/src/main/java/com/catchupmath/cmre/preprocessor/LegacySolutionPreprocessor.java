package com.catchupmath.cmre.preprocessor;

import com.catchupmath.cmre.preprocessor.model.Solution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One-time pre-processor CLI: converts every legacy solution export
 * under an input directory into cm_re's clean Solution JSON format —
 * see NEW_DIRECTION.org's "Legacy content pre-processor" decision.
 * solution_editor and the tutor never see raw legacy HTML; this tool
 * is where that responsibility lives, run once per solution.
 *
 * Usage: LegacySolutionPreprocessor <input-dir> <subjectId> <output-dir>
 *
 * Output layout, per solution:
 *   <output-dir>/<pid>.json          — the clean Solution document
 *   <output-dir>/<pid>/<filename>    — every image the cleaned HTML
 *                                       actually references (see
 *                                       copyReferencedImages)
 */
public class LegacySolutionPreprocessor {

    // Per-solution image refs in the CLEANED solution JSON: HtmlCleaner
    // has already rewritten every kept <img src> to
    // "/modules/<subjectId>/<pid>/<filename>". Pull the filenames back
    // out so we copy exactly what's referenced — nothing more, nothing
    // less. Shared "/specialchars/..." icons are rewritten to a
    // different prefix and correctly don't match.
    private static final Pattern MODULE_IMAGE_REF = Pattern.compile(
            "/modules/[^\"/]+/[^\"/]+/([^\"/?#]+\\.(?:gif|png|jpe?g|svg))", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: LegacySolutionPreprocessor <input-dir> <subjectId> <output-dir>");
            System.exit(1);
            return;
        }

        File input = new File(args[0]);
        String subjectId = args[1];
        File outputDir = new File(args[2]);
        outputDir.mkdirs();

        List<File> solutionDirs = new ArrayList<>();
        findSolutionDirs(input, solutionDirs);
        System.out.println("Found " + solutionDirs.size() + " solution director" + (solutionDirs.size() == 1 ? "y" : "ies") + " under " + input);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        int succeeded = 0;
        int copiedImages = 0;
        List<String> failures = new ArrayList<>();
        List<String> missingImages = new ArrayList<>();

        for (File dir : solutionDirs) {
            try {
                Solution solution = LegacySolutionParser.parse(dir, subjectId, input);
                String json = gson.toJson(solution);
                Files.writeString(
                        new File(outputDir, solution.pid + ".json").toPath(),
                        json,
                        StandardCharsets.UTF_8);
                copiedImages += copyReferencedImages(dir, outputDir, solution.pid, referencedImageNames(json), missingImages);
                succeeded++;
            } catch (Exception e) {
                failures.add(dir + ": " + e);
            }
        }

        System.out.println("Converted: " + succeeded + " succeeded, " + failures.size() + " failed");
        System.out.println("Images copied: " + copiedImages);
        for (String failure : failures) {
            System.out.println("  FAILED: " + failure);
        }
        if (!missingImages.isEmpty()) {
            System.out.println("Referenced images NOT found anywhere under the solution's export dir ("
                    + missingImages.size() + " -- genuine gaps in the source export, see SOLUTION_INFO.org):");
            for (String miss : missingImages) {
                System.out.println("  MISSING: " + miss);
            }
        }
    }

    private static Set<String> referencedImageNames(String cleanedJson) {
        Set<String> names = new LinkedHashSet<>();
        Matcher m = MODULE_IMAGE_REF.matcher(cleanedJson);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names;
    }

    /** A "solution dir" contains tutor_steps.html or tutor_steps_2.html. Skips our own version2/ conversion-artifact dirs. */
    private static void findSolutionDirs(File dir, List<File> result) {
        if (new File(dir, "tutor_steps.html").exists() || new File(dir, "tutor_steps_2.html").exists()) {
            result.add(dir);
            return;
        }
        File[] children = dir.listFiles(File::isDirectory);
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.getName().equals("version2")) {
                continue;
            }
            findSolutionDirs(child, result);
        }
    }

    /**
     * Copies exactly the images the cleaned HTML references (by
     * filename), from the solution's own export dir into
     * <output-dir>/<pid>/. Returns the count copied; appends any
     * referenced-but-absent filenames to {@code missing} (mostly
     * cross-solution references — a step reusing a sibling solution's
     * equation image — which the same-dir HtmlCleaner rewrite doesn't
     * follow; tracked in SOLUTION_INFO.org).
     *
     * Reference-based, NOT name-pattern-based. An earlier version
     * skipped every "imageNNN.gif" as a presumed WIRIS equation-
     * snapshot duplicate, but ~43% of the alg1ptests corpus references
     * those files directly as the rendered math in a step (no MathML
     * alternative in that spot), so they 404'd. Copying by reference
     * keeps what's used and still drops the genuinely-orphan WIRIS
     * dupes — nothing points at them, so they're never in the set.
     *
     * HtmlCleaner flattens every rewritten src to
     * ".../<pid>/<basename>", dropping any intermediate path segment
     * (some exports keep images in a "resources/" subdir). So the file
     * is looked up first directly under solutionDir, then by a
     * subtree walk for that basename — and always written flat, to
     * match the URL the cleaner emitted.
     */
    private static int copyReferencedImages(File solutionDir, File outputDir, String pid,
                                            Set<String> referenced, List<String> missing) throws IOException {
        if (referenced.isEmpty()) {
            return 0;
        }
        File assetsDir = new File(outputDir, pid);
        int copied = 0;
        for (String name : referenced) {
            File src = new File(solutionDir, name);
            if (!src.isFile()) {
                src = findInSubtree(solutionDir, name);
            }
            if (src == null || !src.isFile()) {
                missing.add(pid + "/" + name);
                continue;
            }
            assetsDir.mkdirs();
            Files.copy(src.toPath(), new File(assetsDir, name).toPath(), StandardCopyOption.REPLACE_EXISTING);
            copied++;
        }
        return copied;
    }

    /** First regular file with this exact (case-insensitive) name anywhere under root, or null. */
    private static File findInSubtree(File root, String name) throws IOException {
        try (var stream = Files.walk(root.toPath())) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(name))
                    .map(java.nio.file.Path::toFile)
                    .findFirst()
                    .orElse(null);
        }
    }
}
