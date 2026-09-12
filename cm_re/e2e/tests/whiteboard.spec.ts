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

  test("Clear wipes immediately with no confirmation, and Undo brings it all back", async ({ page }) => {
    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    const canvas = page.locator("aside canvas");
    const box = (await canvas.boundingBox())!;

    async function stroke(dx1: number, dy1: number, dx2: number, dy2: number) {
      await page.mouse.move(box.x + dx1, box.y + dy1);
      await page.mouse.down();
      await page.mouse.move(box.x + dx2, box.y + dy2, { steps: 6 });
      await page.mouse.up();
    }
    await stroke(30, 30, 90, 70);
    await stroke(50, 100, 120, 140);
    await expect(page.getByRole("button", { name: /^Whiteboard \(2\)/ })).toBeVisible();

    const clearBtn = page.getByRole("button", { name: "Clear" });
    const undoBtn = page.getByRole("button", { name: "Undo" });

    await clearBtn.click();
    // no confirmation dialog at all
    await expect(page.getByTestId("app-dialog")).toBeHidden();
    await expect(page.getByRole("button", { name: /^Whiteboard$/ })).toBeVisible(); // count gone, back to bare label
    await expect(undoBtn).toBeEnabled(); // clearing is itself undoable

    await undoBtn.click();
    await expect(page.getByRole("button", { name: /^Whiteboard \(2\)/ })).toBeVisible(); // both strokes back at once

    // survives a reload too (clearWhiteboard wasn't left half-applied)
    await page.waitForTimeout(600);
    await page.reload();
    await expect(page.getByRole("button", { name: /^Whiteboard \(2\)/ })).toBeVisible();
  });

  test("problem-visibility slider: right reveals the problem, left hides it; sticky across a reload", async ({
    page,
  }) => {
    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    const canvas = page.locator("aside canvas");
    const slider = page.locator("#wb-opacity");

    await expect(slider).toHaveAttribute("min", "0");
    await expect(slider).toHaveAttribute("max", "0.8"); // never a fully opaque board, even at the "hidden" end
    await expect(slider).toHaveValue("0.2"); // default = 25% visibility (0.25 * 0.8)
    await expect(canvas).toHaveCSS("background-color", "rgba(255, 255, 255, 0.6)");

    // drag all the way right — problem should become fully visible (canvas fully transparent)
    await slider.fill("0.8");
    await slider.dispatchEvent("input");
    await expect(canvas).toHaveCSS("background-color", "rgba(255, 255, 255, 0)");
    // ...and the overlay container itself must carry no fixed backdrop of its own,
    // or it caps visibility regardless of what the canvas does (regression: it
    // used to have a fixed bg-white/80 sitting behind the canvas).
    await expect(page.locator("aside")).toHaveCSS("background-color", "rgba(0, 0, 0, 0)");

    // drag all the way left — problem should be as hidden as it gets (canvas at max alpha)
    await slider.fill("0");
    await slider.dispatchEvent("input");
    await expect(canvas).toHaveCSS("background-color", "rgba(255, 255, 255, 0.8)");

    await slider.fill("0.6");
    await slider.dispatchEvent("input");
    await page.reload();
    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    await expect(page.locator("#wb-opacity")).toHaveValue("0.6"); // sticky in localStorage
  });

  test("Ask AI about my work — disabled until a stroke exists, POSTs the pid + a PNG, renders feedback", async ({
    page,
  }) => {
    // Mocked — a live version of this same flow (real Claude vision
    // call) is covered separately in learn-ai.spec.ts (RUN_AI_TESTS=1).
    let capturedImage: string | undefined;
    await page.route(`**/api/ai/check-work/${MC_PID}`, async (route) => {
      capturedImage = (route.request().postDataJSON() as { image?: string })?.image;
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ pid: MC_PID, feedback: "<p>Nice work, the steps track.</p>", placeholder: false }),
      });
    });

    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    const askBtn = page.getByRole("button", { name: /Ask AI about my work/i });
    await expect(askBtn).toBeDisabled(); // no strokes yet

    const canvas = page.locator("aside canvas");
    const box = (await canvas.boundingBox())!;
    await page.mouse.move(box.x + 30, box.y + 30);
    await page.mouse.down();
    await page.mouse.move(box.x + 90, box.y + 70, { steps: 8 });
    await page.mouse.up();
    await expect(askBtn).toBeEnabled();

    await askBtn.click();
    await expect(page.locator(".learn-explanation")).toContainText("Nice work, the steps track.");
    expect(capturedImage).toBeTruthy();
    expect(capturedImage!.length).toBeGreaterThan(100); // a real base64 PNG, not a stub

    // dismissing the result doesn't touch the board itself, just hides
    // the panel that was pushing it out of view
    await page.getByRole("button", { name: "dismiss AI feedback" }).click();
    await expect(page.locator(".learn-explanation")).toHaveCount(0);
    await expect(page.locator("aside canvas")).toBeVisible();
  });

  test('Read back what I wrote — disabled until a stroke exists, POSTs the pid + a PNG, renders the transcription', async ({
    page,
  }) => {
    // Mocked, same reasoning as the "Ask AI" test above — a live version
    // hitting real Claude vision was manually verified this session
    // ("x = 7" scribble -> transcription "x = 7"; a random illegible
    // scribble -> an honest "no legible work" sentence, not a guess).
    let capturedImage: string | undefined;
    await page.route(`**/api/ai/read-work/${MC_PID}`, async (route) => {
      capturedImage = (route.request().postDataJSON() as { image?: string })?.image;
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ pid: MC_PID, transcription: "x = 7", placeholder: false }),
      });
    });

    await page.getByRole("button", { name: /^Whiteboard/ }).click();
    const readBtn = page.getByRole("button", { name: /Read back what I wrote/i });
    await expect(readBtn).toBeDisabled(); // no strokes yet

    const canvas = page.locator("aside canvas");
    const box = (await canvas.boundingBox())!;
    await page.mouse.move(box.x + 30, box.y + 30);
    await page.mouse.down();
    await page.mouse.move(box.x + 90, box.y + 70, { steps: 8 });
    await page.mouse.up();
    await expect(readBtn).toBeEnabled();

    await readBtn.click();
    await expect(page.locator(".font-mono")).toHaveText("x = 7");
    expect(capturedImage).toBeTruthy();
    expect(capturedImage!.length).toBeGreaterThan(100); // a real base64 PNG, not a stub

    await page.getByRole("button", { name: "dismiss transcription" }).click();
    await expect(page.locator(".font-mono")).toHaveCount(0);
    await expect(page.locator("aside canvas")).toBeVisible();
  });
});
