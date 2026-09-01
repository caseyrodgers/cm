// Throwaway check, not part of the app — exercises the practice-test
// store end to end against real IndexedDB: start a test, answer some,
// finish, score it, survive a reopen, and confirm no network import.
// Run: npx tsx src/offline/practiceTestStore.smoketest.ts
import "fake-indexeddb/auto";

function assert(cond: unknown, msg: string) {
  if (!cond) throw new Error("FAIL: " + msg);
  console.log("ok —", msg);
}

const { getActiveTest, startTest, recordAnswer, finishTest, scoreTest, sample, QUICK_SIZE } = await import(
  "./practiceTestStore"
);

const subjectId = "alg1ptests";

// --- sample() ---
const pool = Array.from({ length: 50 }, (_, i) => `p${i}`);
const picked = sample(pool, 10);
assert(picked.length === 10, "sample() returns the requested count");
assert(new Set(picked).size === 10, "sample() has no duplicates");
assert(picked.every((p) => pool.includes(p)), "sample() only returns pool members");
assert(sample(pool, 999).length === 50, "sample() caps at pool size");
assert(sample([], 5).length === 0, "sample() of empty pool is empty");

// --- lifecycle ---
assert((await getActiveTest(subjectId)) === undefined, "no active test to begin with");

const pids = sample(pool, QUICK_SIZE).map((p) => `${subjectId}_${p}`);
const t0 = await startTest(subjectId, pids, { kind: "random" });
assert(t0.pids.length === QUICK_SIZE && t0.completedAt === null, "startTest creates a fresh, uncompleted test");
assert(Object.keys((await getActiveTest(subjectId))!.answers).length === 0, "new test has no answers");

await recordAnswer(subjectId, pids[0], { selectedIndex: 1, correct: true });
await recordAnswer(subjectId, pids[1], { selectedIndex: 0, correct: false });
await recordAnswer(subjectId, pids[2], { selectedIndex: 2, correct: null }); // unscorable
await recordAnswer(subjectId, "not_in_this_test", { selectedIndex: 0, correct: true }); // ignored

let t = (await getActiveTest(subjectId))!;
assert(Object.keys(t.answers).length === 3, "recordAnswer ignores pids not in the test");
assert(t.answers[pids[0]].selectedIndex === 1, "answer round-trips");

await recordAnswer(subjectId, pids[0], { selectedIndex: 3, correct: false }); // overwrite
t = (await getActiveTest(subjectId))!;
assert(t.answers[pids[0]].selectedIndex === 3 && Object.keys(t.answers).length === 3, "re-answering overwrites, not appends");

let s = scoreTest(t);
assert(s.total === QUICK_SIZE, "score total = test size");
assert(s.answered === 3, "score counts answered");
assert(s.scorable === 2, "score excludes the unscorable (correct === null) answer");
assert(s.correct === 0, "score: 0 correct after the overwrite made pids[0] wrong");

await recordAnswer(subjectId, pids[0], { selectedIndex: 1, correct: true });
s = scoreTest((await getActiveTest(subjectId))!);
assert(s.correct === 1, "score reflects the latest answer");

await finishTest(subjectId);
assert((await getActiveTest(subjectId))!.completedAt !== null, "finishTest stamps completedAt");

// --- survives a reopen ---
const { db } = await import("./db");
db.close();
await db.open();
const reopened = await getActiveTest(subjectId);
assert(reopened && reopened.pids.length === QUICK_SIZE && reopened.completedAt !== null, "test survives a Dexie reopen");

// --- starting a new test overwrites ---
const t1 = await startTest(subjectId, [`${subjectId}_x`, `${subjectId}_y`], { kind: "subject" });
assert(t1.pids.length === 2 && Object.keys((await getActiveTest(subjectId))!.answers).length === 0, "startTest replaces the old test");

// --- local only ---
const src = await import("node:fs").then((fs) =>
  fs.readFileSync(new URL("./practiceTestStore.ts", import.meta.url), "utf-8")
);
assert(!/^\s*import\b[^\n]*\bapi\/client\b/m.test(src), "practiceTestStore.ts has no import of api/client (local-only)");

console.log("\nPRACTICE TEST STORE SMOKE TEST PASSED");
