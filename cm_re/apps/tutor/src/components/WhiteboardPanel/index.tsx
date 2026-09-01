import { useCallback, useEffect, useRef, useState } from "react";
import type { Stroke } from "../../offline/db";
import { getWhiteboard, saveWhiteboard, clearWhiteboard } from "../../offline/whiteboardStore";
import { Button } from "../ui/button";
import { cn } from "../../lib/utils";

/**
 * Per-solution scratch whiteboard, tied to the solution's steps.
 *
 * The board is one continuous vertical surface divided into
 * fixed-height bands (SEGMENT_H logical units each), one per step.
 * `revealedSegments` is how many bands are unlocked; it grows as the
 * student advances through the steps and never shrinks, so earlier
 * scratch work stays put. Reaching a new step reveals its band and
 * scrolls it into view; you can still scroll back and edit earlier
 * bands.
 *
 * Vector strokes in a fixed logical coordinate space
 * (LOGICAL_W x revealedSegments*SEGMENT_H) so the drawing is
 * resolution- and resize-independent; the canvas backing store is
 * scaled by devicePixelRatio. Persistence is IndexedDB-only via
 * whiteboardStore — never the server. Render with `key={pid}` so
 * switching solutions gets a fresh mount (which flushes the previous
 * board's save on unmount).
 */

const LOGICAL_W = 600;
const SEGMENT_H = 300;
const SAVE_DEBOUNCE_MS = 400;
const PEN_COLORS = ["#1f2937", "#1A99D6", "#C14444"] as const;
const PEN_WIDTH = 2.5;

export default function WhiteboardPanel({
  pid,
  stepIndex,
  stepCount,
}: {
  pid: string;
  stepIndex: number;
  stepCount: number;
}) {
  const [open, setOpen] = useState(false);
  const [color, setColor] = useState<string>(PEN_COLORS[0]);
  const [strokeCount, setStrokeCount] = useState(0);
  const [revealedSegments, setRevealedSegments] = useState(1);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const wrapRef = useRef<HTMLDivElement | null>(null);
  const strokesRef = useRef<Stroke[]>([]);
  const drawingRef = useRef<Stroke | null>(null);
  const dirtyRef = useRef(false);
  const loadedRef = useRef(false);
  const revealedRef = useRef(1); // mirror of revealedSegments for closures that outlive a render (unmount flush)
  const saveTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const dpr = typeof window !== "undefined" ? window.devicePixelRatio || 1 : 1;
  const logicalH = revealedSegments * SEGMENT_H;
  const stepLabel = (i: number) => (stepCount > 0 ? `Step ${i + 1}` : "Scratch");

  const redraw = useCallback(() => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext("2d");
    if (!canvas || !ctx) return;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, LOGICAL_W, revealedRef.current * SEGMENT_H);

    // Current-step band tint + per-band dividers and labels.
    if (stepCount > 0 && stepIndex < revealedRef.current) {
      ctx.fillStyle = "rgba(26, 153, 214, 0.05)";
      ctx.fillRect(0, stepIndex * SEGMENT_H, LOGICAL_W, SEGMENT_H);
    }
    ctx.font = "13px system-ui, sans-serif";
    for (let k = 0; k < revealedRef.current; k++) {
      if (k > 0) {
        ctx.strokeStyle = "#e2e8f0";
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(0, k * SEGMENT_H);
        ctx.lineTo(LOGICAL_W, k * SEGMENT_H);
        ctx.stroke();
      }
      ctx.fillStyle = "#94a3b8";
      ctx.fillText(stepLabel(k), 8, k * SEGMENT_H + 18);
    }

    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    for (const s of strokesRef.current) drawStroke(ctx, s);
  }, [dpr, stepIndex, stepCount]);

  // Load this solution's saved board on mount / pid change.
  useEffect(() => {
    let cancelled = false;
    getWhiteboard(pid).then((wb) => {
      if (cancelled) return;
      strokesRef.current = wb?.strokes ?? [];
      const revealed = Math.max(wb?.revealedSegments ?? 1, stepIndex + 1, 1);
      revealedRef.current = revealed;
      setRevealedSegments(revealed);
      setStrokeCount(strokesRef.current.length);
      loadedRef.current = true;
      redraw();
    });
    return () => {
      cancelled = true;
    };
    // stepIndex intentionally omitted: initial reveal floor is fine, the
    // grow effect below handles later step changes.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pid, redraw]);

  // Advancing to a not-yet-seen step unlocks its band.
  useEffect(() => {
    if (!loadedRef.current) return;
    if (stepIndex + 1 > revealedRef.current) {
      const next = stepIndex + 1;
      revealedRef.current = next;
      setRevealedSegments(next);
      scheduleSave();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [stepIndex]);

  // Redraw whenever the surface height changes (canvas attrs change →
  // context is cleared by the browser) or the panel opens.
  useEffect(() => {
    if (open) redraw();
  }, [open, revealedSegments, redraw]);

  // Bring the current step's band into view when it changes.
  useEffect(() => {
    if (!open) return;
    const wrap = wrapRef.current;
    const canvas = canvasRef.current;
    if (!wrap || !canvas) return;
    const bandPx = canvas.clientHeight / revealedSegments;
    wrap.scrollTo({ top: Math.max(0, stepIndex * bandPx - 8), behavior: "smooth" });
  }, [stepIndex, revealedSegments, open]);

  const flushSave = useCallback(() => {
    if (saveTimerRef.current) {
      clearTimeout(saveTimerRef.current);
      saveTimerRef.current = null;
    }
    if (dirtyRef.current) {
      dirtyRef.current = false;
      void saveWhiteboard(pid, strokesRef.current, revealedRef.current);
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
    const y = ((e.clientY - r.top) / r.height) * logicalH;
    return [clamp(x, 0, LOGICAL_W), clamp(y, 0, logicalH)];
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
    if (strokesRef.current.length === 0 && revealedRef.current <= stepIndex + 1) return;
    if (!confirm("Clear the whiteboard for this problem?")) return;
    strokesRef.current = [];
    const floor = Math.max(stepIndex + 1, 1);
    revealedRef.current = floor;
    setRevealedSegments(floor);
    setStrokeCount(0);
    redraw();
    if (saveTimerRef.current) clearTimeout(saveTimerRef.current);
    dirtyRef.current = false;
    void clearWhiteboard(pid);
  }

  return (
    <div className="mt-4 rounded-lg border border-slate-200">
      <button
        type="button"
        className="flex w-full items-center justify-between px-3 py-2 text-sm font-medium text-slate-700"
        onClick={() => setOpen((v) => !v)}
      >
        <span>Whiteboard{strokeCount > 0 ? ` (${strokeCount})` : ""}</span>
        <span aria-hidden className="text-slate-400">{open ? "▾" : "▸"}</span>
      </button>

      {open && (
        <div className="border-t border-slate-200 p-3">
          <div className="mb-2 flex items-center gap-2">
            {PEN_COLORS.map((c) => (
              <button
                key={c}
                type="button"
                aria-label={`pen colour ${c}`}
                onClick={() => setColor(c)}
                className={cn("h-6 w-6 rounded-full border-2", color === c ? "border-slate-900" : "border-transparent")}
                style={{ backgroundColor: c }}
              />
            ))}
            <div className="ml-auto flex gap-2">
              <Button variant="outline" onClick={undo} disabled={strokeCount === 0}>
                Undo
              </Button>
              <Button variant="outline" onClick={clearAll} disabled={strokeCount === 0 && revealedSegments <= stepIndex + 1}>
                Clear
              </Button>
            </div>
          </div>

          <div ref={wrapRef} className="max-h-[65vh] overflow-y-auto rounded-md border border-slate-300 bg-white">
            <canvas
              ref={canvasRef}
              width={LOGICAL_W * dpr}
              height={logicalH * dpr}
              className="block w-full touch-none"
              style={{ aspectRatio: `${LOGICAL_W} / ${logicalH}` }}
              onPointerDown={onPointerDown}
              onPointerMove={onPointerMove}
              onPointerUp={endStroke}
              onPointerLeave={endStroke}
              onPointerCancel={endStroke}
            />
          </div>
          <p className="mt-1 text-xs text-slate-400">
            {stepCount > 0
              ? `${revealedSegments} of ${stepCount} step ${revealedSegments === 1 ? "band" : "bands"} unlocked · saved on this device only`
              : "Saved on this device only."}
          </p>
        </div>
      )}
    </div>
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
