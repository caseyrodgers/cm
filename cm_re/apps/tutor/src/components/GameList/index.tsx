import { useEffect, useState } from "react";
import { GAMES, type GameEntry } from "../../games/registry";
import { navigate, hashFor } from "../../routing";
import { getInstalledModules } from "../../offline/moduleManager";
import type { InstalledModule } from "../../offline/db";
import { Card, CardContent } from "../ui/card";

export default function GameList() {
  const [modules, setModules] = useState<InstalledModule[]>([]);
  useEffect(() => {
    getInstalledModules().then(setModules);
  }, []);

  return (
    <div className="space-y-2">
      <h2 className="mb-2 text-base font-medium text-slate-700">Games</h2>
      {GAMES.map((g) => (
        <Card key={g.slug}>
          <CardContent>
            <button className="w-full text-left" onClick={() => navigate(hashFor.game(g.slug))}>
              <span className="flex items-center justify-between">
                <span className="text-base font-semibold text-slate-900">{g.title}</span>
                <span aria-hidden className="text-slate-400">&rsaquo;</span>
              </span>
              <span className="mt-1 block text-sm text-slate-500">{g.description}</span>
              {g.skills.length > 0 && (
                <span className="mt-1.5 flex flex-wrap gap-1">
                  {g.skills.map((s) => (
                    <span key={s} className="rounded-full bg-blue-50 px-2 py-0.5 text-[10px] font-medium text-blue-700">
                      {s}
                    </span>
                  ))}
                </span>
              )}
            </button>
            <PracticeButton game={g} modules={modules} />
          </CardContent>
        </Card>
      ))}
      {GAMES.length === 0 && (
        <p className="py-8 text-center text-sm text-slate-400">No games yet.</p>
      )}
    </div>
  );
}

function PracticeButton({ game, modules }: { game: GameEntry; modules: InstalledModule[] }) {
  const match = bestSubjectForGame(game, modules);
  if (!match) return null;

  return (
    <button
      className="mt-2 w-full rounded-md border border-blue-200 bg-blue-50 px-3 py-1.5 text-sm font-medium text-blue-700 hover:bg-blue-100"
      onClick={(e) => {
        e.stopPropagation();
        navigate(hashFor.testSkills(match, game.skills));
      }}
    >
      Practice these skills
    </button>
  );
}

function bestSubjectForGame(game: GameEntry, modules: InstalledModule[]): string | null {
  const skillSet = new Set<string>(game.skills);
  let best: string | null = null;
  let bestCount = 0;
  for (const mod of modules) {
    let count = 0;
    for (const ch of mod.chapters ?? []) {
      for (const s of ch.skills ?? []) {
        if (skillSet.has(s)) count++;
      }
    }
    if (count > bestCount) {
      bestCount = count;
      best = mod.subjectId;
    }
  }
  return best;
}
