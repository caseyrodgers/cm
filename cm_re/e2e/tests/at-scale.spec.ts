import { test, expect } from "@playwright/test";
import { SUBJECT, installModule, openSolution, choices } from "../helpers";

/**
 * The real 846-solution `alg1ptests` module. Tagged @slow — installing it
 * writes the whole bundle (~5 MB) to IndexedDB. Run with:
 *
 *   npx playwright test --grep @slow          (from cm_re/e2e)
 */
test.describe("@slow alg1ptests at scale", () => {
  test.slow();

  test("installs 846 solutions and opens one with image-only answer choices", async ({ page }) => {
    await installModule(page, SUBJECT.real);
    await expect(page.getByText(/846 solutions/)).toBeVisible();

    // A problem whose four MC choices are pure graph .gif images (the case
    // the preprocessor's reference-based image copy + the AI vision fix
    // both targeted).
    await openSolution(page, "alg1ptests_10_1_chapter10practicetest_7_10");
    await expect(choices(page)).toHaveCount(4);

    const imgs = choices(page).locator("img");
    await expect(imgs).toHaveCount(4);
    for (let i = 0; i < 4; i++) {
      const ok = await imgs.nth(i).evaluate((el) => {
        const im = el as HTMLImageElement;
        return im.complete && im.naturalWidth > 0;
      });
      expect(ok, `choice image ${i} loaded`).toBe(true);
    }
  });
});
