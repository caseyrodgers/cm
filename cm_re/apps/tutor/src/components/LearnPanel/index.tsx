import { useEffect, useRef, useState } from "react";
import type { Solution } from "@cm_re/shared-types";
import {
  GRADES,
  gradeLabel,
  explainProblem,
  problemTextOf,
  ExplainAbortError,
  type Grade,
} from "../../api/aiClient";
import { SanitizedHtml } from "../StepViewer";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";
import { cn } from "../../lib/utils";

/**
 * "Learn" — ask an AI to explain the current problem, tuned to a grade
 * level. UI only for now: the call goes through api/aiClient's stub,
 * which returns a canned placeholder (see that file's TODO).
 *
 * Collapsible section, sits with the solution. The chosen grade is
 * remembered in localStorage so it's sticky across problems.
 */

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

  // Reset when the parent swaps in a different solution.
  useEffect(() => {
    abortRef.current?.abort();
    setStatus("idle");
    setResult(null);
  }, [solution.pid]);

  useEffect(() => () => abortRef.current?.abort(), []);

  function pickGrade(g: Grade) {
    setGrade(g);
    try {
      localStorage.setItem(GRADE_KEY, g);
    } catch {
      /* private mode / storage disabled — fine, just not sticky */
    }
  }

  async function explain() {
    if (!grade) return;
    abortRef.current?.abort();
    const ac = new AbortController();
    abortRef.current = ac;
    setStatus("loading");
    setResult(null);
    try {
      const res = await explainProblem(
        { pid: solution.pid, title, problemText: problemTextOf(solution), grade },
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
          <p className="mb-2 text-sm font-medium text-slate-700">Tell me like a…</p>
          <div className="mb-3 grid grid-cols-3 gap-2">
            {GRADES.map((g) => (
              <button
                key={g}
                type="button"
                onClick={() => pickGrade(g)}
                className={cn(
                  "rounded-md border px-2 py-2 text-sm transition-colors",
                  grade === g
                    ? "border-blue-500 bg-blue-50 font-medium text-blue-800"
                    : "border-slate-200 text-slate-700 hover:border-slate-300"
                )}
              >
                {gradeLabel(g)}
              </button>
            ))}
          </div>

          <Button className="w-full" onClick={explain} disabled={!grade || status === "loading"}>
            {status === "loading" ? <Spinner /> : result ? "Explain again" : "Explain this problem"}
          </Button>

          {status === "error" && (
            <p className="mt-2 text-sm text-red-600">Couldn't get an explanation. Try again.</p>
          )}

          {result && (
            <div className="mt-3">
              {placeholder && (
                <p className="mb-1 text-xs font-medium text-amber-700">
                  Stub response from the server — the AI model isn't wired up yet
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
            </div>
          )}
        </div>
      )}
    </div>
  );
}

