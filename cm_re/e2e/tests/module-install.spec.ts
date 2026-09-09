import { test, expect } from "@playwright/test";
import { SUBJECT, installModule } from "../helpers";

test.describe("module install", () => {
  test("subject picker lists the seeded subjects", async ({ page }) => {
    await page.goto("/#/");
    await expect(page.getByRole("heading", { name: /Choose a subject/i })).toBeVisible();
    for (const name of [
      "Algebra 1 Practice Tests",
      "Algebra 2 Practice Tests",
      "Geometry Practice Tests",
      "Pre-Algebra Practice Tests",
      "Graphing Calculator Practice",
    ]) {
      await expect(page.getByRole("button", { name: new RegExp(name) })).toBeVisible();
    }
  });

  test("download makes a module usable offline, remove tears it down", async ({ page }) => {
    await page.goto(`/#/m/${SUBJECT.demo}`);
    await expect(page.getByText(/\d+ solutions/)).toBeVisible();

    await page.getByRole("button", { name: /Download for offline/i }).click();
    await expect(page.getByText(/Installed for offline use/i)).toBeVisible({ timeout: 60_000 });
    await expect(page.getByRole("button", { name: /Take a .* practice test/i })).toBeVisible();
    await expect(page.getByRole("button", { name: /Show all \d+ problems/i })).toBeVisible();

    page.once("dialog", (d) => d.accept()); // "Remove download" confirm()
    await page.getByRole("button", { name: /Remove download/i }).click();
    await expect(page.getByRole("button", { name: /Download for offline/i })).toBeVisible();
  });

  test("installed content survives a reload (persisted in IndexedDB)", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await page.reload();
    await page.goto(`/#/m/${SUBJECT.demo}`);
    await expect(page.getByText(/Installed for offline use/i)).toBeVisible();
  });
});
