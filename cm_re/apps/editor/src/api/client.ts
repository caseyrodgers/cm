import type { Solution } from "@cm_re/shared-types";

/**
 * Thin fetch wrapper for the editor's CRUD API. Unlike the tutor's
 * client, this one assumes connectivity — the editor is online-only.
 * Endpoints are served by the Java TutorServer under /api/editor/*
 * when it's run with CM_EDITOR=1 (see EditorHandler / SolutionSource).
 */

export interface SolutionSummary {
  pid: string;
  subjectId: string;
  identification?: {
    book?: string;
    chapter?: string;
    section?: string;
    set?: string;
    problemNumber?: string;
    page?: string;
  };
  statementPreview: string;
  hasQuestion: boolean;
  hasWidget: boolean;
  stepCount: number;
}

export interface PublishResult {
  subjectId: string;
  version: string;
  solutionIds: string[];
  approxSizeBytes: number;
}

const BASE = "/api/editor";

async function unwrap<T>(res: Response): Promise<T> {
  const text = await res.text();
  if (!res.ok) {
    let msg = text;
    try {
      msg = JSON.parse(text).error ?? text;
    } catch {
      /* keep raw text */
    }
    throw new Error(`${res.status} ${res.statusText} — ${msg}`);
  }
  return JSON.parse(text) as T;
}

export function listSubjects(): Promise<string[]> {
  return fetch(`${BASE}/subjects`).then((r) => unwrap<string[]>(r));
}

export function listSolutions(subject: string): Promise<SolutionSummary[]> {
  return fetch(`${BASE}/solutions?subject=${encodeURIComponent(subject)}`).then((r) =>
    unwrap<SolutionSummary[]>(r)
  );
}

export function getSolution(pid: string): Promise<Solution> {
  return fetch(`${BASE}/solutions/${encodeURIComponent(pid)}`).then((r) => unwrap<Solution>(r));
}

export function saveSolution(doc: Solution): Promise<Solution> {
  return fetch(`${BASE}/solutions/${encodeURIComponent(doc.pid)}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(doc),
  }).then((r) => unwrap<Solution>(r));
}

export function publishModule(subject: string): Promise<PublishResult> {
  return fetch(`${BASE}/modules/${encodeURIComponent(subject)}/publish`, {
    method: "POST",
  }).then((r) => unwrap<PublishResult>(r));
}
