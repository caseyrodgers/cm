import { useEffect, useMemo, useState } from "react";
import type { Solution } from "@cm_re/shared-types";
import { StatementView, StepUnitView } from "../StepViewer";
import { QuestionView } from "../QuestionView";
import WhiteboardPanel from "../WhiteboardPanel";
import { solutionTitle } from "../../lib/solutionTitle";
import { Button } from "../ui/button";
import { Card, CardContent } from "../ui/card";
import { getSolutionSource, LEGACY_SERVER_BASE_URL, type SolutionSource } from "../../data/solutionSources";

/**
 * Step-by-step navigation over one Solution's steps array. Reads from
 * whatever's already in memory (passed in as a prop) — no network
 * calls here; the caller is responsible for having read the Solution
 * out of IndexedDB already (see ModuleDownloadPrompt).
 */
export default function SolutionNav({ solution, onBack }: { solution: Solution; onBack: () => void }) {
  const [stepIndex, setStepIndex] = useState(0);
  const step = solution.steps[stepIndex];
  const atStart = stepIndex === 0;
  const atEnd = stepIndex === solution.steps.length - 1;

  // Debug affordance: show either the original legacy export
  // directory's file listing (when this solution was actually
  // converted from a real one — see data/solutionSources.ts) or, for
  // synthetic fixture solutions with no real source, the raw stored
  // JSON document instead. Blob URL rather than a data: URI since
  // content can be large enough to hit some browsers' data: URI
  // length limits, and rather than a `file://` link since that's
  // unreliable/blocked from an http://-served page in several browsers.
  const source = getSolutionSource(solution.pid);
  const debugLinkLabel = source ? "View source directory" : "View JSON";
  const debugUrl = useMemo(() => {
    const blob = source
      ? new Blob([renderDirectoryListing(source)], { type: "text/html" })
      : new Blob([JSON.stringify(solution, null, 2)], { type: "application/json" });
    return URL.createObjectURL(blob);
  }, [solution, source]);
  useEffect(() => () => URL.revokeObjectURL(debugUrl), [debugUrl]);

  return (
    <Card>
      <CardContent>
        <div className="mb-3 flex items-center justify-between">
          <button className="text-sm text-blue-600 hover:underline" onClick={onBack}>
            &larr; Back to solutions
          </button>
          <a className="text-sm text-slate-500 hover:underline" href={debugUrl} target="_blank" rel="noreferrer">
            {debugLinkLabel}
          </a>
        </div>

        <h2 className="mb-3 text-base font-semibold text-slate-900">
          {solutionTitle(solution.pid, solution.subjectId)}
        </h2>

        <StatementView solution={solution} />

        {solution.question && (
          // TODO(syncQueue): forward the result to offline/syncQueue.ts
          // once that stops being a stub — this is the first real
          // per-student event the tutor produces.
          <QuestionView
            question={solution.question}
            onAnswer={(r) => console.debug("[QuestionView] answer", solution.pid, r)}
          />
        )}

        {step ? (
          <StepUnitView step={step} />
        ) : (
          <p className="text-sm text-slate-500">This solution has no steps.</p>
        )}

        {solution.steps.length > 0 && (
          <div className="mt-4 flex items-center justify-between">
            <Button variant="outline" onClick={() => setStepIndex((i) => i - 1)} disabled={atStart}>
              &larr; Previous
            </Button>
            <span className="text-xs text-slate-500">
              {stepIndex + 1} / {solution.steps.length}
            </span>
            <Button variant="outline" onClick={() => setStepIndex((i) => i + 1)} disabled={atEnd}>
              Next &rarr;
            </Button>
          </div>
        )}

        {/* Per-solution scratch space — one continuous board. key=pid so
            navigating to another solution unmounts this one (flushing its
            save) and mounts a fresh board. */}
        <WhiteboardPanel key={solution.pid} pid={solution.pid} />
      </CardContent>
    </Card>
  );
}

function renderDirectoryListing(source: SolutionSource): string {
  const rows = source.files
    .map((f) => {
      const url = `${LEGACY_SERVER_BASE_URL}/${source.relativePath}/${encodeURIComponent(f.name)}`;
      return `<tr><td><a href="${url}" target="_blank" rel="noreferrer">${escapeHtml(f.name)}</a></td><td>${f.size.toLocaleString()} bytes</td><td>${escapeHtml(f.modified)}</td></tr>`;
    })
    .join("\n");
  return `<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Index of ${escapeHtml(source.path)}</title>
<style>
  body { font-family: -apple-system, sans-serif; padding: 1.5rem; color: #1f1f1f; }
  h1 { font-size: 1rem; font-weight: 600; word-break: break-all; }
  table { border-collapse: collapse; width: 100%; margin-top: 1rem; }
  td { padding: 0.35rem 0.75rem 0.35rem 0; border-bottom: 1px solid #e5e7eb; font-size: 0.875rem; }
  a { color: #1A99D6; text-decoration: none; }
  .note { color: #7a8593; font-size: 0.8rem; margin-top: 1rem; }
</style>
</head>
<body>
<h1>Index of ${escapeHtml(source.path)}</h1>
<table>
<thead><tr><td><strong>Name</strong></td><td><strong>Size</strong></td><td><strong>Modified</strong></td></tr></thead>
<tbody>
${rows}
</tbody>
</table>
<p class="note">Links point at ${escapeHtml(LEGACY_SERVER_BASE_URL)}, served by <code>make serve-legacy</code> (a dev-only static server rooted at the real legacy solutions tree) — run that first, or these 404. Dev-only debug affordance, not something a student would ever see.</p>
</body>
</html>`;
}

function escapeHtml(s: string): string {
  return s.replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[c]!);
}
