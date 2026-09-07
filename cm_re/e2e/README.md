# cm_re end-to-end tests (Playwright)

Reusable browser tests for the tutor. They drive a real headless Chromium
against a running dev server and assert on real behaviour (module install,
MathML rendering, the MC question flow, the "Correct!" celebration, step
navigation, practice tests, the score-screen review).

These complement the fast in-process `*.smoketest.ts` / `*.check.ts` files
under `apps/tutor/src/` — those test logic with `fake-indexeddb`; these
test the assembled app in a browser.

## One-time setup

```sh
cd cm_re/e2e
npm install
npx playwright install chromium      # reuses the machine's browser cache if present
```

## Running

```sh
cd cm_re && make e2e
```

That's self-contained — it builds the tutor, starts the Java server,
runs the suite, and shuts the server down. If you already have `make
run` going in another terminal it reuses that one (faster — no rebuild).

Running from `cm_re/e2e` directly, `npm test` expects a server already
on `http://localhost:5173` (start one with `make run`). Set
`CM_E2E_START_SERVER=1` to have it boot one itself — this is what `make
e2e` does.

Other commands (run from `cm_re/e2e`):

| command | what |
|---|---|
| `npm test` | all specs except `@slow` and the live-AI ones |
| `npm run test:headed` | same, with a visible browser |
| `npm run test:ui` | Playwright's interactive UI mode |
| `npm run test:slow` | also run `@slow` (installs the real 846-solution module) |
| `npm run test:ai` | run the live Claude "Learn" test (costs money — see below) |
| `npm run report` | open the HTML report from the last run |
| `npm run codegen` | record a new test by clicking through the app |

### Point at a different origin

`CM_E2E_BASE_URL=http://localhost:8080 npm test` (e.g. against `make serve`).

### Live AI test

`learn-ai.spec.ts` calls the real Claude API through the server and is
skipped unless `RUN_AI_TESTS=1`. The server must have been started with
`ANTHROPIC_API_KEY` set.

```sh
RUN_AI_TESTS=1 npx playwright test learn-ai
```

## Notes

- **Service workers are blocked** (`playwright.config.ts`). The tutor's
  Workbox SW otherwise serves a stale app shell after a rebuild.
- Each test gets a fresh browser context, so IndexedDB starts empty —
  tests that need content call `installModule()` from `helpers.ts`.
- Most tests use the small `algebra1` demo bundle (3 solutions, 1 with a
  scorable MC question) so they install fast. `alg1ptests` (the real 846)
  is only used by `@slow` tests.
- Stable hooks in the app: `data-testid="mc-question" | "mc-choice" |
  "mc-submit" | "step-counter" | "step-next" | "step-prev"`, and the
  celebration is `role="status"` / `aria-label="Correct!"`.
