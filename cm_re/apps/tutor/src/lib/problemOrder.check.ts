// Throwaway check — pins chapterOf / compareProblems / groupByChapter
// against real alg1ptests pid shapes, then sanity-checks the ordering
// and grouping over every pid in the deployed bundle.
// Run: npx tsx src/lib/problemOrder.check.ts
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { chapterOf, compareProblems, orderPids, groupByChapter } from "./problemOrder";

const S = "alg1ptests";
let failed = 0;
const eq = (got: unknown, want: unknown, msg: string) => {
  const ok = JSON.stringify(got) === JSON.stringify(want);
  console.log(`${ok ? "ok  " : "FAIL"} ${msg}${ok ? "" : `  got ${JSON.stringify(got)} want ${JSON.stringify(want)}`}`);
  if (!ok) failed++;
};

eq(chapterOf("alg1ptests_10_1_chapter10practicetest_10_10", S).label, "Chapter 10", "chapterOf standard");
eq(chapterOf("alg1ptests_1_1_chapter1practicetest_2_1", S).rank, 1, "chapterOf rank = chapter number");
eq(chapterOf("alg1ptests_coursetest_1_algebra1practicetest_10_1", S).label, "Course Test 1", "chapterOf coursetest");
eq(chapterOf("alg1ptests_coursetest_2_algebra1practicetest_10_1", S).rank, 1002, "coursetest ranks after chapters");
eq(chapterOf("alg1ptests_10_1__5_10", S).label, "Chapter 10", "chapterOf malformed (empty set token)");

// problem 2 before problem 10 (numeric, not lexical); chapter 2 before chapter 10.
const scrambled = [
  "alg1ptests_10_1_chapter10practicetest_10_10",
  "alg1ptests_2_1_chapter2practicetest_2_2",
  "alg1ptests_2_1_chapter2practicetest_10_2",
  "alg1ptests_1_1_chapter1practicetest_1_1",
  "alg1ptests_coursetest_1_algebra1practicetest_3_1",
];
eq(
  orderPids(scrambled, S),
  [
    "alg1ptests_1_1_chapter1practicetest_1_1",
    "alg1ptests_2_1_chapter2practicetest_2_2",
    "alg1ptests_2_1_chapter2practicetest_10_2",
    "alg1ptests_10_1_chapter10practicetest_10_10",
    "alg1ptests_coursetest_1_algebra1practicetest_3_1",
  ],
  "orderPids: chapter then problem-number, numeric"
);

// Real bundle: grouping covers every pid, chapters are rank-ascending,
// within-chapter order is non-decreasing.
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const bundle = JSON.parse(
  readFileSync(path.resolve(__dirname, "../../public/modules/alg1ptests/bundle.json"), "utf-8")
);
const pids: string[] = bundle.solutions.map((s: { pid: string }) => s.pid);
const groups = groupByChapter(pids, S);
const covered = groups.reduce((n, g) => n + g.pids.length, 0);
eq(covered, pids.length, `groupByChapter covers all ${pids.length} pids`);
const ranks = groups.map((g) => g.chapter.rank);
eq(
  ranks.every((r, i) => i === 0 || r >= ranks[i - 1]),
  true,
  "chapters are in ascending rank"
);
let monotonic = true;
for (const g of groups) {
  for (let i = 1; i < g.pids.length; i++) {
    if (compareProblems(g.pids[i - 1], g.pids[i], S) > 0) monotonic = false;
  }
}
eq(monotonic, true, "within every chapter, pids are in order");
console.log(
  "\nchapters:",
  groups.map((g) => `${g.chapter.label}(${g.pids.length})`).join("  ")
);

if (failed) {
  console.log(`\nFAILED: ${failed}`);
  process.exit(1);
}
console.log("\nPROBLEM ORDER CHECK PASSED");
