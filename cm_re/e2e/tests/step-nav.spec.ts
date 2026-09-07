import { test, expect } from "@playwright/test";
import { MC_PID, SUBJECT, installModule, openSolution, rapidClick } from "../helpers";

test.describe("step navigation", () => {
  test.beforeEach(async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID); // 2 worked steps
  });

  test("walks to the last step and stops", async ({ page }) => {
    const counter = page.getByTestId("step-counter");
    await expect(counter).toHaveText("1 / 2");
    await expect(page.getByTestId("step-prev")).toBeDisabled();

    await page.getByTestId("step-next").click();
    await expect(counter).toHaveText("2 / 2");
    await expect(page.getByTestId("step-next")).toBeDisabled();
    await expect(page.getByTestId("step-prev")).toBeEnabled();
  });

  test("a rapid double-tap past the end cannot wedge the view (stepIndex clamp)", async ({ page }) => {
    // Fire 25 synchronous clicks so React batches them — pre-fix this pushed
    // stepIndex past the end, re-enabled both nav buttons, and showed
    // "This solution has no steps." on a solution that has them.
    await rapidClick(page, '[data-testid="step-next"]', 25);

    await expect(page.getByText("This solution has no steps.")).toHaveCount(0);
    await expect(page.getByTestId("step-counter")).toHaveText(/^[12] \/ 2$/);
    await expect(page.getByTestId("step-next")).toBeDisabled(); // clamped at the last step

    // and it still recovers going backwards
    await rapidClick(page, '[data-testid="step-prev"]', 25);
    await expect(page.getByTestId("step-counter")).toHaveText("1 / 2");
    await expect(page.getByTestId("step-prev")).toBeDisabled();
  });
});
