import { useEffect, useRef, useState } from "react";
import { GAMES } from "../../games/registry";
import { gameUrl, type GameContext } from "../../games/matching";
import { navigate, hashFor } from "../../routing";
import { getInstalledModules } from "../../offline/moduleManager";
import { listSubjects } from "../../api/client";
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

export default function GameView({ slug, ctx }: { slug: string; ctx?: GameContext }) {
  const game = GAMES.find((g) => g.slug === slug);
  const headerRef = useRef<HTMLDivElement>(null);
  const [iframeHeight, setIframeHeight] = useState<number>(0);
  const [subjects, setSubjects] = useState<SubjectMatch[]>([]);

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
  }, []);

  useEffect(() => {
    function onMessage(e: MessageEvent) {
      if (e.data?.type === "cm_game_score") {
        // future: record score, tie into streak/stats
      }
    }
    window.addEventListener("message", onMessage);
    return () => window.removeEventListener("message", onMessage);
  }, []);

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
          <span />
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
