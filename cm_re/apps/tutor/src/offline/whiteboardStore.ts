import { db, type Stroke, type Whiteboard } from "./db";

/**
 * Per-solution whiteboard persistence. IndexedDB only — this module
 * deliberately does not import api/client.ts and has no network path.
 * The student's scratch work stays on their device; it is never sent
 * to the server. One board per solution, keyed by pid.
 */

export type { Stroke, Whiteboard };

/** The board for one solution, or `undefined` if nothing's been drawn yet. */
export async function getWhiteboard(pid: string): Promise<Whiteboard | undefined> {
  return db.whiteboards.get(pid);
}

/**
 * Overwrites the board for one solution. Callers debounce this (see
 * WhiteboardPanel) — it's a full replace, not an append, so the last
 * write wins and there's no partial-state to reconcile.
 * `revealedSegments` is how many step-bands are currently unlocked
 * (see Whiteboard) — persisted so reopening restores the same board
 * height before the student re-advances through the steps.
 */
export async function saveWhiteboard(pid: string, strokes: Stroke[], revealedSegments: number): Promise<void> {
  await db.whiteboards.put({ pid, strokes, revealedSegments, updatedAt: Date.now() });
}

/** Removes the board entirely (Clear button). */
export async function clearWhiteboard(pid: string): Promise<void> {
  await db.whiteboards.delete(pid);
}
