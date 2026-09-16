/**
 * A rough per-subject icon for Hub's subject tiles (IDEAS.org "Change
 * subject list on Hub to be interesting icons/graphics as custom large
 * square buttons"). No real per-subject artwork exists for this
 * content, and the app has deliberately stayed dependency-free on UI
 * libraries (Tailwind + hand-written components, no icon package) — an
 * emoji keyed off the subjectId is a real, distinct, zero-asset glyph
 * that needs no new dependency and nothing to fetch.
 *
 * Matched by subjectId substring (stable, machine-derived — see
 * lib/problemOrder.ts's similar pid-inference rationale) rather than
 * the display title, most-specific pattern first so e.g.
 * "gcprealgpractice" (graphing calculator AND pre-algebra) matches the
 * graphing-calculator icon, not the generic algebra one.
 */
const PATTERNS: [RegExp, string][] = [
  [/graphingcalc|^gcpractice|^gcprealg|^gcgeo/i, "🖩"],
  [/geometry|^geoptests/i, "📐"],
  [/calc(ulus)?/i, "📈"],
  [/placement/i, "📝"],
  [/algebra|ptests/i, "🧮"],
];

/** "📘" (generic book) when nothing more specific matches. */
export function subjectIcon(subjectId: string): string {
  const id = subjectId.toLowerCase();
  for (const [pattern, icon] of PATTERNS) {
    if (pattern.test(id)) return icon;
  }
  return "📘";
}
