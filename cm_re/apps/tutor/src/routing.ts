import { useEffect, useState } from "react";

/**
 * Tiny hash-based router. Hash routing (not history/pathname) because
 * the tutor is a static-hosted PWA — `#/s/<pid>` resolves on any static
 * file server with zero rewrite config, survives a hard refresh, and
 * works the same offline. No react-router dependency (the bundle-size
 * budget is tight — see NEW_DIRECTION.org's Ionic removal).
 *
 * Top-level sections (the app shell's nav):
 *   #/                  Hub / home
 *   #/tests             pick a subject for a practice test
 *   #/problems          pick a subject to browse its problems by chapter
 *   #/me                this student's status + reset
 *
 * Within a subject / a solution:
 *   #/m/<subjectId>          a subject's problems (download + chapter list)
 *   #/s/<pid>                one solution, by its globally unique pid
 *   #/t/<subjectId>          a practice test for that subject
 *   #/t/<subjectId>/<pid>    a specific problem in the active "Missed
 *                            Questions Lesson" walkthrough
 */

export type Route =
  | { kind: "hub" }
  | { kind: "tests" }
  | { kind: "problems" }
  | { kind: "me" }
  | { kind: "module"; subjectId: string }
  | { kind: "solution"; pid: string }
  | { kind: "test"; subjectId: string; pid?: string };

export function parseHash(hash: string): Route {
  // Accept "#/m/x", "#m/x", "/m/x", "m/x" — normalise to segments.
  const path = hash.replace(/^#/, "").replace(/^\//, "");
  const segments = path.split("/").filter(Boolean).map(decodeURIComponent);

  if (segments[0] === "m" && segments[1]) {
    return { kind: "module", subjectId: segments[1] };
  }
  if (segments[0] === "s" && segments[1]) {
    return { kind: "solution", pid: segments[1] };
  }
  if (segments[0] === "t" && segments[1]) {
    return { kind: "test", subjectId: segments[1], pid: segments[2] };
  }
  if (segments[0] === "tests") return { kind: "tests" };
  if (segments[0] === "problems") return { kind: "problems" };
  if (segments[0] === "me") return { kind: "me" };
  return { kind: "hub" };
}

export const hashFor = {
  hub: () => "#/",
  tests: () => "#/tests",
  problems: () => "#/problems",
  me: () => "#/me",
  module: (subjectId: string) => `#/m/${encodeURIComponent(subjectId)}`,
  solution: (pid: string) => `#/s/${encodeURIComponent(pid)}`,
  test: (subjectId: string, pid?: string) =>
    pid
      ? `#/t/${encodeURIComponent(subjectId)}/${encodeURIComponent(pid)}`
      : `#/t/${encodeURIComponent(subjectId)}`,
};

/** Navigate by setting the hash — the single source of truth; the hook below re-renders off `hashchange`. */
export function navigate(hash: string): void {
  if (window.location.hash === hash) return;
  window.location.hash = hash;
}

export function useHashRoute(): Route {
  const [route, setRoute] = useState<Route>(() => parseHash(window.location.hash));
  useEffect(() => {
    const onChange = () => setRoute(parseHash(window.location.hash));
    window.addEventListener("hashchange", onChange);
    return () => window.removeEventListener("hashchange", onChange);
  }, []);
  return route;
}
