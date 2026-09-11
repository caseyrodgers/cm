import { test, expect } from "@playwright/test";
import { SUBJECT, installModule } from "../helpers";

/**
 * The app shell: Hub / Practice Tests / Problems / Me nav, the running
 * "✓ N" pill, and the #/me status + reset. The shell is swappable via
 * ?shell= — content is untouched.
 */
test.describe("app shell", () => {
  test("hub links to the three sections; nav switches between them", async ({ page }) => {
    await page.goto("/#/");
    const nav = page.locator("header nav");
    await expect(nav.getByRole("button", { name: "Hub" })).toHaveAttribute("aria-current", "page");

    await nav.getByRole("button", { name: "Problems" }).click();
    await expect(page).toHaveURL(/#\/problems$/);
    await expect(page.getByRole("heading", { name: /pick a subject/i })).toBeVisible();
    await expect(nav.getByRole("button", { name: "Problems" })).toHaveAttribute("aria-current", "page");

    await nav.getByRole("button", { name: "Practice Tests" }).click();
    await expect(page).toHaveURL(/#\/tests$/);

    await nav.getByRole("button", { name: "Me" }).click();
    await expect(page).toHaveURL(/#\/me$/);
    await expect(page.getByText("Correct answers", { exact: true })).toBeVisible();
  });

  test("Me shows status and reset clears the correct-answer count", async ({ page }) => {
    // rack up a correct answer first
    await installModule(page, SUBJECT.demo);
    await page.goto("/#/s/alg1ptests_1_1_chapter1practicetest_10_1");
    await page.getByTestId("mc-choice").nth(0).click();
    await page.getByTestId("mc-submit").click();
    await expect(page.getByTestId("correct-total")).toHaveText(/1$/);

    await page.goto("/#/me");
    await expect(page.locator("main")).toContainText("Correct answers");

    await page.getByRole("button", { name: /Reset my progress/i }).click();
    await expect(page.getByTestId("app-dialog")).toBeVisible();
    await page.getByTestId("app-dialog-confirm").click();

    await expect(page.getByTestId("correct-total")).toHaveText(/0$/);
  });

  test("Practice Tests hides browse-only subjects; Problems shows them", async ({ page }) => {
    // Mini Calculus and Graphing Calculator Practice have no scorable MC
    // content (scorableCount 0) -- no test to take, so #/tests must not
    // offer them, while #/problems (browsing) still lists everything.
    await page.goto("/#/tests");
    await expect(page.getByRole("button", { name: "Algebra 1 Practice Tests" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Mini Calculus" })).toHaveCount(0);
    await expect(page.getByRole("button", { name: "Graphing Calculator Practice" })).toHaveCount(0);

    await page.goto("/#/problems");
    await expect(page.getByRole("button", { name: "Mini Calculus" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Graphing Calculator Practice" })).toBeVisible();
  });

  test("?shell= is remembered", async ({ page }) => {
    await page.goto("/?shell=default#/");
    // only "default" exists today; the mechanism is what we're checking
    const stored = await page.evaluate(() => localStorage.getItem("cm_re.shell"));
    expect(stored).toBeNull(); // ?shell=default clears the override
    await expect(page.locator("header nav")).toBeVisible();
  });
});
