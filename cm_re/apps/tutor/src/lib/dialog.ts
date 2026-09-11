/**
 * App dialogs — an async, styled replacement for window.alert /
 * window.confirm. Call from anywhere:
 *
 *   import { confirm, alert } from "../lib/dialog";
 *   if (!(await confirm({ title: "Remove download", message: "…", danger: true }))) return;
 *   await alert("Nothing to show.");
 *
 * <DialogHost/> (mounted once in App) renders the actual modal and
 * fulfils the promise. If it isn't mounted (tests, SSR) this falls back
 * to the native dialogs so nothing hangs.
 */

export interface DialogOptions {
  title?: string;
  message: string;
  /** confirm() only — label for the affirmative button (default "OK"). */
  confirmLabel?: string;
  /** confirm() only — label for the dismissive button (default "Cancel"). */
  cancelLabel?: string;
  /** style the affirmative button as destructive. */
  danger?: boolean;
}

export interface DialogRequest extends DialogOptions {
  kind: "alert" | "confirm";
  resolve: (ok: boolean) => void;
}

let host: ((req: DialogRequest) => void) | null = null;

/** DialogHost calls this on mount/unmount. Internal. */
export function _setDialogHost(fn: ((req: DialogRequest) => void) | null): void {
  host = fn;
}

function open(kind: "alert" | "confirm", opts: DialogOptions): Promise<boolean> {
  return new Promise((resolve) => {
    if (!host) {
      // No host mounted — degrade to the platform dialogs.
      if (kind === "alert") {
        try {
          window.alert(opts.message);
        } catch {
          /* non-browser */
        }
        resolve(true);
      } else {
        let ok = false;
        try {
          ok = window.confirm(opts.message);
        } catch {
          /* non-browser */
        }
        resolve(ok);
      }
      return;
    }
    host({ kind, resolve, ...opts });
  });
}

export function confirm(opts: string | DialogOptions): Promise<boolean> {
  return open("confirm", typeof opts === "string" ? { message: opts } : opts);
}

export function alert(opts: string | DialogOptions): Promise<void> {
  return open("alert", typeof opts === "string" ? { message: opts } : opts).then(() => undefined);
}
