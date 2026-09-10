import { inferChapterName } from "../api/aiClient";

/** "Chapter 3: Ratios and Proportions", or just "Chapter 3" when no topic name is known. */
export function chapterDisplay(label: string, name?: string | null): string {
  return name ? `${label}: ${name}` : label;
}

/** true for the cumulative "Course Test N" buckets (chapterOf key "course-1", …). */
export function isCourseTest(chapterKey: string): boolean {
  return chapterKey.startsWith("course-");
}

/** The detail line shown under a "Course Test …" label. */
export const COURSE_TEST_BLURB =
  "A cumulative test — problems drawn from across the whole course, not a single chapter.";

/**
 * Cached wrapper around inferChapterName — the AI call is a real
 * (if cheap) round-trip, so a chapter's deduced name is looked up once
 * per browser and remembered in localStorage from then on.
 */

const KEY_PREFIX = "cm_re.chapterName.";

function cacheKey(subjectId: string, chapterLabel: string): string {
  return `${KEY_PREFIX}${subjectId}::${chapterLabel}`;
}

/** "" when there's nothing cached and inference isn't available/fails — callers fall back to the numeric label alone. */
export async function chapterTopicName(subjectId: string, chapterLabel: string, samplePids: string[]): Promise<string> {
  const key = cacheKey(subjectId, chapterLabel);
  try {
    const cached = localStorage.getItem(key);
    if (cached !== null) return cached;
  } catch {
    /* private mode / storage disabled — just skip the cache */
  }

  try {
    const { name, placeholder } = await inferChapterName(subjectId, chapterLabel, samplePids);
    if (!placeholder && name) {
      try {
        localStorage.setItem(key, name);
      } catch {
        /* not fatal — just not sticky this time */
      }
      return name;
    }
  } catch {
    /* offline / server down — degrade quietly, no name */
  }
  return "";
}
