import { GAMES } from "../../games/registry";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";

export default function GameList() {
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
          </CardContent>
        </Card>
      ))}
      {GAMES.length === 0 && (
        <p className="py-8 text-center text-sm text-slate-400">No games yet.</p>
      )}
    </div>
  );
}
