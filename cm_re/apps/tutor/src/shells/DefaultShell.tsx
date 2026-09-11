import type { ReactNode } from "react";
import { useHashRoute, navigate, hashFor } from "../routing";
import { useCorrectTotal } from "../lib/correctCount";
import { useCurrentSubject } from "../lib/useCurrentSubject";

/**
 * The default app shell — header nav (Hub / Practice Tests / Problems /
 * Me) + a running "✓ N" correct-answer pill + a thin footer. The
 * content is passed as `children`; the shell knows nothing about it
 * beyond the current section (from the route) for nav highlighting.
 *
 * Swap shells with `?shell=<id>` — see lib/shell.ts and shells/index.ts.
 */

type Section = "hub" | "tests" | "problems" | "me";

const NAV: { section: Section; label: string; to: string }[] = [
  { section: "hub", label: "Hub", to: hashFor.hub() },
  { section: "tests", label: "Practice Tests", to: hashFor.tests() },
  { section: "problems", label: "Problems", to: hashFor.problems() },
  { section: "me", label: "Me", to: hashFor.me() },
];

/** Which top-level section a route belongs under (for nav highlight + the up-arrow). */
function sectionOf(kind: string): Section | null {
  switch (kind) {
    case "hub":
      return "hub";
    case "tests":
      return "tests";
    case "test":
      return "tests";
    case "problems":
      return "problems";
    case "module":
    case "solution":
      return "problems";
    case "me":
      return "me";
    default:
      return null;
  }
}

export default function DefaultShell({ children }: { children: ReactNode }) {
  const route = useHashRoute();
  const total = useCorrectTotal();
  const subject = useCurrentSubject();
  const section = sectionOf(route.kind);
  const deep = route.kind === "module" || route.kind === "solution" || route.kind === "test";

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-3xl items-center gap-3 px-4 py-3">
          {deep && (
            <button
              aria-label="Up a level"
              onClick={() => navigate(section ? NAV.find((n) => n.section === section)!.to : hashFor.hub())}
              className="rounded-md border border-slate-300 px-2 py-1 text-sm text-slate-600 hover:bg-slate-50"
            >
              &larr;
            </button>
          )}
          <button
            onClick={() => navigate(hashFor.hub())}
            className="text-lg font-semibold text-slate-900"
          >
            Catchup Math
          </button>
          <span
            data-testid="correct-total"
            title="Correct answers — all time"
            className="ml-auto inline-flex items-center gap-1 rounded-full bg-green-100 px-2.5 py-1 text-sm font-semibold text-green-800"
          >
            <span aria-hidden>&#10003;</span>
            {total}
          </span>
        </div>
        <nav className="mx-auto flex max-w-3xl gap-1 px-3 pb-1 text-sm">
          {NAV.map((n) => (
            <button
              key={n.section}
              onClick={() => navigate(n.to)}
              aria-current={section === n.section ? "page" : undefined}
              className={
                "rounded-md px-3 py-1.5 font-medium transition-colors " +
                (section === n.section
                  ? "bg-slate-900 text-white"
                  : "text-slate-600 hover:bg-slate-100")
              }
            >
              {n.label}
            </button>
          ))}
        </nav>
        {subject && (
          <div
            data-testid="subject-header"
            className="border-t border-slate-100 bg-slate-50 px-4 py-1.5"
          >
            <span className="mx-auto block max-w-3xl text-sm font-semibold text-slate-900">
              {subject.title}
            </span>
          </div>
        )}
      </header>

      <main className="mx-auto w-full max-w-3xl flex-1 p-4">{children}</main>

      <footer className="border-t border-slate-200 bg-white px-4 py-3 text-center text-xs text-slate-400">
        Catchup Math &middot; works offline &middot; your progress stays on this device
      </footer>
    </div>
  );
}
