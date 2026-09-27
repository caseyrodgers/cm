import { test, expect } from "@playwright/test";

/**
 * Regression test for a real bug (found 2026-09-19): StepEditor's TipTap
 * instance only loaded StarterKit + the custom MathNode — no Image node —
 * so ProseMirror's HTML parser silently dropped every <img> on load. Any
 * MC choice/statement/step authored as an image (SOLUTION_INFO.org: ~43%
 * of the corpus) rendered as empty in the editor, and saving would have
 * permanently deleted the image reference from the real content. Fixed by
 * adding @tiptap/extension-image to StepEditor's extensions.
 *
 * alg1ptests_12_1_chapter12practicetest_1_1 is real: its statement/prompt
 * are empty and all 4 MC choices are pure diagram images, no text — the
 * exact shape that exposed the bug.
 */
test("editor renders image-only MC choices, not a blank editor", async ({ page }) => {
  const pid = "alg1ptests_12_1_chapter12practicetest_1_1";
  await page.goto(`/editor/#/s/${pid}`);

  await expect(page.getByRole("heading", { name: pid })).toBeVisible();

  const choiceImages = page.locator('[data-testid="choice-content"] img');
  await expect(choiceImages).toHaveCount(4);
  for (let i = 0; i < 4; i++) {
    const img = choiceImages.nth(i);
    await expect(img).toBeVisible();
    await expect(async () => {
      expect(await img.evaluate((el: HTMLImageElement) => el.naturalWidth)).toBeGreaterThan(0);
    }).toPass({ timeout: 10_000 });
  }
});
