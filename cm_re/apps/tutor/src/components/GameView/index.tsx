import { useEffect, useRef, useState } from "react";
import { GAMES } from "../../games/registry";
import { gameUrl, type GameContext } from "../../games/matching";
import { navigate, hashFor } from "../../routing";
import { getInstalledModules } from "../../offline/moduleManager";
import { listSubjects } from "../../api/client";
import {
  GRADES,
  gradeLabel,
  explainGame,
  ExplainAbortError,
  type Grade,
  type ExplainResult,
} from "../../api/aiClient";
import { SanitizedHtml } from "../StepViewer";
import { Spinner } from "../ui/spinner";
import type { InstalledModule } from "../../offline/db";

interface SubjectMatch {
  subjectId: string;
  title: string;
  overlap: number;
}

function stem(w: string): string {
  if (w.endsWith("ing")) return w.slice(0, -3);
  if (w.endsWith("s")) return w.slice(0, -1);
  return w;
}

function skillsOverlap(gameSkill: string, chapterSkill: string): boolean {
  if (gameSkill === chapterSkill) return true;
  if (gameSkill.includes(chapterSkill) || chapterSkill.includes(gameSkill)) return true;
  const gw = gameSkill.split("-").filter((w) => w.length >= 3).map(stem);
  const cw = new Set(chapterSkill.split("-").filter((w) => w.length >= 3).map(stem));
  return gw.some((w) => cw.has(w));
}

function subjectsForGame(
  game: typeof GAMES[number],
  modules: InstalledModule[],
  titles: Record<string, string>,
): SubjectMatch[] {
  const matches: SubjectMatch[] = [];

  for (const mod of modules) {
    let count = 0;
    for (const ch of mod.chapters ?? []) {
      for (const cs of ch.skills ?? []) {
        for (const gs of game.skills) {
          if (skillsOverlap(gs, cs)) { count++; break; }
        }
      }
    }
    if (count > 0) {
      matches.push({
        subjectId: mod.subjectId,
        title: titles[mod.subjectId] ?? mod.subjectId,
        overlap: count,
      });
    }
  }

  matches.sort((a, b) => b.overlap - a.overlap);
  return matches;
}

const GRADE_KEY = "cm_re.learn.grade";

function loadGrade(): Grade | null {
  try {
    const v = localStorage.getItem(GRADE_KEY);
    return (GRADES as string[]).includes(v ?? "") ? (v as Grade) : null;
  } catch { return null; }
}

type ExplainStatus = "idle" | "loading" | "done" | "error";

export default function GameView({ slug, ctx }: { slug: string; ctx?: GameContext }) {
  const game = GAMES.find((g) => g.slug === slug);
  const headerRef = useRef<HTMLDivElement>(null);
  const [iframeHeight, setIframeHeight] = useState<number>(0);
  const [subjects, setSubjects] = useState<SubjectMatch[]>([]);

  const [currentProblem, setCurrentProblem] = useState<string | null>(null);
  const [explainOpen, setExplainOpen] = useState(false);
  const [grade, setGrade] = useState<Grade | null>(loadGrade);
  const [explainStatus, setExplainStatus] = useState<ExplainStatus>("idle");
  const [explainResult, setExplainResult] = useState<ExplainResult | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  useEffect(() => {
    if (!game) return;
    Promise.all([
      getInstalledModules(),
      listSubjects().catch(() => []),
    ]).then(([mods, subs]) => {
      const titles: Record<string, string> = {};
      for (const s of subs) titles[s.subjectId] = s.title;
      setSubjects(subjectsForGame(game, mods, titles));
    });
  }, [game]);

  useEffect(() => {
    function measure() {
      if (!headerRef.current) return;
      const bottom = headerRef.current.getBoundingClientRect().bottom;
      setIframeHeight(window.innerHeight - bottom);
    }
    measure();
    window.addEventListener("resize", measure);
    return () => window.removeEventListener("resize", measure);
  });

  useEffect(() => {
    function onMessage(e: MessageEvent) {
      if (e.data?.type === "cm_game_problem") {
        const problem = typeof e.data.problem === "string" ? e.data.problem : null;
        setCurrentProblem(problem);
        if (!problem) {
          setExplainOpen(false);
          setExplainStatus("idle");
          setExplainResult(null);
          abortRef.current?.abort();
        }
      }
      if (e.data?.type === "cm_game_score") {
        // future: record score, tie into streak/stats
      }
    }
    window.addEventListener("message", onMessage);
    return () => window.removeEventListener("message", onMessage);
  }, []);

  useEffect(() => () => abortRef.current?.abort(), []);

  function pickGrade(g: Grade) {
    setGrade(g);
    try { localStorage.setItem(GRADE_KEY, g); } catch { /* ok */ }
    doExplain(g);
  }

  async function doExplain(g: Grade) {
    if (!currentProblem) return;
    abortRef.current?.abort();
    const ac = new AbortController();
    abortRef.current = ac;
    setExplainStatus("loading");
    setExplainResult(null);
    try {
      const res = await explainGame({ problem: currentProblem, grade: g }, ac.signal);
      setExplainResult(res);
      setExplainStatus("done");
    } catch (e) {
      if (e instanceof ExplainAbortError) return;
      setExplainStatus("error");
    }
  }

  function dismissExplain() {
    abortRef.current?.abort();
    setExplainOpen(false);
    setExplainStatus("idle");
    setExplainResult(null);
  }

  if (!game) {
    return (
      <div className="p-4 text-center">
        <p className="mb-2 text-slate-600">Game not found.</p>
        <button className="text-sm text-blue-600 hover:underline" onClick={() => navigate(hashFor.games())}>
          &larr; All games
        </button>
      </div>
    );
  }

  const src = ctx ? gameUrl(game, ctx) : game.url;

  return (
    <div className="-mx-4 -mb-4">
      <div ref={headerRef}>
        <div className="flex items-center justify-between border-b border-slate-200 px-3 py-2">
          <button className="text-sm text-blue-600 hover:underline" onClick={() => navigate(hashFor.games())}>
            &larr; Games
          </button>
          <span className="text-sm font-semibold text-slate-700">{game.title}</span>
          {currentProblem ? (
            <button
              className={
                "rounded-md px-2.5 py-1 text-xs font-medium transition-colors " +
                (explainOpen
                  ? "bg-indigo-100 text-indigo-800"
                  : "bg-slate-100 text-slate-600 hover:bg-slate-200")
              }
              onClick={() => setExplainOpen((v) => !v)}
            >
              Explain
            </button>
          ) : (
            <span />
          )}
        </div>
        {subjects.length > 0 && (
          <div className="flex flex-wrap items-center gap-x-3 gap-y-1 border-b border-slate-100 bg-slate-50 px-3 py-1.5">
            <span className="text-xs font-medium text-slate-500">Related:</span>
            {subjects.map((s) => (
              <span key={s.subjectId} className="flex items-center gap-1.5 text-xs">
                <button
                  className="font-medium text-blue-600 hover:underline"
                  onClick={() => navigate(hashFor.module(s.subjectId))}
                  title="Browse problems"
                >
                  {s.title}
                </button>
                {s.overlap > 0 && (
                  <button
                    className="rounded bg-blue-50 px-1.5 py-0.5 font-medium text-blue-700 hover:bg-blue-100"
                    onClick={() => navigate(hashFor.testSkills(s.subjectId, game.skills))}
                    title="Practice test filtered to this game's skills"
                  >
                    Practice
                  </button>
                )}
              </span>
            ))}
          </div>
        )}
        {explainOpen && currentProblem && (
          <div className="border-b border-slate-200 bg-white px-3 py-3">
            <div className="mb-2 flex items-center justify-between">
              <p className="text-sm font-medium text-slate-700">Explain this problem</p>
              <button
                onClick={dismissExplain}
                className="rounded px-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
              >
                ✕
              </button>
            </div>
            <div className="mb-1 grid grid-cols-3 gap-2">
              {GRADES.map((g) => (
                <button
                  key={g}
                  type="button"
                  onClick={() => pickGrade(g)}
                  disabled={explainStatus === "loading"}
                  className={
                    "rounded-md border px-2 py-1.5 text-xs transition-colors disabled:cursor-not-allowed disabled:opacity-60 " +
                    (grade === g
                      ? "border-blue-500 bg-blue-50 font-medium text-blue-800"
                      : "border-slate-200 text-slate-700 hover:border-slate-300")
                  }
                >
                  {explainStatus === "loading" && grade === g ? <Spinner /> : gradeLabel(g)}
                </button>
              ))}
            </div>
            {explainStatus === "error" && (
              <p className="mt-2 text-sm text-red-600">Couldn't get an explanation. Try again.</p>
            )}
            {explainResult && (
              <div className="mt-2">
                {explainResult.placeholder && (
                  <p className="mb-1 text-xs font-medium text-amber-700">
                    Placeholder — the AI explanation service is unavailable right now
                  </p>
                )}
                <SanitizedHtml
                  html={explainResult.text}
                  className="learn-explanation rounded-md bg-slate-50 p-3 text-sm text-slate-800"
                />
              </div>
            )}
          </div>
        )}
      </div>
      {iframeHeight > 0 && (
        <iframe
          src={src}
          title={game.title}
          className="w-full border-0"
          style={{ height: iframeHeight }}
          allow="autoplay"
        />
      )}
    </div>
  );
}
