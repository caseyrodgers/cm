import { test, expect } from "@playwright/test";
import { SUBJECT, installModule } from "../helpers";

/**
 * "Show all problems" groups the list into collapsible chapter nodes,
 * each labelled with the topic name baked into the manifest
 * (ChapterNamer). Uses the real subject so there are named chapters —
 * @slow (846-solution install).
 */
test.describe("@slow chapter list", () => {
  test.slow();

  test("problems are grouped into collapsible, named chapter nodes", async ({ page }) => {
    await installModule(page, SUBJECT.real);
    await page.getByRole("button", { name: /Show all \d+ problems/i }).click();

    const ch1 = page.getByRole("button", { name: /^\s*›?\s*Chapter 1: .+\d+$/ });
    await expect(ch1.first()).toBeVisible();

    // headers are chapter toggles; collapsed by default -> no problem rows yet
    const headers = page.locator("button[aria-expanded]");
    expect(await headers.count()).toBeGreaterThan(5);
    await expect(page.getByRole("button", { name: /Chapter 3 Practice Test .* Problem 1$/ })).toHaveCount(0);

    // expand chapter 3 -> its problems appear
    const ch3 = page.getByRole("button", { name: /Chapter 3: .+/ });
    await ch3.click();
    await expect(ch3).toHaveAttribute("aria-expanded", "true");
    await expect(page.getByRole("button", { name: /Chapter 3 Practice Test .* Problem 1$/ }).first()).toBeVisible();

    // collapse again
    await ch3.click();
    await expect(ch3).toHaveAttribute("aria-expanded", "false");
    await expect(page.getByRole("button", { name: /Chapter 3 Practice Test .* Problem 1$/ })).toHaveCount(0);
  });
});
