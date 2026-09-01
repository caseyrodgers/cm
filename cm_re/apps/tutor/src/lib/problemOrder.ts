/**
 * Chapter grouping and a logical ordering for solutions, both inferred
 * from the pid (same rationale as solutionTitle.ts — the real model
 * has no chapter/order field, the pid is what's always there).
 *
 * pid shape after the subject prefix, in the alg1ptests corpus:
 *   <chapter>_<section>_<setname>_<problem>_<variant>   e.g. 10_1_chapter10practicetest_10_10
 *   coursetest_<n>_<setname>_<problem>_<variant>        e.g. coursetest_1_algebra1practicetest_10_1
 *   plus a few malformed ones (empty token, glued digits).
 */

const isNum = (t: string) => /^\d+$/.test(t);

function tokensOf(pid: string, subjectId: string): string[] {
  let rest = pid;
  if (rest.toLowerCase().startsWith(subjectId.toLowerCase() + "_")) {
    rest = rest.slice(subjectId.length + 1);
  }
  return rest.split("_").filter(Boolean);
}

export interface Chapter {
  /** stable id for grouping, e.g. "ch-10", "course-1", "other" */
  key: string;
  label: string;
  /** sort position: numeric chapters 1..N, then course tests, then the rest */
  rank: number;
}

export function chapterOf(pid: string, subjectId: string): Chapter {
  const t = tokensOf(pid, subjectId);
  if (t.length && isNum(t[0])) {
    const n = Number(t[0]);
    return { key: `ch-${n}`, label: `Chapter ${n}`, rank: n };
  }
  if (t[0] === "coursetest" && t[1] && isNum(t[1])) {
    const n = Number(t[1]);
    return { key: `course-${n}`, label: `Course Test ${n}`, rank: 1000 + n };
  }
  return { key: "other", label: "Other", rank: 100000 };
}

/** [chapterRank, section, problemNumber, variant] — trailing tokens are the variant then the problem number. */
function sortTuple(pid: string, subjectId: string): number[] {
  const t = tokensOf(pid, subjectId);
  const nums = [...t];
  let variant = 0;
  let problem = 0;
  if (nums.length && isNum(nums[nums.length - 1])) variant = Number(nums.pop());
  if (nums.length && isNum(nums[nums.length - 1])) problem = Number(nums.pop());
  const section = isNum(t[0]) && t[1] && isNum(t[1]) ? Number(t[1]) : 0;
  return [chapterOf(pid, subjectId).rank, section, problem, variant];
}

export function compareProblems(a: string, b: string, subjectId: string): number {
  const A = sortTuple(a, subjectId);
  const B = sortTuple(b, subjectId);
  for (let i = 0; i < Math.max(A.length, B.length); i++) {
    const d = (A[i] ?? 0) - (B[i] ?? 0);
    if (d) return d;
  }
  return a < b ? -1 : a > b ? 1 : 0; // stable tiebreak on the raw pid
}

export function orderPids(pids: readonly string[], subjectId: string): string[] {
  return [...pids].sort((a, b) => compareProblems(a, b, subjectId));
}

/** Groups pids into chapters, chapters in rank order, pids within a chapter in problem order. */
export function groupByChapter(
  pids: readonly string[],
  subjectId: string
): { chapter: Chapter; pids: string[] }[] {
  const byKey = new Map<string, { chapter: Chapter; pids: string[] }>();
  for (const pid of pids) {
    const ch = chapterOf(pid, subjectId);
    let entry = byKey.get(ch.key);
    if (!entry) {
      entry = { chapter: ch, pids: [] };
      byKey.set(ch.key, entry);
    }
    entry.pids.push(pid);
  }
  const groups = [...byKey.values()];
  groups.sort((a, b) => a.chapter.rank - b.chapter.rank);
  for (const g of groups) g.pids = orderPids(g.pids, subjectId);
  return groups;
}
