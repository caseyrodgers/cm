// Throwaway check, not part of the app — pins solutionTitle() against
// real pid shapes pulled from the alg1ptests corpus plus the synthetic
// fixtures, and then asserts every pid in the deployed bundle produces
// a non-empty title.
// Run: npx tsx src/lib/solutionTitle.check.ts
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { solutionTitle } from "./solutionTitle";

const cases: [string, string, string][] = [
  ["alg1ptests_10_1_chapter10practicetest_10_10", "alg1ptests", "Chapter 10 Practice Test · Problem 10"],
  ["alg1ptests_1_1_chapter1practicetest_10_1", "alg1ptests", "Chapter 1 Practice Test · Problem 10"],
  ["alg1ptests_coursetest_1_algebra1practicetest_10_1", "alg1ptests", "Course Test 1 — Algebra 1 Practice Test · Problem 10"],
  ["alg1ptests_10_1__5_10", "alg1ptests", "Chapter 10 · Problem 5"],
  ["alg1ptests_10_1practicetest__5_10", "alg1ptests", "Chapter 10 — 1 Practice Test · Problem 5"],
  ["sol-linear-eq-1", "algebra1", "Linear Eq 1"],
  ["sol-quadratic-1", "algebra1", "Quadratic 1"],
];

let failed = 0;
for (const [pid, subject, want] of cases) {
  const got = solutionTitle(pid, subject);
  const ok = got === want;
  console.log(`${ok ? "ok  " : "FAIL"} ${pid}  ->  "${got}"${ok ? "" : `   (want "${want}")`}`);
  if (!ok) failed++;
}

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const bundlePath = path.resolve(__dirname, "../../public/modules/alg1ptests/bundle.json");
const bundle = JSON.parse(readFileSync(bundlePath, "utf-8"));
let blank = 0;
for (const s of bundle.solutions as { pid: string; subjectId: string }[]) {
  const t = solutionTitle(s.pid, s.subjectId);
  if (!t || !t.trim()) {
    blank++;
    if (blank <= 5) console.log("  BLANK title for", s.pid);
  }
}
console.log(`\n${bundle.solutions.length} bundle pids -> ${blank} blank titles`);
if (failed || blank) {
  console.log(`\nFAILED: ${failed} case mismatches, ${blank} blank titles`);
  process.exit(1);
}
console.log("\nSOLUTION TITLE CHECK PASSED");
