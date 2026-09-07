# cm_re end-to-end tests (Playwright)

Reusable browser tests for the tutor. They drive a real headless Chromium
against a running dev server and assert on real behaviour (module install,
MathML rendering, the MC question flow, the "Correct!" celebration, step
navigation, practice tests, the score-screen review).

These complement the fast in-process `*.smoketest.ts` / `*.check.ts` files
under `apps/tutor/src/` — those test logic with `fake-indexeddb`; these
test the assembled app in a browser.

## Running

```sh
cd cm_re && make e2e
```

Fully self-contained — `make e2e` installs deps + the browser on first
run (`e2e-setup`, no-ops fast after), builds the tutor, starts the Java
server, runs the suite, and shuts the server down. If you already have
`make run` going in another terminal it reuses that one (no rebuild).

Running from `cm_re/e2e` directly with `npm test` expects a server
already on `http://localhost:5173` (start one with `make run`), and deps
already installed (`npm install && npx playwright install chromium`).
Set `CM_E2E_START_SERVER=1` to have it boot the server itself.

`make e2e` (and a bare `npm test`) runs **every spec in `tests/`** —
14 tests. The only one that doesn't execute is `learn-ai`, which
skips itself unless `RUN_AI_TESTS=1` (it calls the real Claude API).
So a normal run is 13 passed + 1 skipped. The `@slow` at-scale spec
*does* run by default (it's tagged only because it installs the full
846-solution module; in practice it's still sub-second).

Other commands (run from `cm_re/e2e`):

| command | what |
|---|---|
| `npm test` | every spec (`learn-ai` self-skips without `RUN_AI_TESTS=1`) |
| `npm run test:headed` | same, with a visible browser |
| `npm run test:ui` | Playwright's interactive UI mode |
| `npm run test:slow` | run **only** the `@slow` spec |
| `npm run test:ai` | run **only** the live Claude "Learn" test, with the key set (costs money) |
| `npm run report` | open the HTML report from the last run |
| `npm run codegen` | record a new test by clicking through the app |

### Point at a different origin

`make run` and `make serve` both listen on `:5173` (the default the
tests use). To run against a server on a different port or a remote
host:

```sh
make e2e E2E_BASE_URL=http://localhost:9000     # from cm_re/
# or, from cm_re/e2e:
CM_E2E_BASE_URL=http://localhost:9000 npm test
```

With `E2E_BASE_URL` set, `make e2e` does **not** start its own server —
it uses yours as-is. Make sure it's serving current code (`git pull` +
restart if it's stale — the tests rely on `data-testid` hooks).

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
