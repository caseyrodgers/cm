import { useEffect, useState } from "react";
import DOMPurify from "dompurify";
import type { Solution } from "@cm_re/shared-types";
import { getSolution } from "../../api/client";

/**
 * Read-only render of one solution: statement, MC question, widget
 * slot, and steps. Uses the same sanitize-then-dangerouslySetInnerHTML
 * approach the tutor uses (DOMPurify keeps MathML; the browser renders
 * <math> natively). TipTap editing replaces the read-only panes in the
 * next increment — see SOLUTION_EDITOR.org's milestone plan.
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

export default function SolutionView({ pid, onBack }: { pid: string; onBack: () => void }) {
  const [sol, setSol] = useState<Solution | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setSol(null);
    setError(null);
    getSolution(pid)
      .then(setSol)
      .catch((e) => setError(String(e)));
  }, [pid]);

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
            <h2>Steps ({sol.steps.length})</h2>
            <ol className="steps">
              {sol.steps.map((s, i) => (
                <li key={i}>
                  <span className={`step-role step-role-${s.role}`}>{s.role}</span>
                  <Html html={s.content} />
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
            Read-only — TipTap editing lands in the next increment.
          </p>
        </>
      )}
    </div>
  );
}
