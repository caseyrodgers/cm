import { useEffect, useRef, useState, type CSSProperties } from "react";
import { cn } from "../../lib/utils";

/**
 * Big, bright "Correct!" burst — the game-y reward for a right answer.
 * A fixed full-viewport overlay that pops in, holds, fades out, then
 * calls onDone (~2.3s total), or dismisses early on click/tap.
 *
 * CSS-only particle burst (keyframes in styles.css), no animation
 * library — bundle discipline. Honors prefers-reduced-motion (no
 * particles, plain fade instead of the pop).
 */

const LIFETIME_MS = 2300;
const EXIT_MS = 400;

const PARTICLE_COLORS = ["#22c55e", "#1A99D6", "#F0B82E", "#ec4899", "#a855f7", "#f97316"];

interface Particle {
  dx: number;
  dy: number;
  rot: number;
  size: number;
  delay: number;
  color: string;
}

function makeParticles(n: number): Particle[] {
  return Array.from({ length: n }, (_, i) => {
    const angle = (i / n) * 2 * Math.PI + Math.random() * 0.5;
    const dist = 90 + Math.random() * 130;
    return {
      dx: Math.cos(angle) * dist,
      dy: Math.sin(angle) * dist,
      rot: Math.random() * 360,
      size: 8 + Math.random() * 10,
      delay: Math.random() * 90,
      color: PARTICLE_COLORS[i % PARTICLE_COLORS.length],
    };
  });
}

export default function CorrectCelebration({ onDone }: { onDone: () => void }) {
  const [leaving, setLeaving] = useState(false);
  const particles = useRef(makeParticles(16));
  const doneRef = useRef(onDone);
  doneRef.current = onDone;

  useEffect(() => {
    const toExit = setTimeout(() => setLeaving(true), LIFETIME_MS - EXIT_MS);
    const toDone = setTimeout(() => doneRef.current(), LIFETIME_MS);
    return () => {
      clearTimeout(toExit);
      clearTimeout(toDone);
    };
  }, []);

  return (
    <div
      role="status"
      aria-live="polite"
      aria-label="Correct!"
      onClick={() => doneRef.current()}
      className={cn(
        "cc-overlay fixed inset-0 z-[100] flex items-center justify-center overflow-hidden",
        leaving && "cc-overlay-leaving"
      )}
    >
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(34,197,94,0.35),rgba(34,197,94,0)_60%)]" />

      <div className="pointer-events-none absolute left-1/2 top-1/2">
        {particles.current.map((p, i) => (
          <span
            key={i}
            className="cc-particle"
            style={
              {
                "--cc-dx": `${p.dx}px`,
                "--cc-dy": `${p.dy}px`,
                "--cc-rot": `${p.rot}deg`,
                "--cc-delay": `${p.delay}ms`,
                width: `${p.size}px`,
                height: `${p.size}px`,
                background: p.color,
              } as CSSProperties
            }
          />
        ))}
      </div>

      <div className="cc-pop relative flex flex-col items-center gap-3 px-6 text-center">
        <span className="flex h-20 w-20 items-center justify-center rounded-full bg-green-500 text-5xl text-white shadow-xl sm:h-24 sm:w-24">
          &#10003;
        </span>
        <span className="bg-gradient-to-b from-green-400 to-emerald-600 bg-clip-text text-6xl font-extrabold tracking-tight text-transparent drop-shadow-sm sm:text-8xl">
          Correct!
        </span>
      </div>
    </div>
  );
}
