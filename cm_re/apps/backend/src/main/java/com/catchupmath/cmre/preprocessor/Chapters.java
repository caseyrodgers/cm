package com.catchupmath.cmre.preprocessor;

import java.util.Arrays;

/**
 * Chapter grouping derived from a solution's pid — the Java mirror of
 * apps/tutor/src/lib/problemOrder.ts's chapterOf(). Keep the two in
 * sync: the {@code key}s must match so a manifest's baked-in chapter
 * names line up with the client's grouping.
 *
 * pid shape after the "<subjectId>_" prefix:
 *   <chapter>_<section>_<setname>_<problem>_<variant>   e.g. 10_1_chapter10practicetest_10_10
 *   coursetest_<n>_<setname>_<problem>_<variant>        e.g. coursetest_1_algebra1practicetest_10_1
 */
public final class Chapters {

    private Chapters() {}

    /** key: stable grouping id ("ch-10", "course-1", "other"); label: display ("Chapter 10"); rank: sort position. */
    public record Ref(String key, String label, int rank) {}

    public static Ref of(String pid, String subjectId) {
        String rest = pid;
        String prefix = subjectId.toLowerCase() + "_";
        if (rest.toLowerCase().startsWith(prefix)) {
            rest = rest.substring(prefix.length());
        }
        String[] t = Arrays.stream(rest.split("_")).filter(s -> !s.isEmpty()).toArray(String[]::new);

        if (t.length > 0 && t[0].matches("\\d+")) {
            int n = Integer.parseInt(t[0]);
            return new Ref("ch-" + n, "Chapter " + n, n);
        }
        if (t.length > 1 && t[0].equalsIgnoreCase("coursetest") && t[1].matches("\\d+")) {
            int n = Integer.parseInt(t[1]);
            return new Ref("course-" + n, "Course Test " + n, 1000 + n);
        }
        return new Ref("other", "Other", 100000);
    }
}
