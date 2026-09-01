/**
 * Maps a real, converted Solution's pid back to its original legacy
 * export directory — captured once, by hand, from the actual
 * directory (`ls -la`) — so SolutionNav's debug link can point at
 * real, clickable files (images included) instead of a dead end.
 * Only solutions actually converted from a real export have an entry
 * here — the synthetic fixture solutions (sol-linear-eq-1, etc.)
 * never had a source directory.
 *
 * Files are served by `make serve-legacy` (see cm_re/Makefile) — a
 * dev-only static server rooted at the legacy `solutions/` tree,
 * separate from the tutor app's own server. Not a `file://` link:
 * browsers can't read arbitrary local directories from JS, and
 * `file://` navigation from an http://-served page is unreliable/
 * blocked in several browsers. Not a live directory listing from that
 * server either — this exact directory has its own legacy
 * `index.html` (a redirect stub), which the server would serve
 * instead of a listing if you browsed straight to the folder; this
 * hand-built listing sidesteps that by linking each file directly.
 *
 * Captured 2026-08-25; excludes `version2/` (our own conversion
 * artifact, not part of the original legacy export).
 */

export const LEGACY_SERVER_BASE_URL = "http://localhost:8090";

export interface SourceFile {
  name: string;
  size: number;
  modified: string;
}

export interface SolutionSource {
  /** Display path, for humans reading the listing. */
  path: string;
  /** Path relative to LEGACY_SERVER_BASE_URL — i.e. relative to the `solutions/` dir `make serve-legacy` serves. */
  relativePath: string;
  files: SourceFile[];
}

export const solutionSources: Record<string, SolutionSource> = {
  alg1ptests_1_1_chapter1practicetest_10_1: {
    path: "catchupmath/temp/help_2022_06/solutions/alg1ptests/1/1/Chapter1PracticeTest/alg1ptests_1_1_Chapter1PracticeTest_10_1",
    relativePath: "alg1ptests/1/1/Chapter1PracticeTest/alg1ptests_1_1_Chapter1PracticeTest_10_1",
    files: [
      { name: "image004.gif", size: 109, modified: "2013-06-08" },
      { name: "image025.gif", size: 220, modified: "2013-06-08" },
      { name: "image026.gif", size: 133, modified: "2013-06-08" },
      { name: "image027.gif", size: 135, modified: "2013-06-08" },
      { name: "image028.gif", size: 111, modified: "2013-06-08" },
      { name: "image029.gif", size: 291, modified: "2013-06-08" },
      { name: "image030.gif", size: 249, modified: "2013-06-08" },
      { name: "image031.gif", size: 274, modified: "2013-06-08" },
      { name: "image032.gif", size: 217, modified: "2013-06-08" },
      { name: "image033.gif", size: 411, modified: "2013-06-08" },
      { name: "image034.gif", size: 184, modified: "2013-06-08" },
      { name: "image035.gif", size: 225, modified: "2013-06-08" },
      { name: "index.html", size: 176, modified: "2015-08-15" },
      { name: "inmh_list.json", size: 983, modified: "2013-06-08" },
      { name: "tutor_data.js", size: 656, modified: "2015-08-15" },
      { name: "tutor_steps.html", size: 2540, modified: "2013-06-08" },
      { name: "tutor_steps_2.html", size: 2679, modified: "2015-08-15" },
    ],
  },
};

export function getSolutionSource(pid: string): SolutionSource | undefined {
  return solutionSources[pid];
}
