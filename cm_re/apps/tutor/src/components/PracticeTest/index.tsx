import { useCallback, useEffect, useMemo, useState } from "react";
import type { Solution } from "@cm_re/shared-types";
import { isModuleInstalled, getSolution, getSolutionsForModule } from "../../offline/moduleManager";
import {
  getActiveTest,
  startTest,
  recordAnswer,
  finishTest,
  clearTest,
  scoreTest,
  sample,
  testTitle,
  QUICK_SIZE,
  CHAPTER_SIZE,
  type PracticeTest as PracticeTestT,
  type TestScope,
} from "../../offline/practiceTestStore";
import { solutionTitle } from "../../lib/solutionTitle";
import { orderPids, groupByChapter } from "../../lib/problemOrder";
import { navigate, hashFor } from "../../routing";
import { QuestionView, choiceLetter } from "../QuestionView";
import { StatementView } from "../StepViewer";
import WhiteboardPanel from "../WhiteboardPanel";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";
import { List, ListItemButton } from "../ui/list";

/**
 * Practice tests for one subject — a fresh route (`#/t/<subjectId>`),
 * separate from the full solution list at `#/m/<subjectId>`.
 *
 * Three kinds, chosen from the picker:
 *   - Quick test: QUICK_SIZE random scorable problems.
 *   - Whole subject: every scorable problem, in problem order.
 *   - Chapter test: CHAPTER_SIZE random from one chapter.
 * All pid lists are stored in problem order (see lib/problemOrder).
 *
 * Then: an index tracking which problems are still unanswered → answer
 * each (answer recorded, correctness NOT revealed) → a score screen
 * with a compact per-question breakdown. One active test per subject,
 * persisted in IndexedDB (local only).
 */

type View = { k: "index" } | { k: "question"; idx: number } | { k: "score" } | { k: "review"; idx: number };

// only solutions with a scorable MC question can be in a test
const scorable = (s: Solution) => !!s.question && typeof s.question.correctIndex === "number";

export default function PracticeTest({ subjectId }: { subjectId: string }) {
  const [installed, setInstalled] = useState<boolean | null>(null);
  const [test, setTest] = useState<PracticeTestT | null | undefined>(undefined);
  const [allSolutions, setAllSolutions] = useState<Solution[] | null>(null);
  const [solutions, setSolutions] = useState<Map<string, Solution>>(new Map());
  const [view, setView] = useState<View>({ k: "index" });
  const [busy, setBusy] = useState(false);

  // Load install state + any active test on mount / subject change.
  useEffect(() => {
    let cancelled = false;
    (async () => {
      const inst = await isModuleInstalled(subjectId);
      if (cancelled) return;
      setInstalled(inst);
      if (!inst) {
        setTest(null);
        return;
      }
      const active = await getActiveTest(subjectId);
      if (cancelled) return;
      setTest(active ?? null);
      if (active) {
        const loaded = await Promise.all(active.pids.map((pid) => getSolution(pid)));
        if (cancelled) return;
        setSolutions(new Map(loaded.filter((s): s is Solution => !!s).map((s) => [s.pid, s])));
        setView(active.completedAt ? { k: "score" } : { k: "index" });
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [subjectId]);

  // Fetch the whole module once, lazily, when the picker needs it.
  const needPicker = installed === true && test === null;
  useEffect(() => {
    if (needPicker && allSolutions === null) {
      getSolutionsForModule(subjectId).then(setAllSolutions);
    }
  }, [needPicker, allSolutions, subjectId]);

  const chapters = useMemo(() => {
    if (!allSolutions) return [];
    const pids = allSolutions.filter(scorable).map((s) => s.pid);
    return groupByChapter(pids, subjectId);
  }, [allSolutions, subjectId]);

  const begin = useCallback(
    async (pids: string[], scope: TestScope, pool: Solution[]) => {
      setBusy(true);
      try {
        const ordered = orderPids(pids, subjectId);
        const fresh = await startTest(subjectId, ordered, scope);
        setSolutions(new Map(ordered.map((pid) => [pid, pool.find((s) => s.pid === pid)!])));
        setTest(fresh);
        setView({ k: "index" });
      } finally {
        setBusy(false);
      }
    },
    [subjectId]
  );

  async function onAnswer(pid: string, selectedIndex: number, correct: boolean | null) {
    await recordAnswer(subjectId, pid, { selectedIndex, correct });
    setTest((t) => (t ? { ...t, answers: { ...t.answers, [pid]: { selectedIndex, correct } } } : t));
  }

  async function onFinish() {
    if (!test) return;
    const unanswered = test.pids.filter((p) => !test.answers[p]).length;
    if (unanswered > 0 && !confirm(`${unanswered} question${unanswered === 1 ? "" : "s"} still unanswered. Finish anyway?`)) {
      return;
    }
    await finishTest(subjectId);
    setTest((t) => (t ? { ...t, completedAt: Date.now() } : t));
    setView({ k: "score" });
  }

  async function backToPicker() {
    await clearTest(subjectId);
    setSolutions(new Map());
    setTest(null);
    setView({ k: "index" });
  }

  // ---- render ----

  if (installed === false) {
    return (
      <Card>
        <CardContent>
          <p className="mb-3 text-sm text-slate-600">
            Download <span className="font-medium">{subjectId}</span> first — a practice test is built from problems on
            this device.
          </p>
          <Button variant="outline" onClick={() => navigate(hashFor.module(subjectId))}>
            Go to {subjectId}
          </Button>
        </CardContent>
      </Card>
    );
  }

  if (installed === null || test === undefined) {
    return <Spinner />;
  }

  // ---- picker (no active test) ----
  if (test === null) {
    if (allSolutions === null) return <Spinner />;
    const pool = allSolutions;
    const scorablePids = pool.filter(scorable).map((s) => s.pid);
    if (scorablePids.length === 0) {
      return (
        <Card>
          <CardContent>
            <p className="text-sm text-slate-500">No multiple-choice problems in this subject.</p>
          </CardContent>
        </Card>
      );
    }
    return (
      <Card>
        <CardHeader>
          <CardTitle>Practice test</CardTitle>
        </CardHeader>
        <CardContent>
          <Button
            className="mb-2 w-full"
            disabled={busy}
            onClick={() => begin(sample(scorablePids, QUICK_SIZE), { kind: "random" }, pool)}
          >
            Quick test — {Math.min(QUICK_SIZE, scorablePids.length)} random
          </Button>
          <Button
            variant="outline"
            className="mb-4 w-full"
            disabled={busy}
            onClick={() => begin(scorablePids, { kind: "subject" }, pool)}
          >
            Whole subject test — all {scorablePids.length} problems
          </Button>

          <p className="mb-2 text-sm font-medium text-slate-700">By chapter ({CHAPTER_SIZE} questions each)</p>
          <List>
            {chapters.map(({ chapter, pids }) => (
              <ListItemButton
                key={chapter.key}
                onClick={() =>
                  begin(sample(pids, CHAPTER_SIZE), { kind: "chapter", chapterKey: chapter.key, label: chapter.label }, pool)
                }
              >
                <span>{chapter.label}</span>
                <span className="text-xs text-slate-400">{pids.length} available</span>
              </ListItemButton>
            ))}
          </List>
        </CardContent>
      </Card>
    );
  }

  const title = testTitle(test.scope);

  // ---- one question ----
  if (view.k === "question") {
    const pid = test.pids[view.idx];
    const solution = solutions.get(pid);
    const prior = test.answers[pid];
    const goIndex = () => setView({ k: "index" });
    const nextUnanswered = test.pids.findIndex((p, i) => i > view.idx && !test.answers[p]);
    return (
      <Card>
        <CardContent>
          <div className="mb-3 flex items-center justify-between text-sm">
            <button className="text-blue-600 hover:underline" onClick={goIndex}>
              &larr; {title}
            </button>
            <span className="text-slate-500">
              {view.idx + 1} / {test.pids.length}
            </span>
          </div>
          <h2 className="mb-3 text-base font-semibold text-slate-900">{solutionTitle(pid, subjectId)}</h2>
          {solution ? (
            <>
              <StatementView solution={solution} />
              {solution.question ? (
                <QuestionView
                  key={pid}
                  question={solution.question}
                  revealOnCheck={false}
                  initialSelectedIndex={prior?.selectedIndex}
                  onAnswer={(r) => {
                    void onAnswer(pid, r.selectedIndex, r.correct);
                    if (nextUnanswered !== -1) setView({ k: "question", idx: nextUnanswered });
                    else goIndex();
                  }}
                />
              ) : (
                <p className="text-sm text-slate-500">This problem has no question.</p>
              )}
              {/* Same per-solution board as the normal view (keyed by pid). */}
              <WhiteboardPanel key={`wb-${pid}`} pid={pid} />
            </>
          ) : (
            <Spinner />
          )}
        </CardContent>
      </Card>
    );
  }

  // ---- review one answered problem (from the score screen) ----
  if (view.k === "review") {
    const pid = test.pids[view.idx];
    const solution = solutions.get(pid);
    const a = test.answers[pid];
    const backToScore = () => setView({ k: "score" });
    return (
      <Card>
        <CardContent>
          <div className="mb-3 flex items-center justify-between text-sm">
            <button className="text-blue-600 hover:underline" onClick={backToScore}>
              &larr; Score
            </button>
            <span className="text-slate-500">
              {view.idx + 1} / {test.pids.length}
            </span>
          </div>
          <h2 className="mb-3 text-base font-semibold text-slate-900">{solutionTitle(pid, subjectId)}</h2>
          {solution ? (
            <>
              <StatementView solution={solution} />
              {solution.question && a ? (
                <QuestionView key={pid} question={solution.question} reviewMode initialSelectedIndex={a.selectedIndex} />
              ) : (
                <p className="text-sm text-slate-500">No recorded answer for this problem.</p>
              )}
              <WhiteboardPanel key={`wb-${pid}`} pid={pid} />
            </>
          ) : (
            <Spinner />
          )}
        </CardContent>
      </Card>
    );
  }

  // ---- score ----
  if (view.k === "score") {
    const { correct, scorable: scorableCount, answered, total } = scoreTest(test);
    return (
      <Card>
        <CardHeader>
          <CardTitle>{title} — your score</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="mb-1 text-2xl font-bold text-slate-900">
            {correct} / {total}
          </p>
          <p className="mb-4 text-sm text-slate-500">
            {answered} of {total} answered
            {scorableCount < total ? ` · ${total - scorableCount} not scorable` : ""}
          </p>

          <ul className="divide-y divide-slate-100">
            {test.pids.map((pid, i) => {
              const solution = solutions.get(pid);
              const a = test.answers[pid];
              const key = solution?.question?.correctIndex;
              const right = a && a.correct === true;
              const scoredWrong = a && a.correct === false;
              const row = (
                <>
                  <span className="w-6 shrink-0 text-right text-slate-400">{i + 1}.</span>
                  <span className="flex-1 text-slate-700">{solutionTitle(pid, subjectId)}</span>
                  {a ? (
                    <span className="shrink-0 text-slate-500">
                      you: {choiceLetter(a.selectedIndex)}
                      {typeof key === "number" && ` · ans: ${choiceLetter(key)}`}
                    </span>
                  ) : (
                    <span className="shrink-0 text-slate-400">skipped</span>
                  )}
                  <span
                    className={
                      right
                        ? "w-4 shrink-0 text-center font-bold text-green-600"
                        : scoredWrong
                          ? "w-4 shrink-0 text-center font-bold text-red-500"
                          : "w-4 shrink-0 text-center text-slate-300"
                    }
                  >
                    {right ? "✓" : scoredWrong ? "✗" : "–"}
                  </span>
                </>
              );
              return (
                <li key={pid}>
                  {a ? (
                    <button
                      type="button"
                      onClick={() => setView({ k: "review", idx: i })}
                      className="flex w-full items-baseline gap-2 py-1.5 text-left text-sm hover:bg-slate-50"
                    >
                      {row}
                    </button>
                  ) : (
                    <div className="flex items-baseline gap-2 py-1.5 text-sm">{row}</div>
                  )}
                </li>
              );
            })}
          </ul>

          <Button className="mt-4 w-full" onClick={backToPicker} disabled={busy}>
            Back to test menu
          </Button>
        </CardContent>
      </Card>
    );
  }

  // ---- index ----
  const answeredCount = test.pids.filter((p) => test.answers[p]).length;
  const firstUnanswered = test.pids.findIndex((p) => !test.answers[p]);
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="mb-3 flex items-center justify-between">
          <button className="text-sm text-blue-600 hover:underline" onClick={backToPicker}>
            &larr; Test menu
          </button>
          <span className="text-sm text-slate-500">
            {answeredCount} of {test.pids.length} answered
          </span>
        </div>
        {firstUnanswered !== -1 && (
          <Button
            variant="outline"
            className="mb-3 w-full"
            onClick={() => setView({ k: "question", idx: firstUnanswered })}
          >
            {answeredCount === 0 ? "Start" : "Next unanswered"} &rarr;
          </Button>
        )}
        <List>
          {test.pids.map((pid, i) => {
            const done = !!test.answers[pid];
            return (
              <ListItemButton key={pid} onClick={() => setView({ k: "question", idx: i })}>
                <span className={done ? "text-slate-400" : "text-slate-900"}>
                  {i + 1}. {solutionTitle(pid, subjectId)}
                </span>
                <span
                  className={
                    done
                      ? "rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-800"
                      : "rounded-full bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-800"
                  }
                >
                  {done ? "answered" : "to do"}
                </span>
              </ListItemButton>
            );
          })}
        </List>
        <Button className="mt-4 w-full" onClick={onFinish}>
          Finish &amp; see score
        </Button>
      </CardContent>
    </Card>
  );
}
