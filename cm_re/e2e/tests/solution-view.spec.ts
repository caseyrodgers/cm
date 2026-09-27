import { test, expect } from "@playwright/test";
import { MC_PID, SUBJECT, installModule, openSolution, answerKey, choices, celebration } from "../helpers";

test.describe("solution view", () => {
  test.beforeEach(async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);
  });

  test("renders the MC question as native MathML", async ({ page }) => {
    const q = page.getByTestId("mc-question");
    await expect(q).toBeVisible();
    await expect(choices(page)).toHaveCount(4);
    // Statement + choices are authored as <math> fragments, rendered natively.
    await expect(page.locator("math").first()).toBeVisible();
    expect(await page.locator("math").count()).toBeGreaterThan(1);
  });

  test("practice mode's 'Check answer' stays the quieter outline button, unlike a test's 'Submit answer'", async ({ page }) => {
    const submit = page.getByTestId("mc-submit");
    await expect(submit).toHaveText(/Check answer/i);
    await expect(submit).not.toHaveClass(/bg-blue-600/);
  });

  test("checking the correct answer reveals it and fires the celebration", async ({ page }) => {
    const key = await answerKey(page, SUBJECT.demo);
    const correct = key[MC_PID];
    expect(typeof correct).toBe("number");

    await choices(page).nth(correct).click();
    await page.getByTestId("mc-submit").click(); // "Check answer"

    // inline reveal: the correct choice gets its ✓ badge and the choices lock
    await expect(choices(page).nth(correct).getByLabel("correct answer")).toBeVisible();
    await expect(page.getByTestId("mc-submit")).toBeDisabled();

    // the reward burst
    await expect(celebration(page)).toBeVisible();
    await expect(celebration(page)).toContainText("Correct!");
    // and it auto-dismisses
    await expect(celebration(page)).toBeHidden({ timeout: 5_000 });
  });

  test("checking a wrong answer does NOT celebrate", async ({ page }) => {
    const key = await answerKey(page, SUBJECT.demo);
    const wrong = (key[MC_PID] + 1) % 4;

    await choices(page).nth(wrong).click();
    await page.getByTestId("mc-submit").click();

    await expect(page.getByTestId("mc-question").getByText("Not quite")).toBeVisible();
    await page.waitForTimeout(500); // give any async mount a chance
    await expect(celebration(page)).toHaveCount(0);
  });

  test("shows 'View source directory' for a solution converted from the legacy corpus", async ({ page }) => {
    await expect(page.getByRole("link", { name: /View source directory/i })).toBeVisible();
    await expect(page.getByRole("link", { name: "Editor" })).toHaveCount(0);
  });

  test("shows an 'Editor' link (opens apps/editor for this pid) for a solution with no legacy source", async ({ page }) => {
    // sol-linear-eq-1 is a synthetic fixture in the same installed
    // algebra1 module -- no data/solutionSources.ts entry.
    await page.goto("/#/s/sol-linear-eq-1");
    const link = page.getByRole("link", { name: "Editor" });
    await expect(link).toBeVisible();
    await expect(link).toHaveAttribute("href", "/editor/#/s/sol-linear-eq-1");
    await expect(page.getByRole("link", { name: /View source directory/i })).toHaveCount(0);
  });
});
