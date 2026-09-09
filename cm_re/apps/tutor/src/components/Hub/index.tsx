import { useCorrectTotal } from "../../lib/correctCount";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";

/**
 * The landing screen. Deliberately thin — it points at the two ways
 * into the content (a practice test, or browsing problems) and at the
 * student's own status. What the header/nav looks like is the shell's
 * job (see shells/).
 */

const TILES: { label: string; sub: string; to: string }[] = [
  { label: "Practice Tests", sub: "Take a timed set — quick, whole-subject, or by chapter.", to: hashFor.tests() },
  { label: "Problems", sub: "Browse every worked solution, grouped by chapter.", to: hashFor.problems() },
  { label: "Me", sub: "Your progress so far — and a way to start over.", to: hashFor.me() },
];

export default function Hub() {
  const total = useCorrectTotal();

  return (
    <div className="space-y-4">
      <div className="rounded-lg border border-slate-200 bg-white p-5 text-center">
        <p className="text-sm text-slate-500">Correct answers, all time</p>
        <p className="mt-1 text-4xl font-extrabold text-slate-900">{total}</p>
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
