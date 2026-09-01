/**
 * Canonical solution-document shape. This is what the backend stores
 * as a Postgres JSONB row, what `editor` writes, what `tutor` reads
 * and caches in IndexedDB. Keep tutor and editor importing this type
 * rather than redeclaring it, so the shape can't drift between them.
 *
 * Derived from the REAL legacy Java model (not guessed) —
 * hotmath.gwt.solution_editor.server.solution.{TutorSolution,
 * TutorProblem,TutorStepUnit(ImplHint|ImplStep)} — and validated
 * against an actual exported solution
 * (alg1ptests_1_1_Chapter1PracticeTest_10_1, see
 * catchupmath/temp/help_2022_06/solutions/.../version2/ for the full
 * conversion + field-provenance notes). Supersedes an earlier
 * placeholder shape that flattened the problem statement into the
 * steps array and invented "question"/"proof" step roles that don't
 * exist in the legacy model.
 */

export type SolutionId = string;

export interface Identification {
  book: string;
  chapter: string;
  section: string;
  set: string;
  problemNumber: string;
  /** Optional — not every problem has a page reference. */
  page?: string;
}

export interface StepUnit {
  /** Legacy TutorStepUnit.Role is literally just {HINT, STEP} — a stray "QUESSTION"/"PROOF" enum sits unused in TutorSolution.java, never wired to anything. Don't model those as roles. */
  role: "hint" | "step";
  /**
   * Plain text by default; may contain embedded HTML markup
   * (including MathML, <math>...</math>) when richer formatting is
   * needed. Sanitize before render either way — a tagless string
   * passes through unchanged, so this never needs a separate
   * "is this HTML" check.
   */
  content: string;
  /** Single associated image/figure reference, if any. */
  figure?: string;
  figures?: string[];
}

export interface McChoice {
  /**
   * Choice body HTML (may embed MathML / <img>). Sanitize before
   * render. Legacy letter prefixes ("A.", "B.") are stripped during
   * preprocessing — the tutor renders position-based labels itself,
   * so the source's own lettering doesn't double up.
   */
  content: string;
  /**
   * Per-choice feedback, shown only after the student checks. Absent
   * for most of the legacy corpus: the `hm_question_def` dialect
   * carried an empty feedback cell, and the `question_responses`
   * dialect carried only a bare "Correct"/"Incorrect" marker (not
   * real feedback — dropped).
   */
  feedback?: string;
}

/**
 * A single-select multiple-choice question, lifted out of the legacy
 * statement HTML by the preprocessor. The legacy system embedded MC
 * questions as markup inside the statement — two different dialects
 * (`<div class="hm_question_def">` with `<li correct="yes|no">`, and
 * the newer `<div class="question_stepunit">` with
 * `<ul class="question_responses">` radio inputs). The preprocessor
 * normalizes both into this shape so the tutor never has to parse
 * either at runtime. See SOLUTION_INFO.org.
 */
export interface McQuestion {
  /**
   * Prompt HTML — the question text only; the choices list is lifted
   * into `choices`. Sanitize before render.
   */
  prompt: string;
  choices: McChoice[];
  /**
   * 0-based index of the correct choice. Shipped inside the
   * downloaded module so the tutor can check answers offline. The
   * tutor MUST NOT render this — or any per-choice correctness hint —
   * into the DOM before the student checks; keep it in component
   * state only. Absent only when the legacy source marked no choice
   * correct.
   */
  correctIndex?: number;
}

export interface Solution {
  /** Legacy TutorProblem.pid — e.g. "alg1ptests_1_1_chapter1practicetest_10_1". Doubles as our SolutionId. */
  pid: SolutionId;
  /**
   * cm_re's own module-partition key (which downloadable module/
   * subject this belongs to — see ModuleManifest). Distinct from
   * identification.book: book is legacy problem-identification
   * metadata carried through as-is; subjectId is how *we* group
   * content into downloadable modules, and the two don't necessarily
   * match one-to-one.
   */
  subjectId: string;
  /** Legacy TutorSolution.version — a format-version string ("2.0"), NOT the module/content version used for update-detection (ModuleManifest.version). Different concept, same word. */
  version: string;
  /** Real TutorSolution fields, but server/DB-side metadata — not always available (e.g. when converting from a static export that never carried them). */
  date?: string;
  createdBy?: string;
  active?: boolean;
  identification: Identification;
  /**
   * Problem prose, HTML; may embed MathML. Any embedded
   * multiple-choice list is pulled out into `question` by the
   * preprocessor — when `question` is set, `statement` holds only the
   * surrounding text (often nearly empty for practice-test items,
   * which are almost all question).
   */
  statement: string;
  statementFigure?: string;
  /**
   * Structured multiple-choice question, when the legacy statement
   * embedded one (~843/846 of the alg1ptests practice-test corpus;
   * content subjects generally won't have one). Render with the
   * interactive QuestionView component, never as raw HTML.
   */
  question?: McQuestion;
  /** Flat, ordered — legacy TutorProblem.stepUnits is a flat List<TutorStepUnit>, always added as hint+step pairs. */
  steps: StepUnit[];
}

/**
 * One per-subject downloadable bundle. See NEW_DIRECTION.org
 * "Offline support" — the prefetch unit is the module, not the
 * individual solution.
 */
export interface ModuleManifest {
  subjectId: string;
  /** Hash of the bundled solutions' content; client compares against its cached copy to decide whether to re-download. */
  version: string;
  solutionIds: SolutionId[];
  /** Approximate download size in bytes, shown to the student before they commit to downloading on a metered/limited connection. */
  approxSizeBytes: number;
}

/**
 * The actual downloadable payload for one module: every solution the
 * manifest references, in one fetch. `ModuleManifest` alone (small) is
 * fetched first to decide *whether* an update is needed; `ModuleBundle`
 * is only fetched when actually installing/updating.
 *
 * Deliberately does NOT carry its own copy of the manifest — an
 * earlier version did, and having two places that both claim to say
 * "what version is this" let them drift out of sync (editing
 * `bundle.json`'s solutions without also updating its embedded
 * manifest looked like a no-op to `checkForUpdate`, since that only
 * ever reads the standalone `manifest.json`). `manifest.json` is the
 * single source of truth for version/solutionIds/size; `downloadModule`
 * fetches it directly rather than trusting anything embedded here.
 */
export interface ModuleBundle {
  solutions: Solution[];
}

/**
 * One entry in the subject picker — deliberately just enough to render
 * a selection widget, not the full manifest (that's fetched afterward,
 * once a subject is actually chosen).
 */
export interface SubjectSummary {
  subjectId: string;
  title: string;
}
