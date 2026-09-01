import { useEffect, useState } from "react";
import type { Solution } from "@cm_re/shared-types";
import { getSolution } from "../../offline/moduleManager";
import SolutionNav from "../SolutionNav";
import { solutionTitle } from "../../lib/solutionTitle";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";

type State =
  | { status: "loading" }
  | { status: "found"; solution: Solution }
  | { status: "missing" };

/**
 * Resolves a `#/s/<pid>` deep link: reads the solution out of IndexedDB
 * by pid and hands it to SolutionNav. If the module that contains it
 * isn't installed, `getSolution` returns undefined and we say so rather
 * than rendering a blank — the pid alone doesn't tell us which subject
 * to offer for download (pids aren't reliably subject-prefixed), so the
 * fallback just routes back to the picker.
 */
export default function SolutionLoader({ pid }: { pid: string }) {
  const [state, setState] = useState<State>({ status: "loading" });

  useEffect(() => {
    let cancelled = false;
    setState({ status: "loading" });
    getSolution(pid).then((solution) => {
      if (cancelled) return;
      setState(solution ? { status: "found", solution } : { status: "missing" });
    });
    return () => {
      cancelled = true;
    };
  }, [pid]);

  if (state.status === "loading") {
    return <Spinner />;
  }

  if (state.status === "missing") {
    return (
      <Card>
        <CardContent>
          <p className="mb-1 text-sm font-medium text-slate-900">
            {/* pid alone doesn't carry a subject to strip, but the title
                function is tolerant of that (it just won't strip a prefix). */}
            {solutionTitle(pid, pid.split("_")[0])} — not downloaded
          </p>
          <p className="mb-3 text-sm text-slate-500">
            <code className="break-all">{pid}</code> isn't in any module installed on this device. Download its subject
            first, then open this link again.
          </p>
          <Button variant="outline" onClick={() => navigate(hashFor.subjects())}>
            Choose a subject
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <SolutionNav
      solution={state.solution}
      onBack={() => navigate(hashFor.module(state.solution.subjectId))}
    />
  );
}
