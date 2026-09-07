import { useEffect, useState } from "react";
import SolutionList from "./components/SolutionList";
import SolutionView from "./components/SolutionView";

/**
 * Top-level shell. Hash routing, no library:
 *   #/            -> SolutionList (browse/search by subject, publish)
 *   #/s/<pid>     -> SolutionView (one solution)
 * Editing (TipTap in StepEditor) lands in the next increment; today
 * SolutionView is read-only.
 */

function currentPid(): string | null {
  const m = window.location.hash.match(/^#\/s\/(.+)$/);
  return m ? decodeURIComponent(m[1]) : null;
}

export default function App() {
  const [pid, setPid] = useState<string | null>(currentPid());

  useEffect(() => {
    const onHash = () => setPid(currentPid());
    window.addEventListener("hashchange", onHash);
    return () => window.removeEventListener("hashchange", onHash);
  }, []);

  return (
    <div className="app">
      <header className="app-header">
        <a href="#/" className="app-title">Catchup Math · Solution Editor</a>
      </header>
      <main className="app-main">
        {pid ? (
          <SolutionView pid={pid} onBack={() => { window.location.hash = "#/"; }} />
        ) : (
          <SolutionList onOpen={(p) => { window.location.hash = `#/s/${encodeURIComponent(p)}`; }} />
        )}
      </main>
    </div>
  );
}
