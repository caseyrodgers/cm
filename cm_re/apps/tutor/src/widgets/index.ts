import { registerWidget } from "./registry";
import { inputWidget } from "./inputWidget";

/**
 * Side-effect import: `import "./widgets"` once (main.tsx) registers
 * every widget module. Components mount widgets by `type` string via
 * <WidgetSlot>, never by importing a concrete module — add new
 * widgets here as they're built (see inputWidget.ts's header comment
 * on the "external prebuilt component" extension point).
 */
registerWidget(inputWidget);

export { default as WidgetSlot } from "./WidgetSlot";
export * from "./types";
export { getWidget, registerWidget, widgetTypes } from "./registry";
