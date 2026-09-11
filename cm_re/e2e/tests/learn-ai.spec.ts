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
});
