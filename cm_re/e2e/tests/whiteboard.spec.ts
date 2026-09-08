import { test, expect } from "@playwright/test";
import { MC_PID, SUBJECT, installModule, openSolution } from "../helpers";

test.describe("whiteboard", () => {
  test.beforeEach(async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);
  });

  test("opens as an overlay that covers the problem/step card", async ({ page }) => {
    const toggle = page.getByRole("button", { name: /^Whiteboard/ });
    await toggle.click();

    const board = page.locator("aside").filter({ has: page.locator("canvas") });
    await expect(board).toBeVisible();
    await expect(board.locator("canvas")).toBeVisible();

    // the board's box should sit on top of the card and cover it
    const card = page.locator(".rounded-lg.border").filter({ has: page.getByRole("heading", { level: 2 }) }).first();
    const b = await board.boundingBox();
    const c = await card.boundingBox();
    expect(b && c).toBeTruthy();
    // aside fills the card (allow a few px for borders)
    expect(Math.abs(b!.x - c!.x)).toBeLessThan(4);
    expect(Math.abs(b!.y - c!.y)).toBeLessThan(4);
    expect(Math.abs(b!.width - c!.width)).toBeLessThan(4);
    expect(Math.abs(b!.height - c!.height)).toBeLessThan(4);

    // it's an overlay, not a viewport-edge panel: its right edge is the
    // card's right edge, well inside the window
    expect(b!.x + b!.width).toBeLessThan(page.viewportSize()!.width - 20);
  });

  test("Escape closes it", async ({ page }) => {
    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    const board = page.locator("aside").filter({ has: page.locator("canvas") });
    await expect(board).toBeVisible();
    await page.keyboard.press("Escape");
    await expect(board).toBeHidden();
  });

  test("a drawn stroke persists across a reload", async ({ page }) => {
    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    const canvas = page.locator("aside canvas");
    const box = (await canvas.boundingBox())!;
    await page.mouse.move(box.x + 30, box.y + 30);
    await page.mouse.down();
    await page.mouse.move(box.x + 90, box.y + 70, { steps: 8 });
    await page.mouse.move(box.x + 140, box.y + 40, { steps: 8 });
    await page.mouse.up();
    await expect(page.getByRole("button", { name: /^Whiteboard \(1\)/ })).toBeVisible();

    await page.waitForTimeout(600); // debounced save
    await page.reload();
    await expect(page.getByRole("button", { name: /^Whiteboard \(1\)/ })).toBeVisible();
  });
});
