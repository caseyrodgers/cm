import { useEffect, useState } from "react";
import { getStudentStats, resetStats, type StudentStats } from "../../lib/studentStats";
import { activeShellId, setShell } from "../../lib/shell";
import { confirm } from "../../lib/dialog";
import { SHELL_IDS } from "../../shells";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";

/**
 * The `#/me` screen — everything this browser knows about the current
 * student (there are no accounts; it's all localStorage + IndexedDB),
 * plus a reset. Also the dev-time shell switcher.
 */

function mb(bytes: number): string {
  return bytes >= 1024 * 1024 ? `${(bytes / 1024 / 1024).toFixed(1)} MB` : `${Math.round(bytes / 1024)} KB`;
}

export default function StudentStatus() {
  const [stats, setStats] = useState<StudentStats | null>(null);
  const [busy, setBusy] = useState(false);

  const load = () => getStudentStats().then(setStats);
  useEffect(() => {
    load();
  }, []);

  async function onReset() {
    const ok = await confirm({
      title: "Reset my progress",
      message:
        "This clears your correct-answer count, every practice test, and every whiteboard. Downloaded subjects stay.",
      confirmLabel: "Reset",
      danger: true,
    });
    if (!ok) return;
    setBusy(true);
    try {
      await resetStats();
      await load();
    } finally {
      setBusy(false);
    }
  }

  if (!stats) return <Spinner />;

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 gap-2">
        <Stat label="Correct answers" value={String(stats.correctTotal)} />
        <Stat label="Grade" value={stats.grade ? `${stats.grade}` : "—"} />
        <Stat label="Downloads" value={stats.downloads.count ? `${stats.downloads.count} · ${mb(stats.downloads.approxSizeBytes)}` : "none"} />
        <Stat label="Whiteboards" value={String(stats.whiteboardCount)} />
      </div>

      <Card>
        <CardContent>
          <p className="mb-2 text-sm font-semibold text-slate-900">Subjects</p>
          {stats.subjects.length === 0 ? (
            <p className="text-sm text-slate-500">Nothing yet — take a practice test or open some problems.</p>
          ) : (
            <ul className="divide-y divide-slate-100 text-sm">
              {stats.subjects.map((s) => (
                <li key={s.subjectId} className="flex items-center gap-2 py-2">
                  <button
                    className="flex-1 text-left text-slate-800 hover:underline"
                    onClick={() => navigate(hashFor.module(s.subjectId))}
                  >
                    {s.title}
                  </button>
                  {s.test && (
                    <span className="text-xs text-slate-500">
                      {s.test.finished
                        ? `${s.test.title}: ${s.test.correct}/${s.test.total}`
                        : `${s.test.title}: ${s.test.answered}/${s.test.total} answered`}
                    </span>
                  )}
                  <span
                    className={
                      "rounded-full px-2 py-0.5 text-xs font-medium " +
                      (s.installed ? "bg-green-100 text-green-800" : "bg-slate-100 text-slate-500")
                    }
                  >
                    {s.installed ? "downloaded" : "not downloaded"}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </CardContent>
      </Card>

      <Button variant="outline" className="w-full" onClick={onReset} disabled={busy}>
        {busy ? <Spinner /> : "Reset my progress"}
      </Button>

      <details className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm">
        <summary className="cursor-pointer text-slate-500">Appearance (experimental)</summary>
        <div className="mt-2 flex flex-wrap gap-2">
          {SHELL_IDS.map((id) => (
            <button
              key={id}
              onClick={() => setShell(id)}
              aria-current={activeShellId() === id ? "true" : undefined}
              className={
                "rounded-md border px-3 py-1.5 " +
                (activeShellId() === id
                  ? "border-slate-900 bg-slate-900 text-white"
                  : "border-slate-300 text-slate-700 hover:bg-slate-50")
              }
            >
              {id}
            </button>
          ))}
        </div>
        <p className="mt-2 text-xs text-slate-400">
          Also selectable with <code>?shell=&lt;id&gt;</code> in the URL.
        </p>
      </details>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-3">
      <p className="text-xs text-slate-500">{label}</p>
      <p className="mt-0.5 text-lg font-semibold text-slate-900">{value}</p>
    </div>
  );
}
