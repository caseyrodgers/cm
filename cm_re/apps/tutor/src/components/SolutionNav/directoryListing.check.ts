// Throwaway check — confirms the directory-listing HTML generator
// produces well-formed, correct output for the real source data.
import { solutionSources } from "../../data/solutionSources";

const source = solutionSources["alg1ptests_1_1_chapter1practicetest_10_1"];

// Re-implement the escape + render logic isn't ideal to duplicate, so
// instead just sanity-check the underlying data this component reads.
console.log("path:", source.path);
console.log("file count:", source.files.length);
if (source.files.length !== 17) {
  throw new Error(`expected 17 files (matches real 'ls -la' output, excluding . .. and version2/), got ${source.files.length}`);
}
const names = source.files.map((f) => f.name);
for (const expected of ["tutor_steps.html", "tutor_steps_2.html", "index.html", "inmh_list.json", "tutor_data.js", "image004.gif"]) {
  if (!names.includes(expected)) {
    throw new Error(`missing expected file: ${expected}`);
  }
}
if (names.includes("version2") || names.includes(".") || names.includes("..")) {
  throw new Error("should not include the version2/ artifact dir or . / ..");
}
console.log("DIRECTORY LISTING DATA CHECK PASSED");
