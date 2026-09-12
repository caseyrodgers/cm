import { useEffect, useState } from "react";
import { getInstalledSummary, type InstalledSummary } from "../../lib/studentStats";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";

/**
 * The landing screen. Deliberately thin — it points at the two ways
 * into the content (a practice test, or browsing problems) and shows
 * what's actually on this device (installed subjects/problems). The
 * student's own performance stats (Problems viewed, Questions
 * answered, Correct) live on #/me instead — see StudentStatus. What
 * the header/nav looks like is the shell's job (see shells/).
 */

const TILES: { label: string; sub: string; to: string }[] = [
  { label: "Practice Tests", sub: "Take a timed set — quick, whole-subject, or by chapter.", to: hashFor.tests() },
  { label: "Problems", sub: "Browse every worked solution, grouped by chapter.", to: hashFor.problems() },
  { label: "Me", sub: "Your progress so far — and a way to start over.", to: hashFor.me() },
];

export default function Hub() {
  const [summary, setSummary] = useState<InstalledSummary | null>(null);

  useEffect(() => {
    let cancelled = false;
    getInstalledSummary().then((s) => {
      if (!cancelled) setSummary(s);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 gap-3">
        <div className="rounded-lg border border-slate-200 bg-white p-5 text-center">
          <p className="text-sm text-slate-500">Installed Problems</p>
          <p className="mt-1 text-4xl font-extrabold text-slate-900" data-testid="installed-problem-count">
            {summary ? summary.problemCount : "—"}
          </p>
        </div>
        <div className="rounded-lg border border-slate-200 bg-white p-5 text-center">
          <p className="text-sm text-slate-500">Subjects</p>
          <p className="mt-1 text-4xl font-extrabold text-slate-900" data-testid="installed-subject-count">
            {summary ? summary.subjectCount : "—"}
          </p>
        </div>
      </div>

      <div className="space-y-2">
        {TILES.map((t) => (
          <Card key={t.to}>
            <CardContent>
              <button className="w-full text-left" onClick={() => navigate(t.to)}>
                <span className="flex items-center justify-between">
                  <span className="text-base font-semibold text-slate-900">{t.label}</span>
                  <span aria-hidden className="text-slate-400">
                    &rsaquo;
                  </span>
                </span>
                <span className="mt-1 block text-sm text-slate-500">{t.sub}</span>
              </button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
