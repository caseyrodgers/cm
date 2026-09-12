import { test, expect } from "@playwright/test";
import { MC_PID, SUBJECT, installModule, openSolution, answerKey, choices } from "../helpers";

/** Reads the trailing number out of the header "✓ N" badge. */
async function total(page: import("@playwright/test").Page): Promise<number> {
  const t = (await page.getByTestId("correct-total").innerText()).replace(/[^\d]/g, "");
  return t === "" ? NaN : Number(t);
}

/** Reads the #/me "Questions" stat (every answer, right or wrong). */
async function answeredTotal(page: import("@playwright/test").Page): Promise<number> {
  await page.goto("/#/me");
  const t = await page.getByTestId("answered-total").innerText();
  return Number(t.trim());
}

/** Reads the #/me "Problems" stat (every problem viewed, whether or not it's answered). */
async function viewedTotal(page: import("@playwright/test").Page): Promise<number> {
  await page.goto("/#/me");
  const t = await page.getByTestId("viewed-total").innerText();
  return Number(t.trim());
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

  test("a wrong answer does not increment it, but does count toward the total answered", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);
    const correct = (await answerKey(page, SUBJECT.demo))[MC_PID];

    await choices(page).nth((correct + 1) % 4).click();
    await page.getByTestId("mc-submit").click();
    await expect(page.getByTestId("mc-question").getByText("Not quite")).toBeVisible();
    expect(await total(page)).toBe(0);
    expect(await answeredTotal(page)).toBe(1);
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

test.describe("Me — Questions", () => {
  test("counts every answer, right or wrong, and doesn't double-count a finished test", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    expect(await answeredTotal(page)).toBe(0);

    // one right, one wrong, in plain practice mode
    await openSolution(page, MC_PID);
    const correct = (await answerKey(page, SUBJECT.demo))[MC_PID];
    await choices(page).nth(correct).click();
    await page.getByTestId("mc-submit").click();
    expect(await answeredTotal(page)).toBe(1);

    // a 1-question practice test adds its answered count at Finish, once
    // (test-mode answers aren't tallied until Finish — see the header-pill
    // version of this check in "finishing a practice test adds its correct
    // count, once" above; the total here would require leaving the test
    // flow to read the Hub stat, so it's checked only before/after).
    await page.goto(`/#/t/${SUBJECT.demo}`);
    await page.getByRole("button", { name: /Quick test/i }).click();
    await page.getByRole("button", { name: /^Start/ }).click();
    const testCorrect = Object.values(await answerKey(page, SUBJECT.demo))[0];
    await choices(page).nth(testCorrect).click();
    await page.getByTestId("mc-submit").click();
    await page.getByRole("button", { name: /Finish/i }).click();
    await expect(page.getByRole("heading", { name: /your score/i })).toBeVisible();
    expect(await answeredTotal(page)).toBe(2);

    await page.reload();
    await page.goto(`/#/t/${SUBJECT.demo}`);
    expect(await answeredTotal(page)).toBe(2); // reopening a finished test doesn't double-count
  });
});

test.describe("Me — Problems (viewed)", () => {
  test("counts every problem viewed, not deduplicated, in either the standalone view or a lesson walkthrough", async ({
    page,
  }) => {
    await installModule(page, SUBJECT.demo);
    expect(await viewedTotal(page)).toBe(0);

    await openSolution(page, MC_PID);
    expect(await viewedTotal(page)).toBe(1);

    // a different solution
    await page.goto(`/#/s/sol-linear-eq-1`);
    await page.waitForSelector("h2");
    expect(await viewedTotal(page)).toBe(2);

    // re-viewing the first one again still counts (not a "distinct pids" set)
    await openSolution(page, MC_PID);
    expect(await viewedTotal(page)).toBe(3);

    // a practice-test question view does NOT bump this (it's not SolutionNav;
    // QuestionView/PracticeTest already cover it via the Questions stat)
    await page.goto(`/#/t/${SUBJECT.demo}`);
    await page.getByRole("button", { name: /Quick test/i }).click();
    await page.getByRole("button", { name: /^Start/ }).click();
    const correct = Object.values(await answerKey(page, SUBJECT.demo))[0];
    await choices(page).nth(correct).click();
    await page.getByTestId("mc-submit").click();
    expect(await viewedTotal(page)).toBe(3);
  });
});
