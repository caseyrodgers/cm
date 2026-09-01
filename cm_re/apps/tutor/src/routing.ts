import { useEffect, useState } from "react";

/**
 * Tiny hash-based router. Hash routing (not history/pathname) because
 * the tutor is a static-hosted PWA — `#/s/<pid>` resolves on any static
 * file server with zero rewrite config, survives a hard refresh, and
 * works the same offline. No react-router dependency (the bundle-size
 * budget is tight — see NEW_DIRECTION.org's Ionic removal).
 *
 * Routes:
 *   #/                  subject picker
 *   #/m/<subjectId>     one subject's module (full download / solution list)
 *   #/s/<pid>           jump straight to one solution, by its globally
 *                       unique pid (SolutionLoader reads it from
 *                       IndexedDB and derives its subject)
 *   #/t/<subjectId>     a 10-question practice test for that subject
 *                       (PracticeTest handles its own index/question/
 *                       score sub-views internally)
 */

export type Route =
  | { kind: "subjects" }
  | { kind: "module"; subjectId: string }
  | { kind: "solution"; pid: string }
  | { kind: "test"; subjectId: string };

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
    return { kind: "test", subjectId: segments[1] };
  }
  return { kind: "subjects" };
}

export const hashFor = {
  subjects: () => "#/",
  module: (subjectId: string) => `#/m/${encodeURIComponent(subjectId)}`,
  solution: (pid: string) => `#/s/${encodeURIComponent(pid)}`,
  test: (subjectId: string) => `#/t/${encodeURIComponent(subjectId)}`,
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
