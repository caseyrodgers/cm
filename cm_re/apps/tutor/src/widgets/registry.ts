import type { WidgetModule } from "./types";

/**
 * Widget-type registry. Modules register themselves as a side effect
 * of being imported (see widgets/index.ts) — components never import
 * a concrete widget module directly, only `type: string` names, so a
 * solution's `widgetSlot.type` (set by the preprocessor) is the only
 * thing that needs to line up with a registration.
 */

const widgets = new Map<string, WidgetModule<any>>();

export function registerWidget(module: WidgetModule<any>): void {
  if (widgets.has(module.type)) {
    console.warn(`[widgets] "${module.type}" is already registered — overwriting`);
  }
  widgets.set(module.type, module);
}

export function getWidget(type: string): WidgetModule<any> | undefined {
  return widgets.get(type);
}

export function widgetTypes(): string[] {
  return [...widgets.keys()];
}
