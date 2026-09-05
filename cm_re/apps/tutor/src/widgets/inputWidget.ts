import type { WidgetModule, WidgetContext, WidgetInstance } from "./types";

/**
 * First real widget under the widget-slot contract (TUTOR_WIDGET.org,
 * 2026-09-04). Captures a single value for the problem statement —
 * today that's a text field with a numeric filter, per Casey: "the
 * widget just needs to be able [to] enter a value for the problem
 * statement. It will probably only be a text input with a number
 * filter. But, it is possible to extend it to external, prebuilt
 * components."
 *
 * That extensibility doesn't require anything special from THIS
 * widget — the contract (types.ts) only asks for `mount(el, ctx)`, so
 * a future "external prebuilt component" is just another
 * WidgetModule registered under its own `type` (e.g. an equation
 * editor, a graphing tool) that happens to mount a third-party
 * element instead of building plain DOM. This file is the plain-DOM
 * case, not a base class the others extend.
 *
 * Value shape: string — the raw entered text (numeric-filtered when
 * config.numeric !== false, but always carried as a string; the host
 * decides if/how to parse it).
 */

export interface InputWidgetConfig {
  /** Restrict keystrokes to a number shape (digits, one leading "-", one "."). Default true — this is the only mode requested so far. */
  numeric?: boolean;
  placeholder?: string;
}

const BASE_INPUT_CLASS =
  "w-full max-w-[16rem] rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 " +
  "focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500";

export const inputWidget: WidgetModule<string> = {
  type: "input",
  mount(root: HTMLElement, ctx: WidgetContext<string>): WidgetInstance<string> {
    const config = ctx.config as InputWidgetConfig;
    const numeric = config.numeric !== false;

    root.className = "flex flex-col gap-1";

    const label = document.createElement("label");
    label.className = "text-xs font-medium text-slate-500";
    label.textContent = "Your answer";
    const labelId = `widget-input-${ctx.instanceId}`;
    label.htmlFor = labelId;
    root.appendChild(label);

    const input = document.createElement("input");
    input.id = labelId;
    input.type = "text";
    input.autocomplete = "off";
    input.spellcheck = false;
    if (numeric) input.inputMode = "decimal";
    if (config.placeholder) input.placeholder = config.placeholder;
    input.className = BASE_INPUT_CLASS;
    input.value = ctx.initialValue ?? "";
    root.appendChild(input);

    function onInput() {
      if (numeric) {
        const sanitized = sanitizeNumeric(input.value);
        if (sanitized !== input.value) input.value = sanitized;
      }
      ctx.onChange?.(input.value);
    }

    input.addEventListener("input", onInput);

    return {
      destroy() {
        input.removeEventListener("input", onInput);
      },
      getValue() {
        return input.value;
      },
      setValue(value: string) {
        input.value = value ?? "";
      },
    };
  },
};

/** Keeps digits, a single leading "-", and a single "." — everything else in a pasted/typed string is dropped. */
function sanitizeNumeric(raw: string): string {
  let s = raw.replace(/[^0-9.\-]/g, "");
  const negative = s.startsWith("-");
  s = s.replace(/-/g, "");
  if (negative) s = "-" + s;
  const dot = s.indexOf(".");
  if (dot !== -1) {
    s = s.slice(0, dot + 1) + s.slice(dot + 1).replace(/\./g, "");
  }
  return s;
}
