/**
 * Which app shell (header/footer/nav chrome) is active. The content is
 * one thing; the shell is where we try out different overall looks.
 *
 * Resolution order:
 *   1. `?shell=<id>` in the URL query (before the hash) — if present,
 *      it's also remembered so a plain reload keeps it.
 *   2. localStorage `cm_re.shell`.
 *   3. "default".
 *
 * `?shell=default` (or `?shell=`) clears the stored override.
 */

const KEY = "cm_re.shell";
export const DEFAULT_SHELL = "default";

export function activeShellId(): string {
  let fromQuery: string | null = null;
  try {
    fromQuery = new URLSearchParams(window.location.search).get("shell");
  } catch {
    /* no window / bad URL */
  }
  if (fromQuery !== null) {
    const id = fromQuery.trim() || DEFAULT_SHELL;
    try {
      if (id === DEFAULT_SHELL) localStorage.removeItem(KEY);
      else localStorage.setItem(KEY, id);
    } catch {
      /* private mode */
    }
    return id;
  }
  try {
    return localStorage.getItem(KEY) || DEFAULT_SHELL;
  } catch {
    return DEFAULT_SHELL;
  }
}

/** Persist a shell choice and reload so it takes effect from the top. */
export function setShell(id: string): void {
  try {
    if (id === DEFAULT_SHELL) localStorage.removeItem(KEY);
    else localStorage.setItem(KEY, id);
  } catch {
    /* ignore */
  }
  // Drop any ?shell= from the URL so it doesn't fight the stored value.
  try {
    const url = new URL(window.location.href);
    url.searchParams.delete("shell");
    window.location.replace(url.toString());
  } catch {
    window.location.reload();
  }
}
