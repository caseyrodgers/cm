// Not part of the app bundle — verifies the "check on open" update
// mechanism actually propagates a republished module to an
// already-installed client, not just that checkForUpdate() type-checks.
//
// Scenario: student already has algebra1 installed at whatever version
// is currently in the fixture. An author edits a solution and
// republishes — manifest.json bumps to a new version, bundle.json's
// content changes. On next check-on-open, the client should see the
// update, and downloadModule() should overwrite the stale content.
//
// Reads the current fixture state rather than assuming a specific
// version/solution count, so it stays valid as the fixture evolves —
// manifest.json is the single source of truth for version/solutionIds
// (see shared-types' ModuleBundle doc comment for why bundle.json no
// longer embeds its own copy).
//
// Run: npx tsx src/offline/moduleUpdate.smoketest.ts
// Requires `npm run preview` (or `make run`) already serving dist/ on :5173.
import "fake-indexeddb/auto";
import { writeFileSync, readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";

const BASE = "http://localhost:5173";
const realFetch = fetch;
// @ts-expect-error — same origin-resolution shim as moduleManager.smoketest.ts
globalThis.fetch = (input: string, init?: RequestInit) => realFetch(new URL(input, BASE), init);

const { checkForUpdate, downloadModule, getSolutionsForModule } = await import("./moduleManager");
const { db } = await import("./db");

const subjectId = "algebra1";
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const distModuleDir = path.resolve(__dirname, "../../dist/modules", subjectId);
const manifestPath = path.join(distModuleDir, "manifest.json");
const bundlePath = path.join(distModuleDir, "bundle.json");

// --- Step 1: student installs whatever's currently published ---
console.log("=== install current fixture version ===");
await downloadModule(subjectId);
const before = await db.modules.get(subjectId);
const beforeSolutions = await getSolutionsForModule(subjectId);
console.log("installed version:", before?.version);
console.log("solution pids:", beforeSolutions.map((s) => s.pid));
if (!before) {
  throw new Error("expected a module to be installed after downloadModule()");
}

// --- Step 2: author edits + republishes — server's static files change under the client ---
console.log("\n=== author republishes ===");
const originalManifest = readFileSync(manifestPath, "utf-8");
const originalBundle = readFileSync(bundlePath, "utf-8");
const parsedOriginalManifest = JSON.parse(originalManifest);
const originalSolutions = JSON.parse(originalBundle).solutions;

const newVersion = `${before.version}-smoketest`;
const newManifest = { ...parsedOriginalManifest, version: newVersion };
const revisedFirstSolution = {
  ...originalSolutions[0],
  statement: `${originalSolutions[0].statement} <!-- revised wording -->`,
};
const newBundle = { solutions: [revisedFirstSolution, ...originalSolutions.slice(1)] };

writeFileSync(manifestPath, JSON.stringify(newManifest, null, 2));
writeFileSync(bundlePath, JSON.stringify(newBundle, null, 2));

try {
  // --- Step 3: check-on-open should now see an update ---
  const hasUpdate = await checkForUpdate(subjectId);
  console.log("update available:", hasUpdate);
  if (!hasUpdate) {
    throw new Error("expected checkForUpdate to report true after republish");
  }

  // --- Step 4: applying the update should overwrite the stale content ---
  await downloadModule(subjectId);
  const after = await db.modules.get(subjectId);
  const afterSolutions = await getSolutionsForModule(subjectId);
  console.log("installed version after update:", after?.version);
  console.log("solution pids after update:", afterSolutions.map((s) => s.pid));

  if (after?.version !== newVersion) {
    throw new Error(`expected version=${newVersion}, got version=${after?.version}`);
  }
  if (afterSolutions.length !== originalSolutions.length) {
    throw new Error(
      `expected ${originalSolutions.length} solutions after update, got ${afterSolutions.length}`
    );
  }
  if (!afterSolutions.some((s) => s.statement.includes("revised wording"))) {
    throw new Error("expected the revised statement to have overwritten the stale one");
  }

  console.log("\nUPDATE-FLOW SMOKE TEST PASSED");
} finally {
  // Restore the fixture files so the app's default demo state stays clean.
  writeFileSync(manifestPath, originalManifest);
  writeFileSync(bundlePath, originalBundle);
  console.log(`(restored dist/modules/${subjectId} fixture files to original content)`);
}
