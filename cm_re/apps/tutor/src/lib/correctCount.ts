import { useEffect, useState } from "react";

/**
 * A running lifetime tally of every question the student has answered
 * correctly. Persisted in localStorage so it survives browser restarts;
 * per-device, never synced. Wrapped in try/catch throughout — private
 * mode / disabled storage just means it doesn't stick (reads as 0).
 *
 * Counted exactly once per answer, at grading time:
 *   - practice / custom-lesson: QuestionView.submit() on a right answer
 *   - a practice test: PracticeTest.onFinish() adds the test's score
 * Never in review mode (that's replaying an already-counted answer).
 */

const KEY = "cm_re.correctTotal";
const EVENT = "cm_re:correct-total";

export function getCorrectTotal(): number {
  try {
    const n = parseInt(localStorage.getItem(KEY) ?? "0", 10);
    return Number.isFinite(n) && n > 0 ? n : 0;
  } catch {
    return 0;
  }
}

/** Add `by` (default 1) to the tally; returns the new total. No-op for by <= 0. */
export function bumpCorrectTotal(by = 1): number {
  if (by <= 0) return getCorrectTotal();
  const next = getCorrectTotal() + Math.floor(by);
  try {
    localStorage.setItem(KEY, String(next));
  } catch {
    /* not sticky this session — still report the new value */
  }
  try {
    window.dispatchEvent(new CustomEvent(EVENT, { detail: next }));
  } catch {
    /* no window (tests/SSR) — fine */
  }
  return next;
}

export function resetCorrectTotal(): void {
  try {
    localStorage.removeItem(KEY);
  } catch {
    /* ignore */
  }
  try {
    window.dispatchEvent(new CustomEvent(EVENT, { detail: 0 }));
  } catch {
    /* ignore */
  }
}

/** Live-updating view of the tally for a header badge etc. */
export function useCorrectTotal(): number {
  const [n, setN] = useState(getCorrectTotal);
  useEffect(() => {
    const refresh = () => setN(getCorrectTotal());
    window.addEventListener(EVENT, refresh); // same tab
    window.addEventListener("storage", refresh); // other tabs
    return () => {
      window.removeEventListener(EVENT, refresh);
      window.removeEventListener("storage", refresh);
    };
  }, []);
  return n;
}
