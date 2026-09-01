import type { Solution } from "@cm_re/shared-types";
import { db, type InstalledModule } from "./db";
import { getModuleManifest, getModuleBundle } from "../api/client";

/**
 * Module download/install logic (NEW_DIRECTION.org "Offline support").
 * The prefetch unit is the module (one per subject), not the individual
 * solution — see shared-types' ModuleManifest/ModuleBundle.
 */

/** Compares the locally-installed manifest's version against the server's, without downloading the (larger) bundle. */
export async function checkForUpdate(subjectId: string): Promise<boolean> {
  const remote = await getModuleManifest(subjectId);
  const local = await db.modules.get(subjectId);
  return !local || local.version !== remote.version;
}

export async function isModuleInstalled(subjectId: string): Promise<boolean> {
  return (await db.modules.get(subjectId)) !== undefined;
}

export async function getInstalledModules(): Promise<InstalledModule[]> {
  return db.modules.toArray();
}

export async function getSolutionsForModule(subjectId: string): Promise<Solution[]> {
  return db.solutions.where("subjectId").equals(subjectId).toArray();
}

/**
 * One solution by its pid, from whichever installed module contains it —
 * `db.solutions` is keyed by pid, so this is a direct primary-key
 * lookup. Backs URL deep-linking (`#/s/<pid>`): the pid is globally
 * unique and the returned Solution carries its own `subjectId`, so the
 * caller doesn't need to know which module it's in. `undefined` when
 * that solution's module isn't installed.
 */
export async function getSolution(pid: string): Promise<Solution | undefined> {
  return db.solutions.get(pid);
}

/**
 * Downloads a module's full bundle and writes it into IndexedDB. Requests
 * persistent storage first — see NEW_DIRECTION.org's eviction caveat:
 * without this, the browser can silently evict cached module content
 * under storage pressure, which would be very confusing offline.
 */
export async function downloadModule(subjectId: string): Promise<void> {
  if (navigator.storage?.persist) {
    await navigator.storage.persist();
  }

  // manifest.json is the single source of truth for version/solutionIds/
  // size — fetched here directly rather than trusting a copy embedded in
  // the bundle (an earlier version did that; the two copies could drift).
  const [manifest, bundle] = await Promise.all([
    getModuleManifest(subjectId),
    getModuleBundle(subjectId),
  ]);

  await db.transaction("rw", db.modules, db.solutions, async () => {
    for (const solution of bundle.solutions) {
      await db.solutions.put(solution);
    }
    await db.modules.put({ ...manifest, installedAt: Date.now() });
  });
}

/**
 * Deletes a module and every solution belonging to it from IndexedDB —
 * frees the storage back up. There's no server-side counterpart to
 * this: removal only ever affects the local cache, never the
 * published content itself.
 */
export async function removeModule(subjectId: string): Promise<void> {
  await db.transaction("rw", db.modules, db.solutions, async () => {
    await db.solutions.where("subjectId").equals(subjectId).delete();
    await db.modules.delete(subjectId);
  });
}
