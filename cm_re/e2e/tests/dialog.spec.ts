import { test, expect } from "@playwright/test";
import { SUBJECT, installModule } from "../helpers";

/**
 * The custom modal that replaced window.confirm / window.alert
 * (lib/dialog.ts + DialogHost). Exercised through the "Remove download"
 * confirm.
 */
test.describe("app dialog", () => {
  test.beforeEach(async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await page.goto(`/#/m/${SUBJECT.demo}`);
    await expect(page.getByText(/Installed for offline use/i)).toBeVisible();
  });

  test("cancel leaves things alone; confirm goes through", async ({ page }) => {
    const dialog = page.getByTestId("app-dialog");

    // open, then cancel — module stays installed
    await page.getByRole("button", { name: /Remove download/i }).click();
    await expect(dialog).toBeVisible();
    await expect(dialog).toContainText(/Remove download/i);
    await page.getByTestId("app-dialog-cancel").click();
    await expect(dialog).toBeHidden();
    await expect(page.getByText(/Installed for offline use/i)).toBeVisible();

    // open again, Escape also cancels
    await page.getByRole("button", { name: /Remove download/i }).click();
    await expect(dialog).toBeVisible();
    await page.keyboard.press("Escape");
    await expect(dialog).toBeHidden();
    await expect(page.getByText(/Installed for offline use/i)).toBeVisible();

    // now confirm — module is removed
    await page.getByRole("button", { name: /Remove download/i }).click();
    await page.getByTestId("app-dialog-confirm").click();
    await expect(page.getByRole("button", { name: /Download for offline/i })).toBeVisible();
  });
});
