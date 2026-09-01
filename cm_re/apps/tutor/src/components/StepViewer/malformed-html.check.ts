// Throwaway check — confirms how DOMPurify/jsdom's HTML parser
// actually handles the malformed markup found in the real corpus
// (SOLUTION_INFO.org, "Malformed legacy HTML, found in the wild"):
// a broken attribute with a missing closing quote.
// Run: npx tsx src/components/StepViewer/malformed-html.check.ts
import { JSDOM } from "jsdom";
import createDOMPurify from "dompurify";

const { window } = new JSDOM("");
const DOMPurify = createDOMPurify(window as unknown as Window & typeof globalThis);

const malformed =
  '<img class="graphimgstatic id="figure-9" src="/help/solutions/alg1ptests/10/1/chapter10practicetest/alg1ptests_10_1_chapter10practicetest_18_10/f-10-18-5.gif"/>';

console.log("input:\n", malformed);
const output = DOMPurify.sanitize(malformed);
console.log("\noutput:\n", output);

// What actually matters: does the <img> survive with a usable src, or
// does the broken quote corrupt/drop the src attribute entirely?
const parsed = new window.DOMParser().parseFromString(output, "text/html");
const img = parsed.querySelector("img");
console.log("\nparsed <img> src:", img?.getAttribute("src"));
console.log("parsed <img> class:", img?.getAttribute("class"));
console.log("parsed <img> id:", img?.getAttribute("id"));

if (!img || img.getAttribute("src") !== "/help/solutions/alg1ptests/10/1/chapter10practicetest/alg1ptests_10_1_chapter10practicetest_18_10/f-10-18-5.gif") {
  throw new Error("the broken attribute corrupted the src — this IS a real problem, not just cosmetic");
}
console.log("\nsrc survived intact — browser/jsdom HTML parsing recovers from this specific malformation");
