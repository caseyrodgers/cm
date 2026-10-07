import { useEffect, useRef, useState } from "react";
import { GAMES } from "../../games/registry";
import { gameUrl, type GameContext } from "../../games/matching";
import { navigate, hashFor } from "../../routing";

export default function GameView({ slug, ctx }: { slug: string; ctx?: GameContext }) {
  const game = GAMES.find((g) => g.slug === slug);
  const headerRef = useRef<HTMLDivElement>(null);
  const [iframeHeight, setIframeHeight] = useState<number>(0);

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
      <div ref={headerRef} className="flex items-center justify-between border-b border-slate-200 px-3 py-2">
        <button className="text-sm text-blue-600 hover:underline" onClick={() => navigate(hashFor.games())}>
          &larr; Games
        </button>
        <span className="text-sm font-semibold text-slate-700">{game.title}</span>
        <span />
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
