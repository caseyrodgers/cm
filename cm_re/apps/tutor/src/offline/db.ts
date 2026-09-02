import Dexie, { type Table } from "dexie";
import type { ModuleManifest, Solution } from "@cm_re/shared-types";

/**
 * Dexie (IndexedDB) schema. Three tables:
 *   - modules: installed module manifests, keyed by subjectId —
 *     "is Algebra 1 installed, and at what version"
 *   - solutions: cached Solution documents, keyed by pid (the legacy
 *     TutorProblem.pid — see shared-types' Solution), indexed by
 *     subjectId so a module's solutions can be read back as a group
 *   - whiteboards: the student's scratch drawing for one solution,
 *     keyed by pid. Local only — deliberately never synced to the
 *     server (see whiteboardStore.ts). Independent of module
 *     lifecycle: a board survives a module refresh, and removeModule
 *     leaves it alone.
 *
 * IMPORTANT: bump the version number (and add a new .version().stores()
 * step) any time a table's keyPath or index list changes — Dexie only
 * applies schema changes when it sees a version number higher than
 * what's already in the browser's IndexedDB. Editing an *existing*
 * version's .stores() call in place does nothing for anyone who
 * already has the old version created: their browser keeps the old
 * object store as-is, silently, with no error.
 *
 * That's exactly what happened going from v1 (solutions keyed by
 * `id`) to the real Solution schema (keyed by `pid`) — the version
 * number here stayed at 1, so browsers that already had a v1 database
 * kept the old `id`-keyed store, and new pid-only Solution objects
 * (no `id` field at all) collided/failed against it. v2 below fixes
 * this for real: drop the old store, recreate it with the correct
 * keyPath. There's no real user data at stake yet (this is all
 * prototype/fixture content), so a clean drop-and-recreate is the
 * right call rather than writing migration logic for a shape nobody
 * needs preserved.
 */

export interface InstalledModule extends ModuleManifest {
  installedAt: number;
}

/** One freehand stroke: a colour, a width, and a flat [x0,y0,x1,y1,...] point list (flat array = smaller when serialised). */
export interface Stroke {
  color: string;
  width: number;
  points: number[];
}

/**
 * The scratch drawing for one solution — a single continuous board.
 * `pid` is the primary key (one board per solution); stepping through
 * the solution doesn't partition it, every step's work lands on the
 * same surface.
 */
export interface Whiteboard {
  pid: string;
  strokes: Stroke[];
  updatedAt: number;
}

/** One recorded answer within a practice test. */
export interface TestAnswer {
  selectedIndex: number;
  /** null when the source solution marked no choice correct (unscorable). */
  correct: boolean | null;
}

/** What kind of test this is — drives the title and "start another" flow. */
export type TestScope =
  | { kind: "random" }
  | { kind: "subject" }
  | { kind: "chapter"; chapterKey: string; label: string };

/**
 * The student's current practice test for one subject, keyed by
 * subjectId — a set of that module's MC problems (random 10, the whole
 * subject, or one chapter), the answers given so far, and (once
 * finished) the score. `pids` is stored in problem order. Local only;
 * one active test per subject at a time. Starting a new test overwrites
 * it.
 */
export interface PracticeTest {
  subjectId: string;
  pids: string[];
  answers: Record<string, TestAnswer>;
  /** Optional only for backward-compat: tests created before this field existed have none. `startTest` always sets it. */
  scope?: TestScope;
  startedAt: number;
  completedAt: number | null;
}

class CmDb extends Dexie {
  modules!: Table<InstalledModule, string>;
  solutions!: Table<Solution, string>;
  whiteboards!: Table<Whiteboard, string>;
  practiceTests!: Table<PracticeTest, string>;

  constructor() {
    super("cm_re_tutor");
    this.version(1).stores({
      modules: "subjectId",
      solutions: "id, subjectId",
    });
    this.version(2).stores({
      modules: "subjectId",
      solutions: null,
    });
    this.version(3).stores({
      modules: "subjectId",
      solutions: "pid, subjectId",
    });
    // v4: add the per-solution whiteboard store. Additive — existing
    // modules/solutions stores are unchanged, so no data migration.
    this.version(4).stores({
      modules: "subjectId",
      solutions: "pid, subjectId",
      whiteboards: "pid",
    });
    // v5: add the per-subject practice-test store. Additive.
    this.version(5).stores({
      modules: "subjectId",
      solutions: "pid, subjectId",
      whiteboards: "pid",
      practiceTests: "subjectId",
    });
  }
}

export const db = new CmDb();
