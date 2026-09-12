import { useEffect, useState } from "react";

/**
 * Three running lifetime tallies, same shape: every question answered
 * correctly, every question answered at all (right or wrong — the
 * denominator for the first), and every problem viewed (opened in the
 * full statement+steps view, whether or not it's ever answered).
 * Persisted in localStorage so they survive browser restarts;
 * per-device, never synced. Wrapped in try/catch throughout — private
 * mode / disabled storage just means they don't stick (reads as 0).
 *
 * Counted exactly once per event:
 *   - practice / custom-lesson: QuestionView.submit() — answered bumps
 *     on every submit, correct bumps only when right
 *   - a practice test: PracticeTest.onFinish() adds the test's answered
 *     count and correct count together
 *   - viewed bumps once per distinct pid shown in SolutionNav (the
 *     standalone #/s/ view and the missed-questions-lesson walkthrough
 *     — not the in-test question view, which QuestionView/PracticeTest
 *     already cover via answered/correct)
 * Never in review mode (that's replaying an already-counted answer).
 */

interface Tally {
  get(): number;
  /** Add `by` (default 1); returns the new total. No-op for by <= 0. */
  bump(by?: number): number;
  reset(): void;
  /** Live-updating view for a header badge / stat card etc. */
  useTotal(): number;
}

function makeTally(key: string, event: string): Tally {
  function get(): number {
    try {
      const n = parseInt(localStorage.getItem(key) ?? "0", 10);
      return Number.isFinite(n) && n > 0 ? n : 0;
    } catch {
      return 0;
    }
  }

  function bump(by = 1): number {
    if (by <= 0) return get();
    const next = get() + Math.floor(by);
    try {
      localStorage.setItem(key, String(next));
    } catch {
      /* not sticky this session — still report the new value */
    }
    try {
      window.dispatchEvent(new CustomEvent(event, { detail: next }));
    } catch {
      /* no window (tests/SSR) — fine */
    }
    return next;
  }

  function reset(): void {
    try {
      localStorage.removeItem(key);
    } catch {
      /* ignore */
    }
    try {
      window.dispatchEvent(new CustomEvent(event, { detail: 0 }));
    } catch {
      /* ignore */
    }
  }

  function useTotal(): number {
    const [n, setN] = useState(get);
    useEffect(() => {
      const refresh = () => setN(get());
      window.addEventListener(event, refresh); // same tab
      window.addEventListener("storage", refresh); // other tabs
      return () => {
        window.removeEventListener(event, refresh);
        window.removeEventListener("storage", refresh);
      };
    }, []);
    return n;
  }

  return { get, bump, reset, useTotal };
}

const correctTally = makeTally("cm_re.correctTotal", "cm_re:correct-total");
const answeredTally = makeTally("cm_re.answeredTotal", "cm_re:answered-total");

export const getCorrectTotal = correctTally.get;
export const bumpCorrectTotal = correctTally.bump;
export const resetCorrectTotal = correctTally.reset;
export const useCorrectTotal = correctTally.useTotal;

export const getAnsweredTotal = answeredTally.get;
export const bumpAnsweredTotal = answeredTally.bump;
export const resetAnsweredTotal = answeredTally.reset;
export const useAnsweredTotal = answeredTally.useTotal;

const viewedTally = makeTally("cm_re.viewedTotal", "cm_re:viewed-total");

export const getViewedTotal = viewedTally.get;
export const bumpViewedTotal = viewedTally.bump;
export const resetViewedTotal = viewedTally.reset;
export const useViewedTotal = viewedTally.useTotal;
