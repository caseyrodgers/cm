import type { Solution } from "@cm_re/shared-types";
import SubjectSelector from "./components/SubjectSelector";
import ModuleDownloadPrompt from "./components/ModuleDownloadPrompt";
import SolutionLoader from "./components/SolutionLoader";
import PracticeTest from "./components/PracticeTest";
import Hub from "./components/Hub";
import StudentStatus from "./components/StudentStatus";
import DialogHost from "./components/DialogHost";
import { useHashRoute, navigate, hashFor } from "./routing";
import { activeShellId } from "./lib/shell";
import { SHELLS } from "./shells";

// The content is one thing; the shell (header/footer/nav) is swappable
// via ?shell=<id> — see lib/shell.ts and shells/. App just parses the
// route, picks the shell, and renders the routed content inside it.
export default function App() {
  const route = useHashRoute();
  const Shell = SHELLS[activeShellId()] ?? SHELLS.default;

  return (
    <>
      <Shell>{renderRoute(route)}</Shell>
      <DialogHost />
    </>
  );
}

function renderRoute(route: ReturnType<typeof useHashRoute>) {
  switch (route.kind) {
    case "hub":
      return <Hub />;
    case "tests":
      return (
        <SubjectPicker heading="Practice test — pick a subject" onSelect={(id) => navigate(hashFor.test(id))} />
      );
    case "problems":
      return (
        <SubjectPicker heading="Problems — pick a subject" onSelect={(id) => navigate(hashFor.module(id))} />
      );
    case "me":
      return <StudentStatus />;
    case "module":
      return (
        <ModuleDownloadPrompt
          subjectId={route.subjectId}
          onOpenSolution={(s: Solution) => navigate(hashFor.solution(s.pid))}
        />
      );
    case "solution":
      return <SolutionLoader pid={route.pid} />;
    case "test":
      return <PracticeTest subjectId={route.subjectId} pid={route.pid} />;
  }
}

function SubjectPicker({ heading, onSelect }: { heading: string; onSelect: (subjectId: string) => void }) {
  return (
    <div>
      <h2 className="mb-2 text-base font-medium text-slate-700">{heading}</h2>
      <SubjectSelector onSelect={onSelect} />
    </div>
  );
}
