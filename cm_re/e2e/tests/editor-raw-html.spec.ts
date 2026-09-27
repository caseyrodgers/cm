import { test, expect } from "@playwright/test";

/**
 * "View HTML" toggle in StepEditor (added 2026-09-19, Casey: "we need to
 * be able to get to the raw html/mathml in question steps") — MathNode's
 * click-to-edit prompt only reaches one <math> element at a time; this
 * gives access to a whole step/choice/statement's exact raw markup.
 *
 * alg1ptests_1_1_chapter1practicetest_10_1 (the workhorse MC fixture,
 * see e2e/helpers.ts) has real embedded MathML in its choices.
 */
test("View HTML shows and lets you edit a choice's raw markup, including MathML", async ({ page }) => {
  const pid = "alg1ptests_1_1_chapter1practicetest_10_1";
  await page.goto(`/editor/#/s/${pid}`);
  await expect(page.getByRole("heading", { name: pid })).toBeVisible();

  const firstChoice = page.locator('[data-testid="choice-content"]').first();
  const toggle = firstChoice.getByTestId("raw-html-toggle");
  const textarea = firstChoice.getByTestId("raw-html-textarea");

  // Rich view by default, no raw textarea.
  await expect(textarea).toHaveCount(0);

  await toggle.click();
  await expect(textarea).toBeVisible();
  const raw = await textarea.inputValue();
  expect(raw).toMatch(/<math/); // real MathML choice content, not stripped
  expect(raw.length).toBeGreaterThan(0);

  // Cancel discards the edit and leaves the choice's rendered content untouched.
  await textarea.fill(raw + "<p>should not stick</p>");
  await firstChoice.getByTestId("raw-html-cancel").click();
  await expect(textarea).toHaveCount(0);
  await expect(firstChoice.getByText("should not stick")).toHaveCount(0);

  // Apply commits the raw edit back into the rich view.
  await toggle.click();
  await expect(textarea).toBeVisible();
  await textarea.fill(raw + "<p>edited via raw html</p>");
  await firstChoice.getByTestId("raw-html-apply").click();
  await expect(textarea).toHaveCount(0);
  await expect(firstChoice.getByText("edited via raw html")).toBeVisible();
});
