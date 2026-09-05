/**
 * Widget-slot contract (cm_re/TUTOR_WIDGET.org). A "widget" is plain
 * JS/DOM: it owns a rectangle of the page the host hands it, builds
 * its own UI into that element with ordinary DOM APIs, and talks back
 * to the host only through this contract — no React, no app-internal
 * imports required. That mirrors the legacy tutor's model (an
 * external, framework-agnostic JS blob mounted into a slot — see
 * CM3_MOBILE_REFERENCE.org) but with an explicit typed contract
 * instead of ambient `window` events.
 *
 * The React bridge is WidgetSlot.tsx; the registry is registry.ts.
 */

export interface WidgetContext<TValue = unknown> {
  /** Stable identity for this slot instance — typically the solution's pid. The widget doesn't have to use it; the host does, for persistence/remount keys. */
  instanceId: string;
  /** Widget-type-specific config. The host passes it through unparsed; only the widget interprets it. */
  config: Record<string, unknown>;
  /** Value to restore on mount, if the host has one saved. Shape is whatever this widget `type` defines. */
  initialValue?: TValue;
  /** Widget calls this whenever its value changes. The host decides whether/how/when to persist it — debouncing is the WIDGET's job, not the host's. */
  onChange?: (value: TValue) => void;
  /** Widget calls this to report a right/wrong result, for widget types where that applies (most won't). */
  onResult?: (result: { correct: boolean | null }) => void;
}

export interface WidgetInstance<TValue = unknown> {
  /**
   * Host is tearing the slot down (solution changed, panel closed,
   * component unmount). Must release every DOM listener/timer/etc it
   * added — the mounted element itself is discarded by the caller, but
   * anything registered on `window`/`document` or via `setTimeout`
   * needs explicit cleanup here.
   */
  destroy(): void;
  /** Optional: lets the host pull the current value directly instead of only listening for onChange (e.g. to flush on unmount). */
  getValue?(): TValue;
  /** Optional: lets the host push a value in after mount (e.g. an async load that resolves after the widget's already up). */
  setValue?(value: TValue): void;
}

export interface WidgetModule<TValue = unknown> {
  /** Registry key. Matches Solution.widgetSlot.type from the preprocessor, or whatever a caller asks WidgetSlot to mount directly. */
  type: string;
  /**
   * Mount into `el` — an empty element already attached to the
   * document, sized by the host. Must be synchronous: build the DOM,
   * wire listeners, return the instance. Anything async (loading
   * assets, etc.) is the widget's own concern after that.
   */
  mount(el: HTMLElement, ctx: WidgetContext<TValue>): WidgetInstance<TValue>;
}
