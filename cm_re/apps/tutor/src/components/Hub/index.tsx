import { useEffect, useState } from "react";
import { getSubjectDownloadStats, formatSize, type SubjectDownloadStat } from "../../lib/studentStats";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";
import { List, ListItemButton } from "../ui/list";
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

      <Card>
        <CardContent>
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
            <List>
              {subjects.map((s) => (
                <ListItemButton key={s.subjectId} onClick={() => navigate(hashFor.module(s.subjectId))}>
                  <span className="text-slate-800">{s.title}</span>
                  <span className="flex shrink-0 items-center gap-2">
                    <span className="text-xs text-slate-400">
                      {s.approxSizeBytes != null ? formatSize(s.approxSizeBytes) : "—"}
                    </span>
                    <span
                      className={
                        "rounded-full px-2 py-0.5 text-xs font-medium " +
                        (s.installed ? "bg-green-100 text-green-800" : "bg-slate-100 text-slate-500")
                      }
                    >
                      {s.installed ? "downloaded" : "not downloaded"}
                    </span>
                  </span>
                </ListItemButton>
              ))}
            </List>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
