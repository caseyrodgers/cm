// Not part of the app bundle — a one-off runtime smoke test proving
// moduleManager.ts actually reads/writes real IndexedDB (via
// fake-indexeddb in Node) end to end, not just that it type-checks.
// Run: npx tsx src/offline/moduleManager.smoketest.ts
// Requires `make run` (or `npm run preview`) already serving dist/ on :5173.
import "fake-indexeddb/auto";

const BASE = "http://localhost:5173";
const realFetch = fetch;
// @ts-expect-error — overriding global fetch so client.ts's relative
// "/modules/..." paths resolve against the locally running server,
// same as a browser would resolve them against its own origin.
globalThis.fetch = (input: string, init?: RequestInit) => realFetch(new URL(input, BASE), init);

const { checkForUpdate, downloadModule, isModuleInstalled, getSolutionsForModule, removeModule } =
  await import("./moduleManager");
const { listSubjects } = await import("../api/client");

const subjects = await listSubjects();
console.log(
  "subjects from index.json:",
  subjects.map((s) => s.subjectId)
);
if (subjects.length !== 3) {
  throw new Error(`expected 3 subjects in index.json, got ${subjects.length}`);
}

for (const { subjectId, expectedSolutionCount } of [
  { subjectId: "algebra1", expectedSolutionCount: 3 },
  { subjectId: "geometry", expectedSolutionCount: 1 },
  { subjectId: "alg1ptests", expectedSolutionCount: 846 },
]) {
  console.log(`--- ${subjectId} ---`);
  console.log("installed before download:", await isModuleInstalled(subjectId));
  console.log("update available:", await checkForUpdate(subjectId));

  await downloadModule(subjectId);

  console.log("installed after download:", await isModuleInstalled(subjectId));
  console.log("update available after install:", await checkForUpdate(subjectId));

  const solutions = await getSolutionsForModule(subjectId);
  const labels = solutions.map((s) => `${s.pid} (${s.identification.set} #${s.identification.problemNumber})`);
  console.log(
    "solutions read back from IndexedDB:",
    labels.length <= 5 ? labels : [...labels.slice(0, 3), `... (${labels.length} total)`]
  );

  if (solutions.length !== expectedSolutionCount) {
    throw new Error(
      `${subjectId}: expected ${expectedSolutionCount} solutions, got ${solutions.length}`
    );
  }

  await removeModule(subjectId);
  console.log("installed after remove:", await isModuleInstalled(subjectId));
  const solutionsAfterRemove = await getSolutionsForModule(subjectId);
  if (await isModuleInstalled(subjectId)) {
    throw new Error(`${subjectId}: expected not installed after removeModule()`);
  }
  if (solutionsAfterRemove.length !== 0) {
    throw new Error(
      `${subjectId}: expected 0 solutions after removeModule(), got ${solutionsAfterRemove.length}`
    );
  }
}
console.log("SMOKE TEST PASSED");
