import { useEffect, useMemo, useState } from "react";
import type { ChapterInfo, ModuleManifest, Solution } from "@cm_re/shared-types";
import { getModuleManifest } from "../../api/client";
import {
  downloadModule,
  isModuleInstalled,
  getSolutionsForModule,
  checkForUpdate,
  removeModule,
} from "../../offline/moduleManager";
import { getActiveTest, scoreTest, testTitle, type PracticeTest } from "../../offline/practiceTestStore";
import { Card, CardHeader, CardTitle, CardSubtitle, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";
import { List, ListItemButton } from "../ui/list";
import { solutionTitle } from "../../lib/solutionTitle";
import { chapterDisplay } from "../../lib/chapterName";
import { compareProblems, groupByChapter } from "../../lib/problemOrder";
import { navigate, hashFor } from "../../routing";

type Status = "idle" | "downloading" | "removing" | "error";

/**
 * "Download this subject for offline" UX. Shows approxSizeBytes from the
 * manifest before committing, then drives moduleManager's download flow.
 * Once installed, also reads the solutions back out of IndexedDB and
 * lists them — proving the "so the PWA can read it" half of the loop,
 * not just the write. Also checks for updates (see SubjectSelector's
 * same check) so re-visiting an installed subject offers a refresh —
 * downloadModule() overwrites by primary key either way, so "install"
 * and "update" are the same operation under the hood.
 */
export default function ModuleDownloadPrompt({
  subjectId,
  onOpenSolution,
}: {
  subjectId: string;
  onOpenSolution: (solution: Solution) => void;
}) {
  const [manifest, setManifest] = useState<ModuleManifest | null>(null);
  const [installed, setInstalled] = useState(false);
  const [hasUpdate, setHasUpdate] = useState(false);
  const [solutions, setSolutions] = useState<Solution[] | null>(null); // null = not fetched yet
  const [showAll, setShowAll] = useState(false);
  const [status, setStatus] = useState<Status>("idle");
  const [activeTest, setActiveTest] = useState<PracticeTest | null>(null);

  useEffect(() => {
    getModuleManifest(subjectId)
      .then(setManifest)
      .catch(() => setStatus("error"));
    isModuleInstalled(subjectId).then(setInstalled);
  }, [subjectId]);

  useEffect(() => {
    if (installed) {
      checkForUpdate(subjectId)
        .then(setHasUpdate)
        .catch(() => setHasUpdate(false));
      getActiveTest(subjectId).then((t) => setActiveTest(t ?? null));
    }
  }, [installed, subjectId]);

  // The full solution list (all 846, for the real subject) is only
  // fetched once the student actually asks to see it — the practice
  // test is the default path and doesn't need it.
  useEffect(() => {
    if (installed && showAll && solutions === null) {
      getSolutionsForModule(subjectId).then((list) =>
        setSolutions([...list].sort((a, b) => compareProblems(a.pid, b.pid, subjectId)))
      );
    }
  }, [installed, showAll, solutions, subjectId]);

  async function handleDownload() {
    setStatus("downloading");
    try {
      await downloadModule(subjectId);
      setInstalled(true);
      setHasUpdate(false);
      setSolutions(null); // re-fetch (the update may have changed content) if/when the list is shown again
      setStatus("idle");
    } catch {
      setStatus("error");
    }
  }

  async function handleRemove() {
    if (!confirm(`Remove "${subjectId}"? You'll need a connection to download it again.`)) {
      return;
    }
    setStatus("removing");
    try {
      await removeModule(subjectId);
      setInstalled(false);
      setHasUpdate(false);
      setSolutions(null);
      setShowAll(false);
      setActiveTest(null);
      setStatus("idle");
    } catch {
      setStatus("error");
    }
  }

  if (!manifest) {
    return <Spinner />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{subjectId}</CardTitle>
        <CardSubtitle>
          {manifest.solutionIds.length} solutions &middot; {(manifest.approxSizeBytes / 1024).toFixed(0)} KB
        </CardSubtitle>
      </CardHeader>

      <CardContent>
        {installed ? (
          <>
            <p className="mb-3 text-sm font-medium text-green-700">&#10003; Installed for offline use.</p>

            {/* Two ways into a subject's problems: a practice test (the
                default path), or the complete list on demand. */}
            <div className="mb-3 space-y-2">
              <div>
                <Button className="w-full" onClick={() => navigate(hashFor.test(subjectId))}>
                  {practiceTestCta(activeTest)} &rarr;
                </Button>
                <p className="mt-1 rounded-md bg-slate-50 px-3 py-1.5 text-xs text-slate-500">
                  {practiceTestContext(activeTest)}
                </p>
              </div>
              <Button variant="outline" className="w-full" onClick={() => setShowAll((v) => !v)}>
                {showAll ? "Hide full problem list" : `Show all ${manifest.solutionIds.length} problems`}
              </Button>
            </div>

            {showAll &&
              (solutions === null ? (
                <Spinner />
              ) : (
                <ChapterList
                  solutions={solutions}
                  subjectId={subjectId}
                  chapters={manifest.chapters}
                  onOpenSolution={onOpenSolution}
                />
              ))}

            {hasUpdate && (
              <Button className="mt-3 w-full" onClick={handleDownload} disabled={status === "downloading"}>
                {status === "downloading" ? <Spinner /> : "Update available — tap to update"}
              </Button>
            )}
            <Button
              variant="outline"
              className="mt-2 w-full"
              onClick={handleRemove}
              disabled={status === "downloading" || status === "removing"}
            >
              {status === "removing" ? <Spinner /> : "Remove download"}
            </Button>
          </>
        ) : (
          <Button className="w-full" onClick={handleDownload} disabled={status === "downloading"}>
            {status === "downloading" ? <Spinner /> : "Download for offline"}
          </Button>
        )}

        {status === "error" && <p className="mt-2 text-sm text-red-600">Something went wrong. Try again.</p>}
      </CardContent>
    </Card>
  );
}

/** Button label — reflects whether there's a test to resume / a score to see. */
function practiceTestCta(t: PracticeTest | null): string {
  if (!t) return "Take a practice test";
  if (t.scope?.kind === "custom") return "Resume your lesson";
  if (t.completedAt != null) return "See your last score";
  return "Resume practice test";
}

/** The label area under the button — what test, and where you are in it. */
function practiceTestContext(t: PracticeTest | null): string {
  if (!t) return "Quick set, the whole subject, or a single chapter — pick when you start.";
  if (t.scope?.kind === "custom") return `${testTitle(t.scope)} — a walk-through of problems near what you missed.`;
  const { correct, answered, total } = scoreTest(t);
  if (t.completedAt != null) return `${testTitle(t.scope)} · scored ${correct}/${total}. Start a new one from there.`;
  return `${testTitle(t.scope)} · ${answered} of ${total} answered — pick up where you left off.`;
}

/**
 * The full problem list, grouped into collapsible chapter nodes.
 * Chapter order + membership come from problemOrder.groupByChapter (pid
 * inference); the topic name on each header comes from the manifest's
 * baked-in `chapters` (ChapterNamer), falling back to "Chapter N".
 */
function ChapterList({
  solutions,
  subjectId,
  chapters,
  onOpenSolution,
}: {
  solutions: Solution[];
  subjectId: string;
  chapters: ChapterInfo[] | undefined;
  onOpenSolution: (s: Solution) => void;
}) {
  const byPid = useMemo(() => new Map(solutions.map((s) => [s.pid, s])), [solutions]);
  const groups = useMemo(
    () => groupByChapter(solutions.map((s) => s.pid), subjectId),
    [solutions, subjectId]
  );
  const nameOf = useMemo(() => {
    const m = new Map((chapters ?? []).map((c) => [c.key, c.name]));
    return (key: string) => m.get(key) ?? "";
  }, [chapters]);

  // Auto-expand when there's just one group (nothing to choose between).
  const [open, setOpen] = useState<Set<string>>(() =>
    groups.length === 1 ? new Set(groups.map((g) => g.chapter.key)) : new Set()
  );
  const toggle = (key: string) =>
    setOpen((prev) => {
      const next = new Set(prev);
      next.has(key) ? next.delete(key) : next.add(key);
      return next;
    });

  return (
    <div className="divide-y divide-slate-200 rounded-lg border border-slate-200">
      {groups.map(({ chapter, pids }) => {
        const isOpen = open.has(chapter.key);
        return (
          <div key={chapter.key}>
            <button
              type="button"
              aria-expanded={isOpen}
              onClick={() => toggle(chapter.key)}
              className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm font-medium text-slate-800 hover:bg-slate-50"
            >
              <span aria-hidden className={`text-slate-400 transition-transform ${isOpen ? "rotate-90" : ""}`}>
                &rsaquo;
              </span>
              <span className="flex-1">{chapterDisplay(chapter.label, nameOf(chapter.key))}</span>
              <span className="text-xs font-normal text-slate-400">{pids.length}</span>
            </button>
            {isOpen && (
              <List>
                {pids.map((pid) => {
                  const s = byPid.get(pid);
                  if (!s) return null;
                  return (
                    <ListItemButton key={pid} onClick={() => onOpenSolution(s)}>
                      <span>{solutionTitle(pid, subjectId)}</span>
                      <span aria-hidden className="text-slate-400">
                        &rsaquo;
                      </span>
                    </ListItemButton>
                  );
                })}
              </List>
            )}
          </div>
        );
      })}
    </div>
  );
}
