// Throwaway check, not part of the app — confirms DOMPurify (v3, the
// installed dependency) preserves MathML tags rather than silently
// stripping them, run against the ACTUAL real converted solution
// content (not just a synthetic example), before trusting StepViewer /
// QuestionView to render it correctly.
// Run: npx tsx src/components/StepViewer/dompurify-mathml.check.ts
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { JSDOM } from "jsdom";
import createDOMPurify from "dompurify";

const { window } = new JSDOM("");
const DOMPurify = createDOMPurify(window as unknown as Window & typeof globalThis);

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const bundlePath = path.resolve(__dirname, "../../../public/modules/algebra1/bundle.json");
const bundle = JSON.parse(readFileSync(bundlePath, "utf-8"));
const real = bundle.solutions.find((s: any) => s.pid === "alg1ptests_1_1_chapter1practicetest_10_1");
if (!real) throw new Error("expected the real converted solution in the algebra1 fixture");

function checkPreserved(label: string, html: string) {
  const output = DOMPurify.sanitize(html);
  const mustSurvive = ["<math", "<mfrac", "<mrow", "<mi", "<mo", "<mn"];
  const missing = mustSurvive.filter((tag) => html.toLowerCase().includes(tag) && !output.toLowerCase().includes(tag));
  console.log(`--- ${label} ---`);
  console.log("input length:", html.length, "| output length:", output.length);
  if (missing.length > 0) {
    throw new Error(`${label}: DOMPurify stripped: ${missing.join(", ")}`);
  }
  console.log("OK — MathML preserved");
}

// The multiple-choice question now lives in `real.question` (the
// preprocessor lifts it out of the statement — see LegacySolutionParser
// / shared-types' McQuestion), so the MathML that used to sit inside the
// statement's <ul><li> list is now spread across question.prompt and
// each choice's content. Check all of it.
if (!real.question) throw new Error("expected the real solution's MC question to be extracted into `real.question`");
if (/<ul|<li/i.test(real.statement)) {
  throw new Error("the MC list should be gone from `statement` now that it's structured in `question`");
}

checkPreserved("statement", real.statement);
checkPreserved("question.prompt", real.question.prompt);
real.question.choices.forEach((choice: any, i: number) => {
  checkPreserved(`question.choices[${i}].content`, choice.content);
});
for (const step of real.steps) {
  checkPreserved(`step (${step.role})`, step.content);
}

console.log("\nALL REAL CONTENT SURVIVES SANITIZATION");
