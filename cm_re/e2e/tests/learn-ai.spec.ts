import { test, expect } from "@playwright/test";
import { MC_PID, SUBJECT, installModule, openSolution } from "../helpers";

/**
 * Hits the real Claude API through the Java server, so it costs money and
 * is non-deterministic. Skipped unless RUN_AI_TESTS=1 (and the server was
 * started with ANTHROPIC_API_KEY set).
 *
 *   RUN_AI_TESTS=1 npx playwright test learn-ai      (from cm_re/e2e)
 */
test.describe("Learn — AI explanation", () => {
  test.skip(process.env.RUN_AI_TESTS !== "1", "set RUN_AI_TESTS=1 to run live AI tests");

  test("explains a problem, and the answer isn't truncated mid-sentence", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);

    await page.getByRole("button", { name: /Learn .* explain this problem/i }).click();
    // Picking a grade fires the explanation immediately — no separate submit step.
    await page.getByRole("button", { name: /10th Grader/i }).click();

    const explanation = page.locator(".learn-explanation");
    await expect(explanation).toBeVisible({ timeout: 60_000 });
    await expect(explanation).not.toContainText(/unavailable right now/i); // placeholder path

    const text = ((await explanation.innerText()) ?? "").replace(/\s+/g, " ").trim();
    // A full grade-pitched explanation of this problem is long; the old
    // max_tokens=1024 cap truncated it to a few hundred chars mid-clause.
    expect(text.length).toBeGreaterThan(400);
    // ...and it shouldn't stop on a dangling connective/operator (the
    // "Since a = 2 > 0" symptom). It may legitimately end on a formula,
    // so we don't require sentence punctuation.
    expect(text).not.toMatch(/\b(the|a|an|is|are|and|so|since|then|to|of|with|for|by|we|it)\s*$/i);
    expect(text).not.toMatch(/[=+\-*/,:]\s*$/);
  });

  test("a follow-up question gets a real, contextual answer", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);

    await page.getByRole("button", { name: /Learn .* explain this problem/i }).click();
    await page.getByRole("button", { name: /10th Grader/i }).click();
    await expect(page.locator(".learn-explanation").first()).toBeVisible({ timeout: 60_000 });

    const input = page.getByPlaceholder(/Ask a follow-up/i);
    await input.fill("what is the general form of this equation?");
    await page.getByRole("button", { name: "Ask" }).click();

    // A second .learn-explanation block is the follow-up's answer.
    await expect(page.locator(".learn-explanation")).toHaveCount(2, { timeout: 60_000 });
    const answer = ((await page.locator(".learn-explanation").nth(1).innerText()) ?? "").trim();
    expect(answer.length).toBeGreaterThan(50);
    expect(answer).not.toMatch(/unavailable right now/i); // placeholder path
    expect(answer).not.toMatch(/no problem found|couldn.t reach the ai service/i);

    // The question itself is echoed above its answer.
    await expect(page.getByText("what is the general form of this equation?")).toBeVisible();
  });

  test("each AI result can be removed independently", async ({ page }) => {
    await installModule(page, SUBJECT.demo);
    await openSolution(page, MC_PID);

    await page.getByRole("button", { name: /Learn .* explain this problem/i }).click();
    await page.getByRole("button", { name: /10th Grader/i }).click();
    await expect(page.locator(".learn-explanation").first()).toBeVisible({ timeout: 60_000 });

    const input = page.getByPlaceholder(/Ask a follow-up/i);
    await input.fill("what is the general form of this equation?");
    await page.getByRole("button", { name: "Ask" }).click();
    await expect(page.locator(".learn-explanation")).toHaveCount(2, { timeout: 60_000 });

    // Removing the follow-up leaves the main explanation (and its remove
    // button) alone.
    await page.getByRole("button", { name: "Remove this answer" }).click();
    await expect(page.locator(".learn-explanation")).toHaveCount(1);
    await expect(page.getByText("what is the general form of this equation?")).toHaveCount(0);
    await expect(page.getByRole("button", { name: "Remove explanation" })).toBeVisible();

    // Removing the main explanation clears everything -- back to the grade picker.
    await page.getByRole("button", { name: "Remove explanation" }).click();
    await expect(page.locator(".learn-explanation")).toHaveCount(0);
    await expect(page.getByRole("button", { name: /10th Grader/i })).toBeVisible();
  });
});
