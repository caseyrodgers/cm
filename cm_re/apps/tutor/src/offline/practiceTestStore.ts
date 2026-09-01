import { db, type PracticeTest, type TestAnswer, type TestScope } from "./db";

/**
 * Per-subject practice-test persistence. IndexedDB only — no
 * api/client import, never the server (same posture as
 * whiteboardStore). One active test per subject; startTest overwrites
 * whatever was there.
 */

export type { PracticeTest, TestAnswer, TestScope };

/** Question count for a random quick test / for a per-chapter test. */
export const QUICK_SIZE = 10;
export const CHAPTER_SIZE = 5;

/** Human title for a test, from its scope. Tolerates a missing scope (tests stored before the field existed). */
export function testTitle(scope: TestScope | undefined): string {
  if (scope?.kind === "subject") return "Whole subject test";
  if (scope?.kind === "chapter") return `${scope.label} test`;
  return "Quick test";
}

/** The subject's current test, or `undefined` if none has been started. */
export async function getActiveTest(subjectId: string): Promise<PracticeTest | undefined> {
  return db.practiceTests.get(subjectId);
}

/** Picks n random items without replacement (partial Fisher–Yates). */
export function sample<T>(items: readonly T[], n: number): T[] {
  const pool = items.slice();
  const take = Math.min(n, pool.length);
  for (let i = 0; i < take; i++) {
    const j = i + Math.floor(Math.random() * (pool.length - i));
    [pool[i], pool[j]] = [pool[j], pool[i]];
  }
  return pool.slice(0, take);
}

/** Starts (or restarts) the subject's test. `pids` should already be in problem order. */
export async function startTest(subjectId: string, pids: string[], scope: TestScope): Promise<PracticeTest> {
  const test: PracticeTest = {
    subjectId,
    pids,
    answers: {},
    scope,
    startedAt: Date.now(),
    completedAt: null,
  };
  await db.practiceTests.put(test);
  return test;
}

/** Records (or overwrites) the answer for one problem in the active test. */
export async function recordAnswer(subjectId: string, pid: string, answer: TestAnswer): Promise<void> {
  await db.transaction("rw", db.practiceTests, async () => {
    const test = await db.practiceTests.get(subjectId);
    if (!test || !test.pids.includes(pid)) return;
    test.answers[pid] = answer;
    await db.practiceTests.put(test);
  });
}

/** Stamps the test complete. Score is derived from `answers`, not stored separately. */
export async function finishTest(subjectId: string): Promise<void> {
  await db.transaction("rw", db.practiceTests, async () => {
    const test = await db.practiceTests.get(subjectId);
    if (!test) return;
    test.completedAt = Date.now();
    await db.practiceTests.put(test);
  });
}

export async function clearTest(subjectId: string): Promise<void> {
  await db.practiceTests.delete(subjectId);
}

/** correct / scorable, plus how many were answered at all. */
export function scoreTest(test: PracticeTest): { correct: number; scorable: number; answered: number; total: number } {
  let correct = 0;
  let scorable = 0;
  let answered = 0;
  for (const pid of test.pids) {
    const a = test.answers[pid];
    if (!a) continue;
    answered++;
    if (a.correct === null) continue;
    scorable++;
    if (a.correct) correct++;
  }
  return { correct, scorable, answered, total: test.pids.length };
}
