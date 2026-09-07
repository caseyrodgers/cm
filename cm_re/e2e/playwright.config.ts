import { defineConfig, devices } from "@playwright/test";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const cmRe = resolve(here, "..");

// Point tests at an already-running server (default) or an alternate origin.
const BASE_URL = process.env.CM_E2E_BASE_URL ?? "http://localhost:5173";

// Whether Playwright is allowed to start the server itself. Off by default:
// `make run` needs Maven + JDK 17 + a tutor build and takes 1-2 min, so the
// normal workflow is to have `make run` going in another terminal. Set
// CM_E2E_START_SERVER=1 to let Playwright boot it.
const START_SERVER = process.env.CM_E2E_START_SERVER === "1";

export default defineConfig({
  testDir: "./tests",
  // One dev server, IndexedDB is per browser-context; keep runs calm and ordered.
  fullyParallel: false,
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  timeout: 60_000,
  expect: { timeout: 10_000 },
  reporter: [["list"], ["html", { open: "never" }]],

  use: {
    baseURL: BASE_URL,
    // The tutor ships a Workbox service worker. Left alone it caches the app
    // shell and will serve a STALE bundle after a rebuild (this bit us by hand
    // repeatedly). Blocking it makes every navigation hit the server fresh.
    serviceWorkers: "block",
    trace: "on-first-retry",
    screenshot: "only-on-failure",
    video: "off",
  },

  projects: [{ name: "chromium", use: { ...devices["Desktop Chrome"] } }],

  ...(START_SERVER
    ? {
        webServer: {
          command: "make run",
          cwd: cmRe,
          url: BASE_URL,
          reuseExistingServer: true,
          timeout: 180_000,
          stdout: "pipe",
          stderr: "pipe",
        },
      }
    : {}),
});
