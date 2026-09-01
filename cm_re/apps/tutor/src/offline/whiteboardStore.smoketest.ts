// Throwaway check, not part of the app — proves the per-solution
// whiteboard persists to IndexedDB, is isolated per pid, survives a
// simulated reload, and clears. No server involved.
// Run: npx tsx src/offline/whiteboardStore.smoketest.ts
import "fake-indexeddb/auto";
import type { Stroke } from "./db";

function assert(cond: unknown, msg: string) {
  if (!cond) throw new Error("FAIL: " + msg);
  console.log("ok —", msg);
}

const { getWhiteboard, saveWhiteboard, clearWhiteboard } = await import("./whiteboardStore");

const pidA = "alg1ptests_1_1_chapter1practicetest_10_1";
const pidB = "alg1ptests_10_1_chapter10practicetest_10_10";
const strokesA: Stroke[] = [
  { color: "#1f2937", width: 2.5, points: [10, 10, 20, 25, 40, 30] },
  { color: "#C14444", width: 2.5, points: [100, 100, 120, 90] },
];

assert((await getWhiteboard(pidA)) === undefined, "no board before anything is drawn");

await saveWhiteboard(pidA, strokesA, 3);
const readA = await getWhiteboard(pidA);
assert(readA && readA.strokes.length === 2, "board saved and read back for pidA");
assert(readA!.strokes[0].points.length === 6, "stroke point data round-trips intact");
assert(readA!.revealedSegments === 3, "revealedSegments (step-band count) round-trips");
assert(typeof readA!.updatedAt === "number" && readA!.updatedAt > 0, "updatedAt stamped");

assert((await getWhiteboard(pidB)) === undefined, "a different solution's board is independent (still empty)");

// Simulate a reload: drop the in-memory Dexie handle and re-open.
const { db } = await import("./db");
db.close();
await db.open();
const afterReload = await getWhiteboard(pidA);
assert(afterReload && afterReload.strokes.length === 2, "board survives a reopen (real IndexedDB persistence)");
assert(afterReload!.revealedSegments === 3, "revealedSegments survives the reopen too");

await saveWhiteboard(pidA, [strokesA[0]], 5);
const replaced = await getWhiteboard(pidA);
assert(replaced!.strokes.length === 1, "save is a full replace (undo path), not an append");
assert(replaced!.revealedSegments === 5, "revealedSegments only grows in the UI, but the store itself just takes what it's given");

await clearWhiteboard(pidA);
assert((await getWhiteboard(pidA)) === undefined, "clear removes the board");

// Never-synced guarantee: whiteboardStore must not pull in the network
// client. Check for an actual import statement, not a substring — the
// file's own comment mentions "api/client.ts" to explain why it's absent.
const src = await import("node:fs").then((fs) =>
  fs.readFileSync(new URL("./whiteboardStore.ts", import.meta.url), "utf-8")
);
assert(
  !/^\s*import\b[^\n]*\bapi\/client\b/m.test(src),
  "whiteboardStore.ts has no import of api/client (local-only, never the server)"
);

console.log("\nWHITEBOARD STORE SMOKE TEST PASSED");
