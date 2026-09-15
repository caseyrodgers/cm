import { db, type ChapterStat, type TestAnswer } from "../offline/db";
import { chapterOf } from "./problemOrder";

/**
 * Per-chapter mastery, derived from the lifetime ChapterStat tallies —
 * "how's this student doing on Chapter 10", not just "how'd they do on
 * the last test". See IDEAS.org "Per-chapter mastery view".
 */
export interface ChapterMastery {
  key: string;
  label: string;
  correct: number;
  answered: number;
  /** 0-100, rounded. 0 when answered === 0 (no data yet, not "0% mastery"). */
  pct: number;
}

function statId(subjectId: string, chapterKey: string): string {
  return `${subjectId}::${chapterKey}`;
}

/**
 * Adds one finished test's per-chapter correct/answered counts onto the
 * running lifetime tally. Idempotency is the caller's job — call exactly
 * once per finished test, same guard PracticeTest.onFinish() already
 * uses for bumpCorrectTotal/bumpAnsweredTotal. Unscored answers
 * (a.correct === null — the source solution marked no choice correct)
 * and unanswered problems don't count either way.
 */
export async function recordChapterResults(
  subjectId: string,
  pids: readonly string[],
  answers: Record<string, TestAnswer>
): Promise<void> {
  const deltas = new Map<string, { label: string; correct: number; answered: number }>();
  for (const pid of pids) {
    const a = answers[pid];
    if (!a || a.correct === null) continue;
    const ch = chapterOf(pid, subjectId);
    const d = deltas.get(ch.key) ?? { label: ch.label, correct: 0, answered: 0 };
    d.answered++;
    if (a.correct) d.correct++;
    deltas.set(ch.key, d);
  }
  if (deltas.size === 0) return;

  await db.transaction("rw", db.chapterStats, async () => {
    for (const [chapterKey, d] of deltas) {
      const id = statId(subjectId, chapterKey);
      const existing = await db.chapterStats.get(id);
      const next: ChapterStat = {
        id,
        subjectId,
        chapterKey,
        label: d.label,
        correct: (existing?.correct ?? 0) + d.correct,
        answered: (existing?.answered ?? 0) + d.answered,
        updatedAt: Date.now(),
      };
      await db.chapterStats.put(next);
    }
  });
}

/**
 * This subject's chapter mastery, worst-first (lowest % first, tied
 * broken by chapter key) — the natural "focus here next" order.
 * Chapters with no scored answers yet just don't appear.
 */
export async function getChapterMastery(subjectId: string): Promise<ChapterMastery[]> {
  const rows = await db.chapterStats.where("subjectId").equals(subjectId).toArray();
  return rows
    .filter((r) => r.answered > 0)
    .map((r) => ({
      key: r.chapterKey,
      label: r.label,
      correct: r.correct,
      answered: r.answered,
      pct: Math.round((r.correct / r.answered) * 100),
    }))
    .sort((a, b) => a.pct - b.pct || a.key.localeCompare(b.key));
}

/** Wipes every subject's chapter mastery — part of studentStats.resetStats(). */
export async function resetChapterMastery(): Promise<void> {
  await db.chapterStats.clear();
}
