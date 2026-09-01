/**
 * A human-readable title for a solution, inferred from its pid.
 *
 * The real legacy Solution has no title field (see shared-types'
 * Solution doc comment), and `identification` isn't always populated
 * (synthetic fixtures, or a legacy template with no "Set:" label). The
 * pid is the one thing always present, so the title is derived from it.
 *
 * Real alg1ptests pids, after the subject prefix, are
 *   <chapter>_<section>_<setname>_<problem>_<variant>       (e.g. 10_1_chapter10practicetest_10_10)
 * or
 *   coursetest_<n>_<setname>_<problem>_<variant>            (e.g. coursetest_1_algebra1practicetest_10_1)
 * with a handful of malformed ones (empty set token, glued digits).
 * Synthetic fixture pids (sol-linear-eq-1) have no underscore shape at
 * all and fall through to a plain de-slug.
 */

// Glued lowercase words seen in the corpus that a letter/digit split
// alone won't separate.
const GLUED: [RegExp, string][] = [
  [/practicetest/g, "practice test"],
  [/coursetest/g, "course test"],
  [/customquiz/g, "custom quiz"],
];

function prettifyToken(token: string): string {
  let s = token.replace(/-/g, " ");
  // split letter<->digit runs: "chapter10practicetest" -> "chapter 10 practicetest"
  s = s.replace(/([a-z])(\d)/g, "$1 $2").replace(/(\d)([a-z])/g, "$1 $2");
  for (const [re, rep] of GLUED) s = s.replace(re, rep);
  return s
    .split(/\s+/)
    .filter(Boolean)
    .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
    .join(" ");
}

const isNum = (t: string) => /^\d+$/.test(t);

/** `subjectId` (always on a Solution) is required — it's how the subject prefix is stripped off the pid before parsing. */
export function solutionTitle(pid: string, subjectId: string): string {
  let rest = pid;
  if (rest.toLowerCase().startsWith(subjectId.toLowerCase() + "_")) {
    rest = rest.slice(subjectId.length + 1);
  }

  const tokens = rest.split("_").filter((t) => t.length > 0);

  // No underscore structure (synthetic fixtures): de-slug the whole thing.
  if (tokens.length <= 1) {
    const words = pid
      .replace(/^sol[-_]?/i, "")
      .split(/[-_]+/)
      .filter(Boolean);
    return words.map((w) => w.charAt(0).toUpperCase() + w.slice(1)).join(" ") || pid;
  }

  const loc = [...tokens];
  // Trailing variant, then problem number.
  if (loc.length && isNum(loc[loc.length - 1])) loc.pop();
  let problem: string | null = null;
  if (loc.length && isNum(loc[loc.length - 1])) problem = loc.pop()!;

  const firstAlpha = loc.findIndex((t) => !isNum(t));

  let location: string;
  if (firstAlpha === -1) {
    // e.g. "10_1" -> "Chapter 10"
    location = `Chapter ${loc[0]}`;
  } else {
    const segs: string[] = [];
    for (let i = firstAlpha; i < loc.length; i++) {
      if (isNum(loc[i])) continue;
      let seg = prettifyToken(loc[i]);
      if (i + 1 < loc.length && isNum(loc[i + 1])) {
        seg += ` ${loc[i + 1]}`;
        i++;
      }
      segs.push(seg);
    }
    location = segs.join(" — ");
    // Prepend the chapter number if a leading numeric token was dropped
    // and the prettified name doesn't already carry a "Chapter N".
    if (firstAlpha > 0 && isNum(loc[0]) && !/chapter/i.test(location)) {
      location = `Chapter ${loc[0]} — ${location}`;
    }
  }

  return problem ? `${location} · Problem ${problem}` : location;
}
