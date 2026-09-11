import { useEffect, useState } from "react";
import { useHashRoute } from "../routing";
import { listSubjects } from "../api/client";
import { getSolution } from "../offline/moduleManager";

/**
 * The subject the current route is "inside", with its display title —
 * so the shell can show it as a header. null on Hub / Me / the subject
 * pickers (not inside any one subject).
 *
 *   #/m/<id>, #/t/<id>[/…]  -> that subject
 *   #/s/<pid>               -> the solution's own subjectId (from IndexedDB)
 */
export function useCurrentSubject(): { subjectId: string; title: string } | null {
  const route = useHashRoute();
  const [titles, setTitles] = useState<Map<string, string>>(new Map());
  const [pidSubject, setPidSubject] = useState<string | null>(null);

  useEffect(() => {
    let live = true;
    listSubjects()
      .then((list) => {
        if (live) setTitles(new Map(list.map((s) => [s.subjectId, s.title])));
      })
      .catch(() => {
        /* offline before first load — fall back to the id */
      });
    return () => {
      live = false;
    };
  }, []);

  useEffect(() => {
    if (route.kind !== "solution") {
      setPidSubject(null);
      return;
    }
    let live = true;
    getSolution(route.pid).then((s) => {
      if (live) setPidSubject(s?.subjectId ?? null);
    });
    return () => {
      live = false;
    };
  }, [route.kind, route.kind === "solution" ? route.pid : null]);

  let subjectId: string | null = null;
  if (route.kind === "module" || route.kind === "test") subjectId = route.subjectId;
  else if (route.kind === "solution") subjectId = pidSubject;

  if (!subjectId) return null;
  return { subjectId, title: titles.get(subjectId) ?? subjectId };
}
