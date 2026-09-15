import { useEffect, useState } from "react";
import DOMPurify from "dompurify";
import type { Solution, StepUnit } from "@cm_re/shared-types";
import { getSolution, saveSolution } from "../../api/client";
import StepEditor from "../StepEditor";

/**
 * Statement, MC question, and widget slot are still read-only renders
 * (sanitize-then-dangerouslySetInnerHTML, same as the tutor — DOMPurify
 * keeps MathML, the browser renders <math> natively). Steps are now
 * editable via StepEditor (TipTap) — SOLUTION_EDITOR.org milestone 3.
 * The rest of the panes land in a later increment (milestone 6).
 */

function Html({ html }: { html: string }) {
  return (
    <div
      className="rich"
      dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(html) }}
    />
  );
}

const ID_FIELDS = ["book", "chapter", "section", "set", "problemNumber", "page"] as const;

type SaveStatus = "idle" | "saving" | "saved" | "error";

export default function SolutionView({ pid, onBack }: { pid: string; onBack: () => void }) {
  const [sol, setSol] = useState<Solution | null>(null);
  const [error, setError] = useState<string | null>(null);
  // Local editable copy of the steps — the parent owns this (not
  // StepEditor) since Save writes the whole Solution doc back in one
  // PUT, not per-step.
  const [steps, setSteps] = useState<StepUnit[]>([]);
  const [dirty, setDirty] = useState(false);
  const [saveStatus, setSaveStatus] = useState<SaveStatus>("idle");
  const [saveError, setSaveError] = useState<string | null>(null);

  useEffect(() => {
    setSol(null);
    setError(null);
    setSteps([]);
    setDirty(false);
    setSaveStatus("idle");
    getSolution(pid)
      .then((s) => {
        setSol(s);
        setSteps(s.steps);
      })
      .catch((e) => setError(String(e)));
  }, [pid]);

  function updateStepContent(index: number, html: string) {
    setSteps((prev) => prev.map((s, i) => (i === index ? { ...s, content: html } : s)));
    setDirty(true);
    setSaveStatus("idle");
  }

  async function onSave() {
    if (!sol) return;
    setSaveStatus("saving");
    setSaveError(null);
    try {
      const saved = await saveSolution({ ...sol, steps });
      setSol(saved);
      setSteps(saved.steps);
      setDirty(false);
      setSaveStatus("saved");
    } catch (e) {
      setSaveStatus("error");
      setSaveError(String(e));
    }
  }

  return (
    <div className="sol-view">
      <button className="back" onClick={onBack}>← all solutions</button>

      {error && <p className="error">{error}</p>}
      {!error && !sol && <p className="muted">Loading…</p>}

      {sol && (
        <>
          <h1 className="sol-title">{sol.pid}</h1>
          <dl className="meta">
            <div><dt>subject</dt><dd>{sol.subjectId}</dd></div>
            <div><dt>format version</dt><dd>{sol.version}</dd></div>
            {sol.identification && (
              <div>
                <dt>identification</dt>
                <dd>
                  {ID_FIELDS.map((k) => {
                    const v = sol.identification[k as keyof typeof sol.identification];
                    return v ? `${k}=${v}` : null;
                  })
                    .filter(Boolean)
                    .join(" · ")}
                </dd>
              </div>
            )}
          </dl>

          <section>
            <h2>Statement</h2>
            {sol.statement ? (
              <Html html={sol.statement} />
            ) : (
              <p className="muted"><em>(empty — usual when there's an MC question)</em></p>
            )}
            {sol.statementFigure && (
              <p className="figure-ref">statement figure: <code>{sol.statementFigure}</code></p>
            )}
          </section>

          {sol.question && (
            <section>
              <h2>Question — multiple choice</h2>
              <Html html={sol.question.prompt} />
              <ol className="choices">
                {sol.question.choices.map((c, i) => (
                  <li key={i} className={sol.question!.correctIndex === i ? "correct" : ""}>
                    <Html html={c.content} />
                    {c.feedback && (
                      <div className="feedback">feedback: <Html html={c.feedback} /></div>
                    )}
                  </li>
                ))}
              </ol>
              <p className="muted">
                correctIndex: {sol.question.correctIndex ?? "(none marked)"}
              </p>
            </section>
          )}

          {sol.widgetSlot && (
            <section>
              <h2>Widget slot</h2>
              <p>type: <code>{sol.widgetSlot.type}</code></p>
            </section>
          )}

          <section>
            <div className="steps-header">
              <h2>Steps ({steps.length})</h2>
              <div className="steps-save">
                {saveStatus === "saved" && <span className="save-ok">Saved</span>}
                {saveStatus === "error" && <span className="error">Save failed: {saveError}</span>}
                <button type="button" onClick={onSave} disabled={!dirty || saveStatus === "saving"}>
                  {saveStatus === "saving" ? "Saving…" : "Save"}
                </button>
              </div>
            </div>
            <ol className="steps">
              {steps.map((s, i) => (
                <li key={i}>
                  <span className={`step-role step-role-${s.role}`}>{s.role}</span>
                  <StepEditor content={s.content} onChange={(html) => updateStepContent(i, html)} />
                  {(s.figure || (s.figures && s.figures.length > 0)) && (
                    <p className="figure-ref">
                      figure(s): {[s.figure, ...(s.figures ?? [])].filter(Boolean).join(", ")}
                    </p>
                  )}
                </li>
              ))}
            </ol>
          </section>

          <p className="muted read-only-note">
            Statement, question, and widget slot are still read-only — editing lands in a later increment.
          </p>
        </>
      )}
    </div>
  );
}
