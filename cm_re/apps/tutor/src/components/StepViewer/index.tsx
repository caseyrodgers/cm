import DOMPurify from "dompurify";
import type { Solution, StepUnit } from "@cm_re/shared-types";

/**
 * Renders one piece of a solution's author-controlled HTML —
 * `statement` and each step's `content` are both plain strings that
 * may embed HTML markup (including MathML), never trusted as-is.
 * DOMPurify's default config preserves MathML tags (<math>, <mrow>,
 * <mfrac>, ...) without extra configuration — verified against the
 * installed v3 in dompurify-mathml.check.ts before building this.
 */
function SanitizedHtml({ html, className }: { html: string; className?: string }) {
  return <div className={className} dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(html) }} />;
}

export function StatementView({ solution }: { solution: Solution }) {
  return <SanitizedHtml className="prose-sm mb-4 border-b border-slate-200 pb-4" html={solution.statement} />;
}

export function StepUnitView({ step }: { step: StepUnit }) {
  return (
    <div>
      <span
        className={
          step.role === "hint"
            ? "mb-1 inline-block rounded bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-800"
            : "mb-1 inline-block rounded bg-blue-100 px-2 py-0.5 text-xs font-medium text-blue-800"
        }
      >
        {step.role === "hint" ? "Hint" : "Step"}
      </span>
      <SanitizedHtml html={step.content} />
    </div>
  );
}
