import { test, expect } from "@playwright/test";
import { MC_PID, SUBJECT, installModule, openSolution, answerKey, choices } from "../helpers";

/** Reads the trailing number out of the header "✓ N" badge. */
async function total(page: import("@playwright/test").Page): Promise<number> {
  const t = (await page.getByTestId("correct-total").innerText()).replace(/[^\d]/g, "");
  return t === "" ? NaN : Number(t);
}

test.describe("running total of correct answers", () => {
  test("increments on a right answer and survives a reload", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);
    expect(await total(page)).toBe(0);

    const correct = (await answerKey(page, SUBJECT.demo))[MC_PID];
    await choices(page).nth(correct).click();
    await page.getByTestId("mc-submit").click();
    await expect(page.getByTestId("correct-total")).toHaveText(/1$/);

    // the whole point: persists across a browser restart (reload stands in)
    await page.reload();
    await expect(page.getByTestId("correct-total")).toHaveText(/1$/);
  });

  test("a wrong answer does not increment it", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);
    const correct = (await answerKey(page, SUBJECT.demo))[MC_PID];

    await choices(page).nth((correct + 1) % 4).click();
    await page.getByTestId("mc-submit").click();
    await expect(page.getByTestId("mc-question").getByText("Not quite")).toBeVisible();
    expect(await total(page)).toBe(0);
  });

  test("finishing a practice test adds its correct count, once", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await page.goto(`/#/t/${SUBJECT.demo}`);
    await page.getByRole("button", { name: /Quick test/i }).click();
    await page.getByRole("button", { name: /^Start/ }).click();

    const correct = Object.values(await answerKey(page, SUBJECT.demo))[0];
    await choices(page).nth(correct).click();
    await page.getByTestId("mc-submit").click(); // test mode: no tally yet
    expect(await total(page)).toBe(0);

    await page.getByRole("button", { name: /Finish/i }).click();
    await expect(page.getByRole("heading", { name: /your score/i })).toBeVisible();
    await expect(page.getByTestId("correct-total")).toHaveText(/1$/);

    // reopening the finished test must not double-count
    await page.reload();
    await page.goto(`/#/t/${SUBJECT.demo}`);
    await expect(page.getByTestId("correct-total")).toHaveText(/1$/);
  });
});
