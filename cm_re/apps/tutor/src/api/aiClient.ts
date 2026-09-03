import type { Solution } from "@cm_re/shared-types";

/**
 * "Learn" — AI explanation of the current problem, tuned to a grade
 * level ("tell me like a 7th grader").
 *
 * explainProblem() calls the backend
 * (com.catchupmath.cmre.server, GET /api/ai/problem/{pid}?grade=<n>),
 * which does a live Claude call and returns { pid, text, placeholder }.
 * `pid` and `grade` are sent; `title` / `problemText` are still only
 * for the eventual pre-generation path (the server composes its own
 * problem text from the served bundles).
 */

const AI_BASE = "/api/ai";

export type Grade = "7" | "10" | "12";

/** A few representative levels rather than every grade — 7th / 10th / 12th. */
export const GRADES: Grade[] = ["7", "10", "12"];

/** "7th Grade" / "10th Grade" / "12th Grade". */
export function gradeLabel(g: Grade): string {
  return `${g}th Grade`;
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

/** Rough plain text from a solution's statement + steps, for the request payload. */
export function problemTextOf(solution: Solution): string {
  const parts = [solution.statement, ...solution.steps.map((s) => s.content)];
  return parts
    .join("\n")
    .replace(/<[^>]+>/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}
