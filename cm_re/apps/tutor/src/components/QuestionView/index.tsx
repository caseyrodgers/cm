import { useEffect, useState } from "react";
import DOMPurify from "dompurify";
import type { McQuestion } from "@cm_re/shared-types";
import { Button } from "../ui/button";
import { cn } from "../../lib/utils";

/**
 * Interactive renderer for a solution's embedded multiple-choice
 * question (see shared-types' McQuestion — the preprocessor lifts
 * these out of the legacy statement HTML so this component never has
 * to parse either legacy dialect).
 *
 * Two modes:
 *   - practice (default): pick, "Check answer", correct/wrong is
 *     revealed inline.
 *   - test (`revealOnCheck={false}`): pick, "Submit answer", the
 *     answer is recorded via onAnswer but NOT revealed — the score
 *     comes at the end of the test.
 *   - review (`reviewMode`): read-only, pre-selected to
 *     `initialSelectedIndex`, always revealed — the score screen's
 *     per-question breakdown.
 *
 * Answer-key handling: `question.correctIndex` ships inside the
 * downloaded module (offline-first grading), but is NEVER written into
 * the DOM until a reveal actually happens — read only in `check()` and
 * the reveal render path.
 */

const LETTERS = "ABCDEFGH";

/** "A", "B", … for a 0-based choice index (falls back to the number for >8 choices). */
export function choiceLetter(i: number): string {
  return LETTERS[i] ?? String(i + 1);
}

export interface QuestionResult {
  selectedIndex: number;
  /** null when the legacy source marked no choice correct (a known data gap for a handful of items). */
  correct: boolean | null;
}

function Sanitized({ html, className }: { html: string; className?: string }) {
  return <span className={className} dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(html) }} />;
}

function isBlank(html: string): boolean {
  // JS \s already covers &nbsp; (U+00A0), so a prompt that's only
  // empty <p></p> wrappers collapses to "".
  return DOMPurify.sanitize(html).replace(/<[^>]*>/g, "").replace(/\s/g, "") === "";
}

export function QuestionView({
  question,
  onAnswer,
  revealOnCheck = true,
  reviewMode = false,
  initialSelectedIndex,
}: {
  question: McQuestion;
  onAnswer?: (result: QuestionResult) => void;
  revealOnCheck?: boolean;
  reviewMode?: boolean;
  initialSelectedIndex?: number;
}) {
  const [selected, setSelected] = useState<number | null>(initialSelectedIndex ?? null);
  const [checked, setChecked] = useState(reviewMode);

  // Reset when the parent swaps in a different question.
  useEffect(() => {
    setSelected(initialSelectedIndex ?? null);
    setChecked(reviewMode);
  }, [question, initialSelectedIndex, reviewMode]);

  const hasKey = typeof question.correctIndex === "number";
  const reveal = checked && (reviewMode || revealOnCheck);
  const locked = checked && (reviewMode || revealOnCheck);
  const gotItRight = reveal && hasKey && selected === question.correctIndex;

  function submit() {
    if (selected === null || (checked && locked)) return;
    setChecked(true);
    onAnswer?.({
      selectedIndex: selected,
      correct: hasKey ? selected === question.correctIndex : null,
    });
  }

  const buttonLabel = revealOnCheck ? "Check answer" : "Submit answer";

  return (
    <div className="mb-4 rounded-lg border border-slate-200 p-4">
      {!isBlank(question.prompt) && <Sanitized className="prose-sm mb-3 block" html={question.prompt} />}

      <ul className="space-y-2">
        {question.choices.map((choice, i) => {
          const isSelected = selected === i;
          const revealCorrect = reveal && hasKey && i === question.correctIndex;
          const revealWrongPick = reveal && isSelected && hasKey && i !== question.correctIndex;
          return (
            <li key={i}>
              <button
                type="button"
                disabled={locked || reviewMode}
                onClick={() => setSelected(i)}
                className={cn(
                  "flex w-full items-start gap-3 rounded-md border px-3 py-2 text-left transition-colors",
                  !reveal && isSelected && "border-blue-500 bg-blue-50",
                  !reveal && !isSelected && "border-slate-200 hover:border-slate-300",
                  reveal && !revealCorrect && !revealWrongPick && "border-slate-200 opacity-60",
                  revealCorrect && "border-2 border-green-500 bg-green-100 font-medium text-green-900",
                  revealWrongPick && "border-red-400 bg-red-50",
                  // test mode after submit: mark the pick without saying if it's right
                  checked && !reveal && isSelected && "border-blue-500 bg-blue-50"
                )}
              >
                <span
                  className={cn(
                    "mt-0.5 font-semibold",
                    revealCorrect ? "text-green-700" : revealWrongPick ? "text-red-500" : "text-slate-500"
                  )}
                >
                  {LETTERS[i] ?? i + 1}.
                </span>
                <span className="flex-1">
                  <Sanitized html={choice.content} />
                  {reveal && isSelected && choice.feedback && (
                    <Sanitized className="mt-1 block text-sm text-slate-600" html={choice.feedback} />
                  )}
                </span>
                {revealCorrect && (
                  <span
                    aria-label="correct answer"
                    className="mt-0.5 inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-green-500 text-xs font-bold text-white"
                  >
                    &#10003;
                  </span>
                )}
                {revealWrongPick && (
                  <span
                    aria-label="your answer, incorrect"
                    className="mt-0.5 inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-red-400 text-xs font-bold text-white"
                  >
                    &#10007;
                  </span>
                )}
              </button>
            </li>
          );
        })}
      </ul>

      {!reviewMode && (
        <div className="mt-3 flex items-center gap-3">
          <Button variant="outline" onClick={submit} disabled={selected === null || (checked && locked)}>
            {buttonLabel}
          </Button>
          {reveal && (
            <span
              className={cn(
                "inline-flex items-center gap-1 text-sm font-semibold",
                !hasKey && "text-slate-500",
                hasKey && gotItRight && "text-green-600",
                hasKey && !gotItRight && "text-red-600"
              )}
            >
              {hasKey && gotItRight && <span aria-hidden>&#10003;</span>}
              {!hasKey ? "Answer recorded" : gotItRight ? "Correct" : "Not quite"}
            </span>
          )}
          {checked && !reveal && <span className="text-sm font-medium text-slate-500">Answer submitted</span>}
        </div>
      )}
    </div>
  );
}

export default QuestionView;
