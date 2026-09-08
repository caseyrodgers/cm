import { test, expect } from "@playwright/test";
import { SUBJECT, installModule, answerKey, choices } from "../helpers";

/**
 * The `algebra1` demo bundle has exactly one scorable MC problem, so a
 * "Quick test" there is a deterministic 1-question test — enough to
 * exercise the whole start -> answer -> finish -> score pipeline.
 */
test.describe("practice test", () => {
  test.beforeEach(async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await page.goto(`/#/t/${SUBJECT.demo}`);
  });

  test("start -> answer -> finish -> score screen", async ({ page }) => {
    await page.getByRole("button", { name: /Quick test/i }).click();
    await page.getByRole("button", { name: /^Start/ }).click();

    // test mode: the button says "Submit answer" and nothing is revealed
    await expect(page.getByTestId("mc-submit")).toHaveText(/Submit answer/i);
    // no "Learn" while the test is in progress
    await expect(page.getByRole("button", { name: /Learn .* explain this problem/i })).toHaveCount(0);

    const key = await answerKey(page, SUBJECT.demo);
    const correct = Object.values(key)[0];
    await choices(page).nth(correct).click();
    await page.getByTestId("mc-submit").click();

    // no inline right/wrong feedback during the test, no celebration
    await expect(page.getByText("Correct", { exact: true })).toHaveCount(0);
    await expect(page.getByRole("status", { name: "Correct!" })).toHaveCount(0);

    await page.getByRole("button", { name: /Finish/i }).click();
    await expect(page.getByRole("heading", { name: /your score/i })).toBeVisible();
    await expect(page.getByText(/^1 \/ 1$/)).toBeVisible();
  });

  test("an unfinished test resumes where it was left", async ({ page }) => {
    await page.getByRole("button", { name: /Quick test/i }).click();
    await expect(page.getByRole("button", { name: /Finish/i })).toBeVisible();

    await page.reload();
    await page.goto(`/#/t/${SUBJECT.demo}`);
    // back into the active test's index, not the picker
    await expect(page.getByRole("button", { name: /Finish/i })).toBeVisible();
    await expect(page.getByRole("button", { name: /Quick test/i })).toHaveCount(0);
  });
});
