// Throwaway check, not part of the app — proves the deep-link path
// works end to end: parseHash maps URLs to routes, and getSolution(pid)
// resolves a real solution out of IndexedDB after a module install.
// Run: npx tsx src/routing.smoketest.ts
// Requires dist/ served on :5173 (make run / npm run preview).
import "fake-indexeddb/auto";
import { parseHash, hashFor } from "./routing";

const BASE = "http://localhost:5173";
const realFetch = fetch;
// @ts-expect-error — resolve the app's relative "/modules/..." paths against the local server.
globalThis.fetch = (input: string, init?: RequestInit) => realFetch(new URL(input, BASE), init);

function assert(cond: unknown, msg: string) {
  if (!cond) throw new Error("FAIL: " + msg);
  console.log("ok —", msg);
}

// --- parseHash / hashFor round-trips ---
assert(parseHash("").kind === "subjects", "empty hash -> subjects");
assert(parseHash("#/").kind === "subjects", "#/ -> subjects");
const m = parseHash("#/m/alg1ptests");
assert(m.kind === "module" && m.subjectId === "alg1ptests", "#/m/alg1ptests -> module(alg1ptests)");
const s = parseHash("#/s/alg1ptests_1_1_chapter1practicetest_10_1");
assert(
  s.kind === "solution" && s.pid === "alg1ptests_1_1_chapter1practicetest_10_1",
  "#/s/<pid> -> solution(<pid>)"
);
assert(hashFor.solution("a b/c") === "#/s/a%20b%2Fc", "hashFor.solution encodes the pid");
assert(parseHash(hashFor.solution("weird pid/2")).kind === "solution", "hashFor.solution round-trips through parseHash");
const t = parseHash("#/t/alg1ptests");
assert(t.kind === "test" && t.subjectId === "alg1ptests", "#/t/<subjectId> -> test(<subjectId>)");
assert(parseHash(hashFor.test("alg1ptests")).kind === "test", "hashFor.test round-trips through parseHash");
assert(parseHash("#/bogus/x").kind === "subjects", "unknown route -> subjects");

// --- getSolution(pid) after a real install ---
const { downloadModule, getSolution } = await import("./offline/moduleManager");
await downloadModule("alg1ptests");

const found = await getSolution("alg1ptests_1_1_chapter1practicetest_10_1");
assert(found, "getSolution returns the installed solution");
assert(found!.subjectId === "alg1ptests", "resolved solution carries its own subjectId (back-nav needs it)");
assert(found!.question && found!.question.choices.length === 4, "resolved solution still has its extracted MC question");

const missing = await getSolution("does_not_exist_pid");
assert(missing === undefined, "getSolution returns undefined for an unknown pid");

console.log("\nDEEP-LINK SMOKE TEST PASSED");
