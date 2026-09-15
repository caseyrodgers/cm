import type { Solution } from "@cm_re/shared-types";

/**
 * "Learn" — AI explanation of the current problem, tuned to a grade
 * level ("tell me like I'm a 7th grader").
 *
 * explainProblem() calls the backend
 * (com.catchupmath.cmre.server, GET /api/ai/problem/{pid}?grade=<n>),
 * which does a live Claude call and returns { pid, text, placeholder }.
 * `pid` and `grade` are sent; `title` / `problemText` are still only
 * for the eventual pre-generation path (the server composes its own
 * problem text from the served bundles).
 *
 * checkWork() is the whiteboard's "Ask AI about my work" — POSTs a PNG
 * of the student's scratch work (POST /api/ai/check-work/{pid}) and
 * gets back qualitative feedback on whether it shows understanding.
 *
 * readWork() is "Read back what I wrote" — same PNG capture, but a pure
 * transcription (POST /api/ai/read-work/{pid}): no judgment, just what
 * the handwritten numbers/math actually say, typed out.
 *
 * buildSkeleton() is "Sketch a starting point" (GET /api/ai/skeleton/
 * {pid}) — asks for the problem's given information back as a short
 * list of basic shapes (Shape below), which the whiteboard converts
 * into its own native strokes and draws. Nothing is sent from the
 * client beyond the pid — no request body, no vision call from here —
 * but the server may itself attach the problem's own images (a figure
 * authored as a picture rather than text) when composing its prompt,
 * so a redrawn geometric figure can trace what's actually shown rather
 * than a generic shape. Either way the response is structured JSON.
 */

const AI_BASE = "/api/ai";

export type Grade = "7" | "10" | "12";

/** A few representative levels rather than every grade — 7th / 10th / 12th. */
export const GRADES: Grade[] = ["7", "10", "12"];

/** "7th Grader" / "10th Grader" / "12th Grader". */
export function gradeLabel(g: Grade): string {
  return `${g}th Grader`;
}

export interface ExplainRequest {
  pid: string;
  /** Human problem title (see lib/solutionTitle). Not sent — for the eventual pre-generation path. */
  title: string;
  /** Plain-text-ish of the problem statement. Not sent — the server composes its own from the bundles. */
  problemText: string;
  /** Sent as ?grade= so the explanation is pitched to this level. */
  grade: Grade;
}

export interface ExplainResult {
  text: string;
  /** true while the backend is returning a stub — the UI surfaces it so nobody mistakes it for a real answer. */
  placeholder: boolean;
}

export class ExplainAbortError extends Error {
  constructor() {
    super("explain aborted");
    this.name = "ExplainAbortError";
  }
}

export async function explainProblem(req: ExplainRequest, signal?: AbortSignal): Promise<ExplainResult> {
  let res: Response;
  try {
    const url = `${AI_BASE}/problem/${encodeURIComponent(req.pid)}?grade=${encodeURIComponent(req.grade)}`;
    res = await fetch(url, { signal });
  } catch (e) {
    if (isAbort(e)) throw new ExplainAbortError();
    throw new Error(`explain request failed: ${String(e)}`);
  }
  if (!res.ok) {
    throw new Error(`explain request failed: ${res.status}`);
  }
  const data = (await res.json()) as { text?: string; placeholder?: boolean };
  return {
    text: typeof data.text === "string" ? data.text : "",
    placeholder: data.placeholder === true,
  };
}

function isAbort(e: unknown): boolean {
  return e instanceof DOMException && e.name === "AbortError";
}

export interface CheckWorkResult {
  /** Qualitative feedback on whether the whiteboard work shows understanding — HTML fragment, same MathML convention as explainProblem's text. Not a correct/incorrect verdict. */
  feedback: string;
  placeholder: boolean;
}

/**
 * "Ask AI about my work" (POST /api/ai/check-work/{pid}) — sends a
 * base64 PNG of the student's whiteboard, gets back qualitative
 * feedback on whether it shows understanding of the problem. The
 * server composes the problem context itself (same as explainProblem);
 * only the pid + image are sent.
 */
export async function checkWork(
  pid: string,
  imageBase64: string,
  signal?: AbortSignal
): Promise<CheckWorkResult> {
  let res: Response;
  try {
    const url = `${AI_BASE}/check-work/${encodeURIComponent(pid)}`;
    res = await fetch(url, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ image: imageBase64 }),
      signal,
    });
  } catch (e) {
    if (isAbort(e)) throw new ExplainAbortError();
    throw new Error(`check-work request failed: ${String(e)}`);
  }
  if (!res.ok) {
    throw new Error(`check-work request failed: ${res.status}`);
  }
  const data = (await res.json()) as { feedback?: string; placeholder?: boolean };
  return {
    feedback: typeof data.feedback === "string" ? data.feedback : "",
    placeholder: data.placeholder === true,
  };
}

export interface ReadWorkResult {
  /** Plain-text readback of the handwritten numbers/math on the board (e.g. "x = 7") — a pure transcription, not an assessment. Empty/placeholder text when the board is illegible or the service is unavailable. */
  transcription: string;
  placeholder: boolean;
}

/**
 * "Snap" mode — POSTs a base64 PNG of the whiteboard and gets back a
 * plain-text transcription of the handwritten numbers/math, nothing
 * more (no correctness judgment — see checkWork for that). Same
 * capture path as checkWork; different server-side prompt.
 */
export async function readWork(
  pid: string,
  imageBase64: string,
  signal?: AbortSignal
): Promise<ReadWorkResult> {
  let res: Response;
  try {
    const url = `${AI_BASE}/read-work/${encodeURIComponent(pid)}`;
    res = await fetch(url, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ image: imageBase64 }),
      signal,
    });
  } catch (e) {
    if (isAbort(e)) throw new ExplainAbortError();
    throw new Error(`read-work request failed: ${String(e)}`);
  }
  if (!res.ok) {
    throw new Error(`read-work request failed: ${res.status}`);
  }
  const data = (await res.json()) as { transcription?: string; placeholder?: boolean };
  return {
    transcription: typeof data.transcription === "string" ? data.transcription : "",
    placeholder: data.placeholder === true,
  };
}

export interface ChapterNameResult {
  /** Deduced topic name, e.g. "Linear Equations" — "" when unavailable (no key, no sample problems, API error). */
  name: string;
  placeholder: boolean;
}

/**
 * Deduces a short topic name for a chapter from a sample of its own
 * problems (GET /api/ai/chapter-name/{subjectId}). The legacy export
 * carries no chapter-title field anywhere, so this is inference from
 * real content via Claude, not a lookup — see AiService.getChapterName.
 */
export async function inferChapterName(
  subjectId: string,
  chapterLabel: string,
  samplePids: string[],
  signal?: AbortSignal
): Promise<ChapterNameResult> {
  const pidsParam = samplePids.slice(0, 3).map(encodeURIComponent).join(",");
  const url = `${AI_BASE}/chapter-name/${encodeURIComponent(subjectId)}?label=${encodeURIComponent(chapterLabel)}&pids=${pidsParam}`;
  let res: Response;
  try {
    res = await fetch(url, { signal });
  } catch (e) {
    if (isAbort(e)) throw new ExplainAbortError();
    throw new Error(`chapter-name request failed: ${String(e)}`);
  }
  if (!res.ok) {
    throw new Error(`chapter-name request failed: ${res.status}`);
  }
  const data = (await res.json()) as { name?: string; placeholder?: boolean };
  return {
    name: typeof data.name === "string" ? data.name : "",
    placeholder: data.placeholder === true,
  };
}

/** A basic drawing primitive the server describes; the whiteboard converts these into its own native Stroke[]. Coordinates are in the same logical space the whiteboard already draws in. */
export type Shape =
  | { type: "line"; from: [number, number]; to: [number, number] }
  | { type: "polyline"; points: [number, number][] }
  | { type: "circle"; center: [number, number]; radius: number }
  | { type: "text"; at: [number, number]; text: string };

export interface BuildSkeletonResult {
  shapes: Shape[];
  /** Why shapes is empty, when it is — "" on a normal successful response with content. */
  message: string;
  placeholder: boolean;
}

/**
 * "Sketch a starting point" (GET /api/ai/skeleton/{pid}) — the given
 * information (the equation as stated, a described or pictured
 * figure's given values, blank axes for a graphing problem) back as a
 * short list of shapes, never a solution or the answer. Malformed/
 * unrecognized shapes are already filtered out server-side; what
 * comes back here is safe to convert and draw as-is.
 */
export async function buildSkeleton(pid: string, signal?: AbortSignal): Promise<BuildSkeletonResult> {
  let res: Response;
  try {
    res = await fetch(`${AI_BASE}/skeleton/${encodeURIComponent(pid)}`, { signal });
  } catch (e) {
    if (isAbort(e)) throw new ExplainAbortError();
    throw new Error(`skeleton request failed: ${String(e)}`);
  }
  if (!res.ok) {
    throw new Error(`skeleton request failed: ${res.status}`);
  }
  const data = (await res.json()) as { shapes?: unknown; message?: string; placeholder?: boolean };
  return {
    shapes: Array.isArray(data.shapes) ? (data.shapes as Shape[]) : [],
    message: typeof data.message === "string" ? data.message : "",
    placeholder: data.placeholder === true,
  };
}

/** Rough plain text from a solution's statement + steps, for the request payload. */
export function problemTextOf(solution: Solution): string {
  const parts = [solution.statement, ...solution.steps.map((s) => s.content)];
  return parts
    .join("\n")
    .replace(/<[^>]+>/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}
