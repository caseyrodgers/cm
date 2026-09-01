// Throwaway check — confirms every computed link actually resolves
// against the real `make serve-legacy` server, the same URL-building
// logic SolutionNav uses (LEGACY_SERVER_BASE_URL + relativePath + name).
import { solutionSources, LEGACY_SERVER_BASE_URL } from "./solutionSources";

let failures = 0;
for (const [pid, source] of Object.entries(solutionSources)) {
  for (const f of source.files) {
    const url = `${LEGACY_SERVER_BASE_URL}/${source.relativePath}/${encodeURIComponent(f.name)}`;
    const res = await fetch(url);
    console.log(res.status, url);
    if (!res.ok) failures++;
  }
  console.log(`${pid}: ${source.files.length} files checked`);
}

if (failures > 0) {
  throw new Error(`${failures} legacy file link(s) did not resolve`);
}
console.log("ALL LEGACY FILE LINKS RESOLVE");
