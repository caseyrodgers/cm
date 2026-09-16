import { useEffect, useState } from "react";
import { getSubjectDownloadStats, formatSize, type SubjectDownloadStat } from "../../lib/studentStats";
import { subjectIcon } from "../../lib/subjectIcon";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";
import { Spinner } from "../ui/spinner";

/**
 * The landing screen. Deliberately thin — it points at the two ways
 * into the content (a practice test, or browsing problems), and lists
 * every subject in the catalog with its download status/size (moved
 * here from #/me — see IDEAS.org "Change Hub to contain All the
 * subjects and their download stats"). The student's own performance
 * stats (Problems viewed, Questions answered, Correct, per-subject
 * test/chapter progress) live on #/me instead — see StudentStatus.
 * What the header/nav looks like is the shell's job (see shells/).
 */

const TILES: { label: string; sub: string; to: string }[] = [
  { label: "Practice Tests", sub: "Take a timed set — quick, whole-subject, or by chapter.", to: hashFor.tests() },
  { label: "Problems", sub: "Browse every worked solution, grouped by chapter.", to: hashFor.problems() },
  { label: "Me", sub: "Your progress so far — and a way to start over.", to: hashFor.me() },
];

export default function Hub() {
  const [subjects, setSubjects] = useState<SubjectDownloadStat[] | null>(null);

  useEffect(() => {
    let cancelled = false;
    getSubjectDownloadStats().then((s) => {
      if (!cancelled) setSubjects(s);
    });
    return () => {
      cancelled = true;
    };
  }, []);

  const installedCount = subjects?.filter((s) => s.installed).length ?? 0;

  return (
    <div className="space-y-4">
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

      <div>
        <div className="mb-2 flex items-baseline justify-between">
          <p className="text-sm font-semibold text-slate-900">Subjects</p>
          {subjects && (
            <span className="text-xs text-slate-500" data-testid="installed-subject-count">
              {installedCount} of {subjects.length} downloaded
            </span>
          )}
        </div>
        {!subjects ? (
          <Spinner />
        ) : (
          <div className="grid grid-cols-4 gap-2">
            {subjects.map((s) => (
              <button
                key={s.subjectId}
                onClick={() => navigate(hashFor.module(s.subjectId))}
                title={
                  s.title +
                  " — " +
                  (s.installed ? "downloaded" : "not downloaded") +
                  (s.approxSizeBytes != null ? ` · ${formatSize(s.approxSizeBytes)}` : "")
                }
                className="relative flex aspect-[4/5] flex-col items-start justify-start gap-0.5 rounded-lg border border-slate-200 bg-white p-1.5 text-left shadow-sm transition-colors hover:border-slate-300 hover:bg-slate-50"
              >
                <span
                  className={
                    "absolute right-1 top-1 h-1.5 w-1.5 rounded-full " +
                    (s.installed ? "bg-green-500" : "bg-slate-300")
                  }
                  aria-hidden
                />
                <span className="line-clamp-2 text-xs font-bold leading-tight text-slate-800">
                  {s.title}
                </span>
                <span aria-hidden className="flex flex-1 w-full items-center justify-center text-7xl leading-none">
                  {subjectIcon(s.subjectId)}
                </span>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
