import { test, expect } from "@playwright/test";
import { SUBJECT, installModule, choices } from "../helpers";

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
    await expect(page.getByTestId("mc-question")).toBeVisible();

    // Next problem -> URL changes to a different pid
    await page.getByRole("button", { name: /Next problem/i }).click();
    await expect(page).toHaveURL(/#\/t\/alg1ptests\/[^/]+$/);
    const second = page.url();
    expect(second).not.toBe(first);

    // deep link survives a reload
    await page.reload();
    await expect(page).toHaveURL(second);
    await expect(page.getByTestId("mc-question")).toBeVisible();

    // Previous problem -> back to the first
    await page.getByRole("button", { name: /Previous problem/i }).click();
    await expect(page).toHaveURL(first);
  });

  test("Learn panel sits below the problem, not above it", async ({ page }) => {
    await intoLesson(page);
    await expect(page.getByTestId("mc-question")).toBeVisible();

    const q = await page.getByTestId("mc-question").boundingBox();
    const learn = await page.getByRole("button", { name: /Learn .* explain this problem/i }).boundingBox();
    expect(q && learn).toBeTruthy();
    expect(learn!.y).toBeGreaterThan(q!.y);
  });
});
