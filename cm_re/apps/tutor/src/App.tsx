import type { Solution } from "@cm_re/shared-types";
import { Button } from "./components/ui/button";
import SubjectSelector from "./components/SubjectSelector";
import ModuleDownloadPrompt from "./components/ModuleDownloadPrompt";
import SolutionLoader from "./components/SolutionLoader";
import PracticeTest from "./components/PracticeTest";
import { useHashRoute, navigate, hashFor } from "./routing";
import { useCorrectTotal } from "./lib/correctCount";

// Top-level shell. The view is a pure function of the URL hash (see
// routing.ts): #/ picker, #/m/<subjectId> a module, #/s/<pid> one
// solution — deep-linkable and refresh-safe.
export default function App() {
  const route = useHashRoute();
  const correctTotal = useCorrectTotal();

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="flex items-center gap-3 border-b border-slate-200 bg-white px-4 py-3">
        {route.kind !== "subjects" && (
          <Button variant="outline" onClick={() => navigate(hashFor.subjects())}>
            &larr;
          </Button>
        )}
        <h1 className="text-lg font-semibold text-slate-900">Catchup Math Tutor</h1>
        <span
          data-testid="correct-total"
          title="Correct answers — all time"
          className="ml-auto inline-flex items-center gap-1 rounded-full bg-green-100 px-2.5 py-1 text-sm font-semibold text-green-800"
        >
          <span aria-hidden>&#10003;</span>
          {correctTotal}
        </span>
      </header>
      <main className="mx-auto max-w-md p-4">
        {route.kind === "subjects" && (
          <SubjectSelector onSelect={(subjectId) => navigate(hashFor.module(subjectId))} />
        )}
        {route.kind === "module" && (
          <ModuleDownloadPrompt
            subjectId={route.subjectId}
            onOpenSolution={(s: Solution) => navigate(hashFor.solution(s.pid))}
          />
        )}
        {route.kind === "solution" && <SolutionLoader pid={route.pid} />}
        {route.kind === "test" && <PracticeTest subjectId={route.subjectId} />}
      </main>
    </div>
  );
}
