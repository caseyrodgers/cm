import { expect, type Locator, type Page } from "@playwright/test";

/**
 * Shared helpers for the cm_re tutor e2e tests.
 *
 * The tutor is offline-first: content lives in IndexedDB, keyed per
 * browser context. Playwright gives each test a fresh context, so every
 * test that needs content must install a module first (`installModule`).
 * Service workers are blocked in playwright.config.ts, so a rebuilt
 * bundle is always picked up.
 */

export const SUBJECT = {
  /** 3 solutions, one with a scorable MC question. Installs in well under a second. */
  demo: "algebra1",
  /** 846 real solutions. Only for @slow "at scale" checks. */
  real: "alg1ptests",
} as const;

/**
 * A solution in the `algebra1` demo bundle that has a scorable MC
 * question (correctIndex 0, four choices rendered as MathML fractions)
 * and two worked steps. The workhorse fixture for solution-view tests.
 */
export const MC_PID = "alg1ptests_1_1_chapter1practicetest_10_1";

/** Download + install a subject module into this context's IndexedDB. */
export async function installModule(page: Page, subjectId: string = SUBJECT.demo): Promise<void> {
  await page.goto(`/#/m/${subjectId}`);
  const download = page.getByRole("button", { name: /Download for offline/i });
  const installed = page.getByText(/Installed for offline use/i);
  await expect(download.or(installed).first()).toBeVisible();
  if (await download.isVisible()) await download.click();
  await expect(installed).toBeVisible({ timeout: 60_000 });
}

/** Open one solution by pid (module must already be installed). */
export async function openSolution(page: Page, pid: string): Promise<void> {
  await page.goto(`/#/s/${pid}`);
  await expect(page.getByRole("button", { name: /Back to solutions/i })).toBeVisible();
}

/**
 * pid -> correctIndex for every scorable solution in a served module
 * bundle. Lets a test answer questions correctly without hard-coding keys.
 */
export async function answerKey(page: Page, subjectId: string = SUBJECT.demo): Promise<Record<string, number>> {
  return page.evaluate(async (s) => {
    const b = await (await fetch(`/modules/${s}/bundle.json`)).json();
    const m: Record<string, number> = {};
    for (const sol of b.solutions) {
      if (sol.question && typeof sol.question.correctIndex === "number") m[sol.pid] = sol.question.correctIndex;
    }
    return m;
  }, subjectId);
}

/** The MC choice buttons of the currently-visible QuestionView, in A..D order. */
export function choices(page: Page): Locator {
  return page.getByTestId("mc-question").getByTestId("mc-choice");
}

/** The celebration overlay (role=status, aria-label "Correct!"). */
export function celebration(page: Page): Locator {
  return page.getByRole("status", { name: "Correct!" });
}

/**
 * Fire N synchronous DOM clicks on a control in a single task so React
 * batches them — the way a real fast double-tap does. Used to prove the
 * SolutionNav step-index clamp holds. `sel` is a CSS selector.
 */
export async function rapidClick(page: Page, sel: string, times: number): Promise<void> {
  await page.evaluate(
    ({ sel, times }) => {
      for (let i = 0; i < times; i++) {
        const el = document.querySelector<HTMLElement>(sel);
        if (el && !(el as HTMLButtonElement).disabled) el.click();
      }
    },
    { sel, times }
  );
}
