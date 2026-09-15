// Throwaway check, not part of the app — before designing the StepEditor,
// confirm empirically whether TipTap's default schema (StarterKit, no
// custom nodes) preserves embedded MathML on a load->edit->save round
// trip, or silently destroys it. Run against REAL step content from the
// alg1ptests corpus, not a synthetic example.
// Run: npx tsx src/components/StepEditor/mathml-survival.check.ts
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { JSDOM } from "jsdom";

const dom = new JSDOM("<!doctype html><html><body></body></html>");
(globalThis as any).window = dom.window;
(globalThis as any).document = dom.window.document;
(globalThis as any).DOMParser = dom.window.DOMParser;
(globalThis as any).Node = dom.window.Node;

const { generateHTML, generateJSON } = await import("@tiptap/core");
const { default: StarterKit } = await import("@tiptap/starter-kit");
const { MathNode } = await import("./mathNode");

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const bundlePath = path.resolve(__dirname, "../../../../tutor/public/modules/alg1ptests/bundle.json");
const bundle = JSON.parse(readFileSync(bundlePath, "utf-8"));
const withMath = bundle.solutions.find(
  (s: any) => s.steps?.some((st: any) => /<math/i.test(st.content))
);
if (!withMath) throw new Error("expected at least one alg1ptests solution with a <math> step");

const step = withMath.steps.find((st: any) => /<math/i.test(st.content));
console.log("pid:", withMath.pid);
console.log("--- INPUT ---");
console.log(step.content);

// generateHTML(html, extensions) doesn't exist directly — TipTap's helper
// takes ProseMirror JSON in, HTML out. To simulate "load into the editor,
// then read back its HTML" (what StepEditor would actually do), parse the
// HTML into the editor's schema via a real Editor-less path: use
// generateJSON (HTML -> ProseMirror JSON) then generateHTML (JSON -> HTML)
// — exactly what mounting <EditorContent> with `content: html` does
// under the hood (ProseMirror's DOMParser.parse, schema-constrained).
function roundTrip(html: string, extensions: any[]): string {
  const json = generateJSON(html, extensions);
  return generateHTML(json, extensions);
}

const mathTags = ["<math", "<mfrac", "<mrow", "<mi", "<mo", "<mn", "<msup", "<msub"];
function report(label: string, input: string, output: string) {
  const present = mathTags.filter((t) => input.toLowerCase().includes(t));
  const lost = present.filter((t) => !output.toLowerCase().includes(t));
  console.log(`\n--- ${label} ---`);
  console.log(output);
  console.log("tags present in input:", present);
  console.log("tags LOST:", lost);
  return lost;
}

const bare = roundTrip(step.content, [StarterKit]);
const lostBare = report("bare StarterKit (no MathNode)", step.content, bare);
if (lostBare.length === 0) {
  throw new Error("expected bare StarterKit to lose MathML — assumptions changed, re-check");
}
console.log("\n=> CONFIRMED: plain StarterKit schema silently destroys embedded MathML.");

const guarded = roundTrip(step.content, [StarterKit, MathNode]);
const lostGuarded = report("StarterKit + MathNode", step.content, guarded);
if (lostGuarded.length > 0) {
  throw new Error(`MathNode still lost: ${lostGuarded.join(", ")}`);
}
console.log("\n=> FIXED: with MathNode registered, every MathML tag survives the round trip.");
