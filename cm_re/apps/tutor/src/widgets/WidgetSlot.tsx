import { useEffect, useRef } from "react";
import { getWidget } from "./registry";
import type { WidgetInstance } from "./types";

/**
 * React bridge for the widget contract (types.ts). Mounts the named
 * widget's vanilla-DOM `mount()` into a plain div and tears it down on
 * unmount or on identity change (type/instanceId) — that pairing is
 * the remount key, mirroring the `key={pid}` pattern used elsewhere
 * for per-solution resets. Config/callback changes alone do NOT force
 * a remount; the widget always sees the latest callbacks via refs.
 */

export interface WidgetSlotProps<TValue = unknown> {
  type: string;
  instanceId: string;
  config?: Record<string, unknown>;
  initialValue?: TValue;
  onChange?: (value: TValue) => void;
  onResult?: (result: { correct: boolean | null }) => void;
  className?: string;
}

export default function WidgetSlot<TValue = unknown>({
  type,
  instanceId,
  config,
  initialValue,
  onChange,
  onResult,
  className,
}: WidgetSlotProps<TValue>) {
  const hostRef = useRef<HTMLDivElement | null>(null);
  const instanceRef = useRef<WidgetInstance<TValue> | null>(null);
  const onChangeRef = useRef(onChange);
  const onResultRef = useRef(onResult);
  onChangeRef.current = onChange;
  onResultRef.current = onResult;

  useEffect(() => {
    const host = hostRef.current;
    if (!host) return;

    const widget = getWidget(type);
    if (!widget) {
      console.error(`[widgets] no widget registered for type "${type}"`);
      return;
    }

    host.innerHTML = "";
    const instance = widget.mount(host, {
      instanceId,
      config: config ?? {},
      initialValue,
      onChange: (value) => onChangeRef.current?.(value as TValue),
      onResult: (result) => onResultRef.current?.(result),
    }) as WidgetInstance<TValue>;
    instanceRef.current = instance;

    return () => {
      instanceRef.current = null;
      instance.destroy();
    };
    // Only type+instanceId identity should remount — config/initialValue
    // changes on the same instance are the widget's own concern via
    // setValue(), not a reason to tear down and rebuild.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [type, instanceId]);

  return <div ref={hostRef} className={className} />;
}
