import { useCallback, useEffect, useRef, useState } from "react";
import type { Stroke } from "../../offline/db";
import { getWhiteboard, saveWhiteboard, clearWhiteboard } from "../../offline/whiteboardStore";
import { Button } from "../ui/button";
import { cn } from "../../lib/utils";

/**
 * Per-solution scratch whiteboard. One continuous board per solution —
 * stepping through the solution doesn't partition it, every step's
 * work lands on the same surface.
 *
 * When open it's a vertical panel pinned to the right edge of the
 * viewport, so the problem stays visible on the left while you work.
 * Strokes are vector, stored in a fixed logical coordinate space
 * (LOGICAL_W x LOGICAL_H, portrait) so the drawing is resolution- and
 * resize-independent regardless of the panel's actual pixel size; the
 * canvas backing store is scaled by devicePixelRatio.
 *
 * Persistence is IndexedDB-only via whiteboardStore — never the
 * server. Keyed by pid; render with `key={pid}` so switching solutions
 * gets a fresh mount (which flushes the previous board's save on
 * unmount).
 */

const LOGICAL_W = 480;
const LOGICAL_H = 1200;
const SAVE_DEBOUNCE_MS = 400;
const PEN_COLORS = ["#1f2937", "#1A99D6", "#C14444"] as const;
const PEN_WIDTH = 2.5;

export default function WhiteboardPanel({ pid }: { pid: string }) {
  const [open, setOpen] = useState(false);
  const [color, setColor] = useState<string>(PEN_COLORS[0]);
  const [strokeCount, setStrokeCount] = useState(0);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const strokesRef = useRef<Stroke[]>([]);
  const drawingRef = useRef<Stroke | null>(null);
  const dirtyRef = useRef(false);
  const saveTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const dpr = typeof window !== "undefined" ? window.devicePixelRatio || 1 : 1;

  const redraw = useCallback(() => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext("2d");
    if (!canvas || !ctx) return;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, LOGICAL_W, LOGICAL_H);
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    for (const s of strokesRef.current) drawStroke(ctx, s);
  }, [dpr]);

  useEffect(() => {
    let cancelled = false;
    getWhiteboard(pid).then((wb) => {
      if (cancelled) return;
      strokesRef.current = wb?.strokes ?? [];
      setStrokeCount(strokesRef.current.length);
      redraw();
    });
    return () => {
      cancelled = true;
    };
  }, [pid, redraw]);

  // The canvas only exists in the DOM while the panel is open.
  useEffect(() => {
    if (open) {
      const id = requestAnimationFrame(redraw);
      return () => cancelAnimationFrame(id);
    }
  }, [open, redraw]);

  // Escape closes the panel.
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") setOpen(false);
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open]);

  const flushSave = useCallback(() => {
    if (saveTimerRef.current) {
      clearTimeout(saveTimerRef.current);
      saveTimerRef.current = null;
    }
    if (dirtyRef.current) {
      dirtyRef.current = false;
      void saveWhiteboard(pid, strokesRef.current);
    }
  }, [pid]);

  const scheduleSave = useCallback(() => {
    dirtyRef.current = true;
    if (saveTimerRef.current) clearTimeout(saveTimerRef.current);
    saveTimerRef.current = setTimeout(flushSave, SAVE_DEBOUNCE_MS);
  }, [flushSave]);

  useEffect(() => flushSave, [flushSave]);

  function toLogical(e: React.PointerEvent<HTMLCanvasElement>): [number, number] {
    const r = e.currentTarget.getBoundingClientRect();
    const x = ((e.clientX - r.left) / r.width) * LOGICAL_W;
    const y = ((e.clientY - r.top) / r.height) * LOGICAL_H;
    return [clamp(x, 0, LOGICAL_W), clamp(y, 0, LOGICAL_H)];
  }

  function onPointerDown(e: React.PointerEvent<HTMLCanvasElement>) {
    e.currentTarget.setPointerCapture(e.pointerId);
    const [x, y] = toLogical(e);
    drawingRef.current = { color, width: PEN_WIDTH, points: [x, y] };
  }

  function onPointerMove(e: React.PointerEvent<HTMLCanvasElement>) {
    const stroke = drawingRef.current;
    if (!stroke) return;
    const [x, y] = toLogical(e);
    const n = stroke.points.length;
    const px = stroke.points[n - 2];
    const py = stroke.points[n - 1];
    stroke.points.push(x, y);
    const ctx = canvasRef.current?.getContext("2d");
    if (ctx) {
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      ctx.lineCap = "round";
      ctx.lineJoin = "round";
      ctx.strokeStyle = stroke.color;
      ctx.lineWidth = stroke.width;
      ctx.beginPath();
      ctx.moveTo(px, py);
      ctx.lineTo(x, y);
      ctx.stroke();
    }
  }

  function endStroke() {
    const stroke = drawingRef.current;
    drawingRef.current = null;
    if (!stroke) return;
    if (stroke.points.length === 2) stroke.points.push(stroke.points[0] + 0.01, stroke.points[1] + 0.01);
    strokesRef.current.push(stroke);
    setStrokeCount(strokesRef.current.length);
    scheduleSave();
  }

  function undo() {
    if (strokesRef.current.length === 0) return;
    strokesRef.current = strokesRef.current.slice(0, -1);
    setStrokeCount(strokesRef.current.length);
    redraw();
    scheduleSave();
  }

  function clearAll() {
    if (strokesRef.current.length === 0) return;
    if (!confirm("Clear the whiteboard for this problem?")) return;
    strokesRef.current = [];
    setStrokeCount(0);
    redraw();
    if (saveTimerRef.current) clearTimeout(saveTimerRef.current);
    dirtyRef.current = false;
    void clearWhiteboard(pid);
  }

  return (
    <>
      <button
        type="button"
        className="mt-4 flex w-full items-center justify-between rounded-lg border border-slate-200 px-3 py-2 text-sm font-medium text-slate-700"
        onClick={() => setOpen((v) => !v)}
      >
        <span>Whiteboard{strokeCount > 0 ? ` (${strokeCount})` : ""}</span>
        <span aria-hidden className="text-slate-400">{open ? "close ›" : "open ‹"}</span>
      </button>

      {open && (
        <aside className="fixed inset-y-0 right-0 z-40 flex w-[min(92vw,26rem)] flex-col border-l border-slate-300 bg-white/70 shadow-2xl">
          <div className="flex items-center gap-2 border-b border-slate-200 bg-white/85 px-3 py-2">
            <span className="text-sm font-medium text-slate-700">Whiteboard</span>
            <div className="ml-auto flex items-center gap-1.5">
              {PEN_COLORS.map((c) => (
                <button
                  key={c}
                  type="button"
                  aria-label={`pen colour ${c}`}
                  onClick={() => setColor(c)}
                  className={cn("h-5 w-5 rounded-full border-2", color === c ? "border-slate-900" : "border-transparent")}
                  style={{ backgroundColor: c }}
                />
              ))}
              <Button variant="outline" onClick={undo} disabled={strokeCount === 0}>
                Undo
              </Button>
              <Button variant="outline" onClick={clearAll} disabled={strokeCount === 0}>
                Clear
              </Button>
              <button
                type="button"
                aria-label="close whiteboard"
                onClick={() => setOpen(false)}
                className="ml-1 rounded px-2 py-1 text-slate-500 hover:bg-slate-100"
              >
                ✕
              </button>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto p-2">
            <canvas
              ref={canvasRef}
              width={LOGICAL_W * dpr}
              height={LOGICAL_H * dpr}
              className="block w-full touch-none rounded-md border border-slate-300 bg-white/40"
              style={{ aspectRatio: `${LOGICAL_W} / ${LOGICAL_H}` }}
              onPointerDown={onPointerDown}
              onPointerMove={onPointerMove}
              onPointerUp={endStroke}
              onPointerLeave={endStroke}
              onPointerCancel={endStroke}
            />
          </div>

          <p className="border-t border-slate-200 bg-white/85 px-3 py-1.5 text-xs text-slate-400">
            One board for this problem · saved on this device only.
          </p>
        </aside>
      )}
    </>
  );
}

function drawStroke(ctx: CanvasRenderingContext2D, s: Stroke) {
  if (s.points.length < 2) return;
  ctx.strokeStyle = s.color;
  ctx.lineWidth = s.width;
  ctx.beginPath();
  ctx.moveTo(s.points[0], s.points[1]);
  for (let i = 2; i < s.points.length; i += 2) ctx.lineTo(s.points[i], s.points[i + 1]);
  ctx.stroke();
}

function clamp(v: number, lo: number, hi: number): number {
  return v < lo ? lo : v > hi ? hi : v;
}
