import { useCallback, useEffect, useRef, useState } from "react";
import type { Stroke } from "../../offline/db";
import { getWhiteboard, saveWhiteboard, clearWhiteboard } from "../../offline/whiteboardStore";
import { checkWork, ExplainAbortError } from "../../api/aiClient";
import { SanitizedHtml } from "../StepViewer";
import { Button } from "../ui/button";
import { Spinner } from "../ui/spinner";
import { cn } from "../../lib/utils";
import { confirm } from "../../lib/dialog";

/**
 * Per-solution scratch whiteboard. One continuous board per solution —
 * stepping through the solution doesn't partition it, every step's
 * work lands on the same surface.
 *
 * When open it's a semi-transparent overlay covering the problem/step
 * card (`absolute inset-0` inside the `relative` Card), so you draw on
 * top of the problem — which stays visible through the board at a
 * student-adjustable opacity (the slider in the toolbar, ranged
 * MIN_OPACITY-MAX_OPACITY; defaults to DEFAULT_OPACITY). Sticky in
 * localStorage across solutions/sessions, not tied to any one pid.
 * Strokes are vector, stored in a fixed logical coordinate space
 * (LOGICAL_W x LOGICAL_H, portrait) so the drawing is resolution- and
 * resize-independent regardless of the panel's actual pixel size; the
 * canvas backing store is scaled by devicePixelRatio.
 *
 * Persistence is IndexedDB-only via whiteboardStore — never the
 * server. Keyed by pid; render with `key={pid}` so switching solutions
 * gets a fresh mount (which flushes the previous board's save on
 * unmount).
 *
 * "Ask AI about my work" is the one server round-trip this component
 * makes: flattens the canvas onto a white background (the on-screen
 * canvas is transparent, drawn over a translucent CSS backdrop — a
 * transparent PNG would leave the model guessing at contrast), base64s
 * it, and POSTs it alongside the pid so the server can pair it with the
 * problem's own text/images. Response is qualitative feedback on
 * whether the work shows understanding — not a correct/incorrect
 * verdict (see aiClient.checkWork).
 */

const LOGICAL_W = 480;
const LOGICAL_H = 1200;
const SAVE_DEBOUNCE_MS = 400;
const PEN_COLORS = ["#1f2937", "#1A99D6", "#C14444"] as const;
const PEN_WIDTH = 2.5;

const OPACITY_KEY = "cm_re.whiteboard.opacity";
// Capped at 80% — even at max the problem should stay at least a little
// visible through the board, never a fully opaque surface.
const MIN_OPACITY = 0.0;
const MAX_OPACITY = 0.8;
const DEFAULT_OPACITY = 0.4;

function loadOpacity(): number {
  try {
    const v = typeof localStorage !== "undefined" ? localStorage.getItem(OPACITY_KEY) : null;
    const n = v === null ? NaN : parseFloat(v);
    return Number.isFinite(n) ? clamp(n, MIN_OPACITY, MAX_OPACITY) : DEFAULT_OPACITY;
  } catch {
    return DEFAULT_OPACITY;
  }
}

type AiStatus = "idle" | "loading" | "done" | "error";

export default function WhiteboardPanel({ pid }: { pid: string }) {
  const [open, setOpen] = useState(false);
  const [color, setColor] = useState<string>(PEN_COLORS[0]);
  const [strokeCount, setStrokeCount] = useState(0);
  const [opacity, setOpacityState] = useState<number>(loadOpacity);
  const [aiStatus, setAiStatus] = useState<AiStatus>("idle");
  const [aiFeedback, setAiFeedback] = useState<string | null>(null);
  const [aiPlaceholder, setAiPlaceholder] = useState(false);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const strokesRef = useRef<Stroke[]>([]);
  const drawingRef = useRef<Stroke | null>(null);
  const dirtyRef = useRef(false);
  const saveTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const aiAbortRef = useRef<AbortController | null>(null);

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
  useEffect(() => () => aiAbortRef.current?.abort(), []);

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

  function setOpacity(n: number) {
    const clamped = clamp(n, MIN_OPACITY, MAX_OPACITY);
    setOpacityState(clamped);
    try {
      localStorage.setItem(OPACITY_KEY, String(clamped));
    } catch {
      /* private mode / storage disabled — fine, just not sticky */
    }
  }

  function undo() {
    if (strokesRef.current.length === 0) return;
    strokesRef.current = strokesRef.current.slice(0, -1);
    setStrokeCount(strokesRef.current.length);
    redraw();
    scheduleSave();
  }

  async function clearAll() {
    if (strokesRef.current.length === 0) return;
    if (!(await confirm({ title: "Clear whiteboard", message: "Clear the whiteboard for this problem?", confirmLabel: "Clear", danger: true }))) {
      return;
    }
    strokesRef.current = [];
    setStrokeCount(0);
    redraw();
    if (saveTimerRef.current) clearTimeout(saveTimerRef.current);
    dirtyRef.current = false;
    void clearWhiteboard(pid);
  }

  /** Flattens the drawn strokes onto a white background PNG (the live canvas has a transparent 2D-context backing store — its CSS translucency is a display effect, not real pixel data) and returns bare base64 (no data: prefix). */
  function captureFlattenedPng(): string | null {
    const canvas = canvasRef.current;
    if (!canvas) return null;
    const flat = document.createElement("canvas");
    flat.width = canvas.width;
    flat.height = canvas.height;
    const fctx = flat.getContext("2d");
    if (!fctx) return null;
    fctx.fillStyle = "#ffffff";
    fctx.fillRect(0, 0, flat.width, flat.height);
    fctx.drawImage(canvas, 0, 0);
    const dataUrl = flat.toDataURL("image/png");
    const comma = dataUrl.indexOf(",");
    return comma >= 0 ? dataUrl.slice(comma + 1) : null;
  }

  async function askAI() {
    if (strokeCount === 0) return;
    const image = captureFlattenedPng();
    if (!image) return;
    aiAbortRef.current?.abort();
    const ac = new AbortController();
    aiAbortRef.current = ac;
    setAiStatus("loading");
    setAiFeedback(null);
    try {
      const res = await checkWork(pid, image, ac.signal);
      setAiFeedback(res.feedback);
      setAiPlaceholder(res.placeholder);
      setAiStatus("done");
    } catch (e) {
      if (e instanceof ExplainAbortError) return;
      setAiStatus("error");
    }
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
        <aside className="absolute inset-0 z-30 flex flex-col overflow-hidden rounded-lg border border-slate-300 bg-white/80 shadow-2xl">
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

          <div className="flex items-center gap-2 border-b border-slate-200 bg-white/85 px-3 py-1.5">
            <label htmlFor="wb-opacity" className="text-xs text-slate-500">
              Opacity
            </label>
            <input
              id="wb-opacity"
              type="range"
              min={MIN_OPACITY}
              max={MAX_OPACITY}
              step={0.05}
              value={opacity}
              onChange={(e) => setOpacity(e.target.valueAsNumber)}
              className="flex-1 accent-blue-600"
              aria-label="whiteboard opacity"
            />
            <span className="w-9 text-right text-xs tabular-nums text-slate-500">{Math.round(opacity * 100)}%</span>
          </div>

          <div className="flex-1 overflow-y-auto p-2">
            <canvas
              ref={canvasRef}
              width={LOGICAL_W * dpr}
              height={LOGICAL_H * dpr}
              className="block w-full touch-none rounded-md border border-slate-300"
              style={{ aspectRatio: `${LOGICAL_W} / ${LOGICAL_H}`, backgroundColor: `rgba(255,255,255,${opacity})` }}
              onPointerDown={onPointerDown}
              onPointerMove={onPointerMove}
              onPointerUp={endStroke}
              onPointerLeave={endStroke}
              onPointerCancel={endStroke}
            />
          </div>

          <div className="border-t border-slate-200 bg-white/85 px-3 py-2">
            <Button className="w-full" onClick={askAI} disabled={strokeCount === 0 || aiStatus === "loading"}>
              {aiStatus === "loading" ? <Spinner /> : "Ask AI about my work"}
            </Button>

            {aiStatus === "error" && (
              <p className="mt-2 text-sm text-red-600">Couldn't get feedback. Try again.</p>
            )}

            {aiFeedback && (
              <div className="mt-2">
                {aiPlaceholder && (
                  <p className="mb-1 text-xs font-medium text-amber-700">
                    Placeholder — the AI feedback service is unavailable right now
                  </p>
                )}
                <SanitizedHtml
                  html={aiFeedback}
                  className="learn-explanation rounded-md bg-slate-50 p-3 text-sm text-slate-800"
                />
              </div>
            )}
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
