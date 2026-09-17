import { useEffect, useState } from "react";
import type { McChoice, McQuestion, Solution, StepUnit } from "@cm_re/shared-types";
import { getSolution, saveSolution } from "../../api/client";
import StepEditor from "../StepEditor";

/**
 * Statement, MC question, and steps are all editable now (statement/
 * question via StepEditor same as steps — SOLUTION_EDITOR.org milestone
 * 6's core; widget slot stays read-only, out of scope for this pass).
 * Identification/pid/subjectId/version are record-keeping, not content
 * — stay read-only. One Save button PUTs the whole document at once.
 */

const ID_FIELDS = ["book", "chapter", "section", "set", "problemNumber", "page"] as const;

/** A brand-new question — 4 empty choices is the corpus norm (see shared-types' McQuestion doc). */
function blankQuestion(): McQuestion {
  return { prompt: "", choices: [{ content: "" }, { content: "" }, { content: "" }, { content: "" }] };
}

type SaveStatus = "idle" | "saving" | "saved" | "error";

export default function SolutionView({ pid, onBack }: { pid: string; onBack: () => void }) {
  const [sol, setSol] = useState<Solution | null>(null);
  const [error, setError] = useState<string | null>(null);
  // Local editable copies — the parent owns these (not the child
  // editors) since Save writes the whole Solution doc back in one PUT.
  const [statement, setStatement] = useState("");
  const [question, setQuestion] = useState<McQuestion | undefined>(undefined);
  const [steps, setSteps] = useState<StepUnit[]>([]);
  const [dirty, setDirty] = useState(false);
  const [saveStatus, setSaveStatus] = useState<SaveStatus>("idle");
  const [saveError, setSaveError] = useState<string | null>(null);

  useEffect(() => {
    setSol(null);
    setError(null);
    setStatement("");
    setQuestion(undefined);
    setSteps([]);
    setDirty(false);
    setSaveStatus("idle");
    getSolution(pid)
      .then((s) => {
        setSol(s);
        setStatement(s.statement);
        setQuestion(s.question);
        setSteps(s.steps);
      })
      .catch((e) => setError(String(e)));
  }, [pid]);

  function markDirty() {
    setDirty(true);
    setSaveStatus("idle");
  }

  function updateStatement(html: string) {
    setStatement(html);
    markDirty();
  }

  function updateStepContent(index: number, html: string) {
    setSteps((prev) => prev.map((s, i) => (i === index ? { ...s, content: html } : s)));
    markDirty();
  }

  function updateQuestionPrompt(html: string) {
    setQuestion((q) => (q ? { ...q, prompt: html } : q));
    markDirty();
  }

  function updateChoice(index: number, patch: Partial<McChoice>) {
    setQuestion((q) =>
      q ? { ...q, choices: q.choices.map((c, i) => (i === index ? { ...c, ...patch } : c)) } : q
    );
    markDirty();
  }

  function setCorrectIndex(index: number) {
    setQuestion((q) => (q ? { ...q, correctIndex: index } : q));
    markDirty();
  }

  function addChoice() {
    setQuestion((q) => (q ? { ...q, choices: [...q.choices, { content: "" }] } : q));
    markDirty();
  }

  function removeChoice(index: number) {
    setQuestion((q) => {
      if (!q) return q;
      const choices = q.choices.filter((_, i) => i !== index);
      const correctIndex =
        q.correctIndex == null
          ? undefined
          : q.correctIndex === index
            ? undefined // the removed choice was the marked-correct one
            : q.correctIndex > index
              ? q.correctIndex - 1
              : q.correctIndex;
      return { ...q, choices, correctIndex };
    });
    markDirty();
  }

  function addQuestion() {
    setQuestion(blankQuestion());
    markDirty();
  }

  function removeQuestion() {
    setQuestion(undefined);
    markDirty();
  }

  async function onSave() {
    if (!sol) return;
    setSaveStatus("saving");
    setSaveError(null);
    try {
      const saved = await saveSolution({ ...sol, statement, question, steps });
      setSol(saved);
      setStatement(saved.statement);
      setQuestion(saved.question);
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
          <div className="sol-header">
            <div>
              <h1 className="sol-title">{sol.pid}</h1>
              {/* The tutor is the same origin's "/" (this editor is "/editor/"
                  — see SOLUTION_EDITOR.org's "one server, two UIs"), and both
                  apps happen to use the identical #/s/<pid> hash shape for a
                  single solution — so a root-relative href (not a bare "#/s/…")
                  is what forces a real navigation to the *other* app instead of
                  just rewriting this page's own hash. Only shows something if
                  that pid's subject module is already downloaded in whatever
                  browser tab it opens in — the tutor has no live single-pid
                  fetch, IndexedDB-only. */}
              <a
                className="tutor-link"
                href={`/#/s/${encodeURIComponent(sol.pid)}`}
                target="_blank"
                rel="noopener noreferrer"
                title="Opens in the tutor app — only shows content if this solution's subject is already downloaded there"
              >
                Open in tutor ↗
              </a>
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
            </div>
            <div className="save-bar">
              {saveStatus === "saved" && <span className="save-ok">Saved</span>}
              {saveStatus === "error" && <span className="error">Save failed: {saveError}</span>}
              <button type="button" onClick={onSave} disabled={!dirty || saveStatus === "saving"}>
                {saveStatus === "saving" ? "Saving…" : "Save"}
              </button>
            </div>
          </div>

          <section>
            <h2>Statement</h2>
            <StepEditor content={statement} onChange={updateStatement} />
            {sol.statementFigure && (
              <p className="figure-ref">statement figure: <code>{sol.statementFigure}</code></p>
            )}
          </section>

          <section>
            <div className="steps-header">
              <h2>Question — multiple choice</h2>
              {question ? (
                <button type="button" onClick={removeQuestion}>Remove question</button>
              ) : (
                <button type="button" onClick={addQuestion}>Add multiple-choice question</button>
              )}
            </div>
            {question ? (
              <>
                <p className="muted field-label">Prompt</p>
                <StepEditor content={question.prompt} onChange={updateQuestionPrompt} />
                <p className="muted field-label">Choices — pick the correct one</p>
                <ol className="choices choices-edit">
                  {question.choices.map((c, i) => (
                    <li key={i} className={question.correctIndex === i ? "correct" : ""}>
                      <div className="choice-row">
                        <label className="choice-correct" title="Mark as the correct choice">
                          <input
                            type="radio"
                            name={`correct-${pid}`}
                            checked={question.correctIndex === i}
                            onChange={() => setCorrectIndex(i)}
                          />
                          correct
                        </label>
                        <button
                          type="button"
                          className="choice-remove"
                          onClick={() => removeChoice(i)}
                          disabled={question.choices.length <= 1}
                        >
                          Remove
                        </button>
                      </div>
                      <div data-testid="choice-content">
                        <StepEditor content={c.content} onChange={(html) => updateChoice(i, { content: html })} />
                      </div>
                      <p className="muted field-label">Feedback (shown after checking, optional)</p>
                      <div data-testid="choice-feedback">
                        <StepEditor content={c.feedback ?? ""} onChange={(html) => updateChoice(i, { feedback: html || undefined })} />
                      </div>
                    </li>
                  ))}
                </ol>
                <button type="button" onClick={addChoice}>Add choice</button>
              </>
            ) : (
              <p className="muted"><em>No question — this solution is browse-only.</em></p>
            )}
          </section>

          {sol.widgetSlot && (
            <section>
              <h2>Widget slot</h2>
              <p>type: <code>{sol.widgetSlot.type}</code></p>
            </section>
          )}

          <section>
            <h2>Steps ({steps.length})</h2>
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
            Widget slot isn't editable yet — everything else on this page is.
          </p>
        </>
      )}
    </div>
  );
}
