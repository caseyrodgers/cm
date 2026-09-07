import { test, expect } from "@playwright/test";
import { SUBJECT, installModule, answerKey, choices, celebration } from "../helpers";

/**
 * Regression: a practice test withholds feedback until the score screen,
 * and the score-screen review is read-only — so getting a quiz question
 * right used to earn the student no celebration anywhere. QuestionView
 * now fires it on mount in review mode when the recorded answer is
 * correct.
 */
test.describe("score-screen review celebration", () => {
  async function runOneQuestionTest(page: import("@playwright/test").Page, pick: "correct" | "wrong") {
    await installModule(page, SUBJECT.demo);
    await page.goto(`/#/t/${SUBJECT.demo}`);
    await page.getByRole("button", { name: /Quick test/i }).click();
    await page.getByRole("button", { name: /^Start/ }).click();

    const correct = Object.values(await answerKey(page, SUBJECT.demo))[0];
    const idx = pick === "correct" ? correct : (correct + 1) % 4;
    await choices(page).nth(idx).click();
    await page.getByTestId("mc-submit").click();
    await page.getByRole("button", { name: /Finish/i }).click();
    await expect(page.getByRole("heading", { name: /your score/i })).toBeVisible();
  }

  test("opening a row you got RIGHT pops the celebration", async ({ page }) => {
    await runOneQuestionTest(page, "correct");
    await page.getByRole("button").filter({ hasText: /you: [A-D]/ }).click();

    await expect(celebration(page)).toBeVisible();
    await expect(celebration(page)).toContainText("Correct!");
    await expect(celebration(page)).toBeHidden({ timeout: 5_000 });
  });

  test("opening a row you got WRONG stays silent", async ({ page }) => {
    await runOneQuestionTest(page, "wrong");
    await page.getByRole("button").filter({ hasText: /you: [A-D]/ }).click();

    // the review still renders (statement + read-only question)
    await expect(page.getByTestId("mc-question")).toBeVisible();
    await page.waitForTimeout(600);
    await expect(celebration(page)).toHaveCount(0);
  });
});
