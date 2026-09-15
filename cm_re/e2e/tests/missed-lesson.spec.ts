import { test, expect, type Locator } from "@playwright/test";
import { SUBJECT, installModule, choices } from "../helpers";

/**
 * locator.boundingBox() doesn't auto-wait/retry the way expect(...) does
 * — it takes one snapshot of the current layout, which can race a
 * still-settling render (native MathML layout in particular can take an
 * extra frame or two for complex nested fractions) and come back null
 * even right after expect(...).toBeVisible() already passed. Poll it
 * briefly instead of trusting a single call.
 */
async function stableBoundingBox(locator: Locator) {
  for (let i = 0; i < 20; i++) {
    const box = await locator.boundingBox();
    if (box) return box;
    await locator.page().waitForTimeout(100);
  }
  return null;
}

/**
 * The "Missed Questions Lesson" walkthrough. Needs the real subject so
 * there are enough missed problems to build a multi-problem lesson —
 * tagged @slow (installs the 846-solution module).
 */
test.describe("@slow missed questions lesson", () => {
  test.slow();

  async function intoLesson(page: import("@playwright/test").Page) {
    await installModule(page, SUBJECT.real);
    await page.goto(`/#/t/${SUBJECT.real}`);
    await page.getByRole("button", { name: /Quick test/i }).click();
    await page.getByRole("button", { name: /^Start/ }).click();

    // Blindly pick the first choice each time — most will be wrong, so
    // ~all 10 land in "missed" and the lesson spans several chapters.
    // Test mode auto-advances after each submit; after the last one it
    // drops to the index, where "Finish" appears.
    const finish = page.getByRole("button", { name: /Finish/i });
    for (let i = 0; i < 12; i++) {
      if (await finish.isVisible().catch(() => false)) break;
      await choices(page).first().click();
      await page.getByTestId("mc-submit").click();
    }
    await finish.click();
    await page.getByRole("button", { name: /Lesson Based on Missed Questions/i }).click();
  }

  test("URL tracks the current problem and deep-links survive reload", async ({ page }) => {
    await intoLesson(page);

    // landed on a specific problem: #/t/<subject>/<pid>
    await expect(page).toHaveURL(/#\/t\/alg1ptests\/[^/]+$/);
    const first = page.url();
    // A handful of alg1ptests solutions are browse-only (no MC
    // question) and are now eligible lesson picks too (IDEAS.org
    // "problems that are browse only should be included in generated
    // Test Lessons"), so assert on the walkthrough's own always-present
    // chrome rather than mc-question, which not every lesson pid has.
    await expect(page.getByRole("button", { name: /Next problem/i })).toBeVisible();

    // Next problem -> URL changes to a different pid
    await page.getByRole("button", { name: /Next problem/i }).click();
    await expect(page).toHaveURL(/#\/t\/alg1ptests\/[^/]+$/);
    const second = page.url();
    expect(second).not.toBe(first);

    // deep link survives a reload
    await page.reload();
    await expect(page).toHaveURL(second);
    await expect(page.getByRole("button", { name: /Previous problem/i })).toBeVisible();

    // Previous problem -> back to the first
    await page.getByRole("button", { name: /Previous problem/i }).click();
    await expect(page).toHaveURL(first);
  });

  test("Learn panel sits below the problem, not above it", async ({ page }) => {
    await intoLesson(page);

    // Most lesson pids have an MC question, but a browse-only one is
    // now a valid pick too (see the test above) — walk forward to find
    // one that actually has a question, since that's what this
    // regression guard is about (Learn's position relative to it).
    let hasQuestion = await page.getByTestId("mc-question").isVisible().catch(() => false);
    const next = page.getByRole("button", { name: /Next problem/i });
    while (!hasQuestion && !(await next.isDisabled())) {
      await next.click();
      hasQuestion = await page.getByTestId("mc-question").isVisible().catch(() => false);
    }
    expect(hasQuestion).toBe(true); // only 3/846 alg1ptests solutions lack one — the lesson should contain at least one with

    await expect(page.getByTestId("mc-question")).toBeVisible();
    const q = await stableBoundingBox(page.getByTestId("mc-question"));
    const learn = await stableBoundingBox(page.getByRole("button", { name: /Learn .* explain this problem/i }));
    expect(q && learn).toBeTruthy();
    expect(learn!.y).toBeGreaterThan(q!.y);
  });
});
