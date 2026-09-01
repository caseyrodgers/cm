import type { ModuleManifest, ModuleBundle, SubjectSummary } from "@cm_re/shared-types";

/**
 * Thin fetch wrapper. Every call here should be optional at runtime —
 * tutor must function with this entirely unreachable once modules are
 * downloaded.
 *
 * First pass: modules are static JSON files, not a dynamic backend
 * endpoint. `apps/backend`'s ModuleController (still a stub) is where
 * this would eventually point once the backend actually publishes
 * bundles; the fetch shape below is deliberately the same either way,
 * so swapping API_BASE for a real backend URL later is a one-line change.
 */
const API_BASE = "/modules";

export async function listSubjects(): Promise<SubjectSummary[]> {
  const res = await fetch(`${API_BASE}/index.json`);
  if (!res.ok) {
    throw new Error(`subject list fetch failed: ${res.status}`);
  }
  return res.json();
}

export async function getModuleManifest(subjectId: string): Promise<ModuleManifest> {
  const res = await fetch(`${API_BASE}/${subjectId}/manifest.json`);
  if (!res.ok) {
    throw new Error(`manifest fetch failed for ${subjectId}: ${res.status}`);
  }
  return res.json();
}

export async function getModuleBundle(subjectId: string): Promise<ModuleBundle> {
  const res = await fetch(`${API_BASE}/${subjectId}/bundle.json`);
  if (!res.ok) {
    throw new Error(`bundle fetch failed for ${subjectId}: ${res.status}`);
  }
  return res.json();
}
