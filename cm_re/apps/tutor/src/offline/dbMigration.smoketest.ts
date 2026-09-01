// Not part of the app bundle — reproduces the EXACT bug Casey hit in
// the real browser: a database already created under the old v1
// schema (solutions keyed by `id`), with an old-shape record sitting
// in it (no `identification` field). Confirms db.ts's v2/v3 migration
// actually drops and recreates the store, and that reading solutions
// back afterward doesn't crash on the stale record.
//
// Run: npx tsx src/offline/dbMigration.smoketest.ts
import "fake-indexeddb/auto";
import Dexie from "dexie";

const DB_NAME = "cm_re_tutor";

// --- Step 1: simulate a browser that already has the OLD v1 database ---
console.log("=== simulate pre-existing v1 database (old schema) ===");
const oldDb = new Dexie(DB_NAME);
oldDb.version(1).stores({
  modules: "subjectId",
  solutions: "id, subjectId",
});
await oldDb.open();
await oldDb.table("solutions").put({
  id: "sol-linear-eq-1",
  subjectId: "algebra1",
  title: "Solving 2x + 3 = 11",
  steps: [{ type: "step", html: "<p>old shape, no identification field</p>" }],
});
console.log("old-shape record count:", await oldDb.table("solutions").count());
oldDb.close();

// --- Step 2: open with the real db.ts, which declares v2 (drop) + v3 (recreate) ---
console.log("\n=== open with the app's real db.ts (v1->v2->v3 migration) ===");
const { db } = await import("./db");
await db.open();

const solutions = await db.solutions.toArray();
console.log("solutions after migration:", solutions);
if (solutions.length !== 0) {
  throw new Error(
    `expected the old-shape record to be gone after the v2 drop/v3 recreate, found ${solutions.length}`
  );
}

// --- Step 3: confirm a real NEW-shape solution can now be stored and read back without crashing ---
console.log("\n=== store + read back a real new-shape solution ===");
await db.solutions.put({
  pid: "sol-linear-eq-1",
  subjectId: "algebra1",
  version: "2.0",
  identification: { book: "algebra1", chapter: "1", section: "1", set: "practice", problemNumber: "1" },
  statement: "<p>Solve for x: 2x + 3 = 11</p>",
  steps: [{ role: "step", content: "<p>x = 4</p>" }],
});
const after = await db.solutions.toArray();
console.log("solutions after real put:", after);
// This is the exact access that crashed in the browser — confirm it no longer throws.
const label = `${after[0].identification.set} — Problem ${after[0].identification.problemNumber}`;
console.log("label (would have crashed before the fix):", label);

console.log("\nDB MIGRATION SMOKE TEST PASSED");
