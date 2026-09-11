import { useEffect, useRef, useState } from "react";
import { _setDialogHost, type DialogRequest } from "../../lib/dialog";
import { Button } from "../ui/button";

/**
 * Renders the app's modal dialog (see lib/dialog.ts). Mount once, near
 * the app root. One dialog at a time — a second request while one is
 * open replaces it (resolving the first as dismissed).
 */
export default function DialogHost() {
  const [req, setReq] = useState<DialogRequest | null>(null);
  const live = useRef<DialogRequest | null>(null);
  live.current = req;

  useEffect(() => {
    _setDialogHost((next) => {
      setReq((cur) => {
        cur?.resolve(false); // a queued dialog supersedes the open one
        return next;
      });
    });
    return () => _setDialogHost(null);
  }, []);

  if (!req) return null;

  const close = (ok: boolean) => {
    live.current?.resolve(ok);
    live.current = null;
    setReq(null);
  };

  const isConfirm = req.kind === "confirm";

  return (
    <div
      data-testid="app-dialog"
      role="alertdialog"
      aria-modal="true"
      aria-label={req.title ?? "Dialog"}
      className="fixed inset-0 z-[90] flex items-center justify-center bg-slate-900/40 p-4"
      onClick={() => close(false)}
      onKeyDown={(e) => {
        if (e.key === "Escape") close(false);
      }}
    >
      <div
        className="w-full max-w-sm rounded-lg border border-slate-200 bg-white p-4 shadow-xl"
        onClick={(e) => e.stopPropagation()}
      >
        {req.title && <h2 className="mb-1 text-base font-semibold text-slate-900">{req.title}</h2>}
        <p className="text-sm text-slate-600">{req.message}</p>
        <div className="mt-4 flex justify-end gap-2">
          {isConfirm && (
            <Button variant="outline" data-testid="app-dialog-cancel" onClick={() => close(false)}>
              {req.cancelLabel ?? "Cancel"}
            </Button>
          )}
          <Button
            autoFocus
            data-testid="app-dialog-confirm"
            className={req.danger ? "bg-red-600 text-white hover:bg-red-700" : undefined}
            onClick={() => close(true)}
          >
            {isConfirm ? (req.confirmLabel ?? "OK") : "OK"}
          </Button>
        </div>
      </div>
    </div>
  );
}
