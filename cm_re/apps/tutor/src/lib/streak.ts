import { useEffect, useState } from "react";

const STREAK_KEY = "cm_re.streak.current";
const LAST_DATE_KEY = "cm_re.streak.lastActiveDate";
const EVENT = "cm_re:streak";

function todayStr(): string {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

function yesterdayStr(): string {
  const d = new Date();
  d.setDate(d.getDate() - 1);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

function readInt(key: string): number {
  try {
    const n = parseInt(localStorage.getItem(key) ?? "0", 10);
    return Number.isFinite(n) && n > 0 ? n : 0;
  } catch {
    return 0;
  }
}

function readStr(key: string): string {
  try {
    return localStorage.getItem(key) ?? "";
  } catch {
    return "";
  }
}

export function getStreak(): number {
  const last = readStr(LAST_DATE_KEY);
  const streak = readInt(STREAK_KEY);
  if (!last || streak === 0) return 0;
  const today = todayStr();
  if (last === today || last === yesterdayStr()) return streak;
  return 0;
}

export function updateStreak(): number {
  const today = todayStr();
  const last = readStr(LAST_DATE_KEY);

  if (last === today) return readInt(STREAK_KEY);

  let next: number;
  if (last === yesterdayStr()) {
    next = readInt(STREAK_KEY) + 1;
  } else {
    next = 1;
  }

  try {
    localStorage.setItem(STREAK_KEY, String(next));
    localStorage.setItem(LAST_DATE_KEY, today);
  } catch {
    /* not sticky */
  }
  try {
    window.dispatchEvent(new CustomEvent(EVENT, { detail: next }));
  } catch {
    /* no window */
  }
  return next;
}

export function resetStreak(): void {
  try {
    localStorage.removeItem(STREAK_KEY);
    localStorage.removeItem(LAST_DATE_KEY);
  } catch {
    /* ignore */
  }
  try {
    window.dispatchEvent(new CustomEvent(EVENT, { detail: 0 }));
  } catch {
    /* ignore */
  }
}

export function useStreak(): number {
  const [n, setN] = useState(getStreak);
  useEffect(() => {
    const refresh = () => setN(getStreak());
    window.addEventListener(EVENT, refresh);
    window.addEventListener("storage", refresh);
    return () => {
      window.removeEventListener(EVENT, refresh);
      window.removeEventListener("storage", refresh);
    };
  }, []);
  return n;
}
