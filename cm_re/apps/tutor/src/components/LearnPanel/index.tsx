import { useEffect, useRef, useState } from "react";
import type { Solution } from "@cm_re/shared-types";
import {
  GRADES,
  gradeLabel,
  explainProblem,
  askFollowUp,
  problemTextOf,
  ExplainAbortError,
  type Grade,
} from "../../api/aiClient";
import { SanitizedHtml } from "../StepViewer";
import { Spinner } from "../ui/spinner";
import { cn } from "../../lib/utils";

/**
 * "Learn" — ask an AI to explain the current problem, tuned to a grade
 * level. UI only for now: the call goes through api/aiClient's stub,
 * which returns a canned placeholder (see that file's TODO).
 *
 * Collapsible section, sits with the solution. The chosen grade is
 * remembered in localStorage so it's sticky across problems. Picking a
 * grade button fires the explanation immediately — there's no separate
 * "Explain this problem" step; re-tapping a grade (the same one or a
 * different one) re-explains at that level.
 *
 * Once an explanation exists, a "Ask a follow-up" input appears below
 * it — a related question ("what is the form of the function?") sent
 * with the problem + this explanation as context (askFollowUp), same
 * non-disclosure posture as the main explanation. Answers append to a
 * running list rather than replacing anything, so earlier follow-ups
 * stay visible; the whole list resets whenever a fresh explanation is
 * requested (grade change / re-explain / a different problem).
 */

interface FollowUp {
  question: string;
  answer: string | null;
  placeholder: boolean;
  error: boolean;
}

const GRADE_KEY = "cm_re.learn.grade";

function loadGrade(): Grade | null {
  const v = typeof localStorage !== "undefined" ? localStorage.getItem(GRADE_KEY) : null;
  return (GRADES as string[]).includes(v ?? "") ? (v as Grade) : null;
}

type Status = "idle" | "loading" | "done" | "error";

export default function LearnPanel({ solution, title }: { solution: Solution; title: string }) {
  const [open, setOpen] = useState(false);
  const [grade, setGrade] = useState<Grade | null>(loadGrade);
  const [status, setStatus] = useState<Status>("idle");
  const [result, setResult] = useState<string | null>(null);
  const [placeholder, setPlaceholder] = useState(false);
  const abortRef = useRef<AbortController | null>(null);

  const [followUps, setFollowUps] = useState<FollowUp[]>([]);
  const [question, setQuestion] = useState("");
  const [followUpBusy, setFollowUpBusy] = useState(false);
  const followUpAbortRef = useRef<AbortController | null>(null);

  // Reset when the parent swaps in a different solution.
  useEffect(() => {
    abortRef.current?.abort();
    followUpAbortRef.current?.abort();
    setStatus("idle");
    setResult(null);
    setFollowUps([]);
    setQuestion("");
  }, [solution.pid]);

  useEffect(() => () => abortRef.current?.abort(), []);
  useEffect(() => () => followUpAbortRef.current?.abort(), []);

  function pickGrade(g: Grade) {
    setGrade(g);
    try {
      localStorage.setItem(GRADE_KEY, g);
    } catch {
      /* private mode / storage disabled — fine, just not sticky */
    }
    explain(g);
  }

  async function explain(g: Grade) {
    abortRef.current?.abort();
    const ac = new AbortController();
    abortRef.current = ac;
    setStatus("loading");
    setResult(null);
    setFollowUps([]);
    setQuestion("");
    try {
      const res = await explainProblem(
        { pid: solution.pid, title, problemText: problemTextOf(solution), grade: g },
        ac.signal
      );
      setResult(res.text);
      setPlaceholder(res.placeholder);
      setStatus("done");
    } catch (e) {
      if (e instanceof ExplainAbortError) return;
      setStatus("error");
    }
  }

  async function submitFollowUp() {
    const q = question.trim();
    if (!q || !result || !grade || followUpBusy) return;
    followUpAbortRef.current?.abort();
    const ac = new AbortController();
    followUpAbortRef.current = ac;
    setFollowUpBusy(true);
    setQuestion("");
    // Optimistic row so the question shows immediately while it loads.
    const index = followUps.length;
    setFollowUps((prev) => [...prev, { question: q, answer: null, placeholder: false, error: false }]);
    try {
      const res = await askFollowUp({ pid: solution.pid, grade, priorAnswer: result, question: q }, ac.signal);
      setFollowUps((prev) =>
        prev.map((f, i) => (i === index ? { ...f, answer: res.text, placeholder: res.placeholder } : f))
      );
    } catch (e) {
      if (e instanceof ExplainAbortError) return;
      setFollowUps((prev) => prev.map((f, i) => (i === index ? { ...f, error: true } : f)));
    } finally {
      setFollowUpBusy(false);
    }
  }

  return (
    <div className="mt-4 rounded-lg border border-slate-200">
      <button
        type="button"
        className="flex w-full items-center justify-between px-3 py-2 text-sm font-medium text-slate-700"
        onClick={() => setOpen((v) => !v)}
      >
        <span>Learn — explain this problem</span>
        <span aria-hidden className="text-slate-400">{open ? "▾" : "▸"}</span>
      </button>

      {open && (
        <div className="border-t border-slate-200 p-3">
          <p className="mb-2 text-sm font-medium text-slate-700">Tell me like I'm a…</p>
          <div className="mb-1 grid grid-cols-3 gap-2">
            {GRADES.map((g) => (
              <button
                key={g}
                type="button"
                onClick={() => pickGrade(g)}
                disabled={status === "loading"}
                className={cn(
                  "rounded-md border px-2 py-2 text-sm transition-colors disabled:cursor-not-allowed disabled:opacity-60",
                  grade === g
                    ? "border-blue-500 bg-blue-50 font-medium text-blue-800"
                    : "border-slate-200 text-slate-700 hover:border-slate-300"
                )}
              >
                {status === "loading" && grade === g ? <Spinner /> : gradeLabel(g)}
              </button>
            ))}
          </div>

          {status === "error" && (
            <p className="mt-2 text-sm text-red-600">Couldn't get an explanation. Try again.</p>
          )}

          {result && (
            <div className="mt-3">
              {placeholder && (
                <p className="mb-1 text-xs font-medium text-amber-700">
                  Placeholder — the AI explanation service is unavailable right now
                </p>
              )}
              {/* The model returns an HTML fragment with <math> MathML for
                  formulas (see AiService's prompt). Sanitize + render the
                  same way StepViewer does — DOMPurify keeps MathML, the
                  browser renders it natively. */}
              <SanitizedHtml
                html={result}
                className="learn-explanation rounded-md bg-slate-50 p-3 text-sm text-slate-800"
              />

              {followUps.map((f, i) => (
                <div key={i} className="mt-2 border-t border-slate-200 pt-2">
                  <p className="text-sm font-medium text-slate-700">{f.question}</p>
                  {f.error && <p className="mt-1 text-sm text-red-600">Couldn't get an answer. Try again.</p>}
                  {f.answer == null && !f.error && (
                    <p className="mt-1 flex items-center gap-2 text-sm text-slate-500">
                      <Spinner /> Thinking…
                    </p>
                  )}
                  {f.answer != null && (
                    <>
                      {f.placeholder && (
                        <p className="mb-1 mt-1 text-xs font-medium text-amber-700">
                          Placeholder — the AI explanation service is unavailable right now
                        </p>
                      )}
                      <SanitizedHtml
                        html={f.answer}
                        className="learn-explanation mt-1 rounded-md bg-slate-50 p-3 text-sm text-slate-800"
                      />
                    </>
                  )}
                </div>
              ))}

              <form
                className="mt-3 flex gap-2"
                onSubmit={(e) => {
                  e.preventDefault();
                  void submitFollowUp();
                }}
              >
                <input
                  type="text"
                  value={question}
                  onChange={(e) => setQuestion(e.target.value)}
                  placeholder="Ask a follow-up — e.g. what is the form of the function?"
                  disabled={followUpBusy}
                  className="flex-1 rounded-md border border-slate-300 px-3 py-1.5 text-sm disabled:opacity-60"
                />
                <button
                  type="submit"
                  disabled={followUpBusy || !question.trim()}
                  className="shrink-0 rounded-md bg-blue-600 px-3 py-1.5 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {followUpBusy ? <Spinner /> : "Ask"}
                </button>
              </form>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

