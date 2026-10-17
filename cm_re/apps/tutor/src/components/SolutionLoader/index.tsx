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
  | { status: "found"; solution: Solution; live?: boolean }
  | { status: "missing" };

async function fetchLive(pid: string): Promise<Solution | undefined> {
  try {
    const res = await fetch(`/api/editor/solutions/${encodeURIComponent(pid)}`);
    if (!res.ok) return undefined;
    return await res.json();
  } catch {
    return undefined;
  }
}

export default function SolutionLoader({ pid }: { pid: string }) {
  const [state, setState] = useState<State>({ status: "loading" });

  useEffect(() => {
    let cancelled = false;
    setState({ status: "loading" });
    fetchLive(pid).then(async (live) => {
      if (cancelled) return;
      if (live) {
        setState({ status: "found", solution: live, live: true });
        return;
      }
      const cached = await getSolution(pid);
      if (cancelled) return;
      setState(cached ? { status: "found", solution: cached } : { status: "missing" });
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
          <Button variant="outline" onClick={() => navigate(hashFor.problems())}>
            Choose a subject
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <SolutionNav
      key={state.solution.pid}
      solution={state.solution}
      onBack={() => navigate(hashFor.module(state.solution.subjectId))}
    />
  );
}
