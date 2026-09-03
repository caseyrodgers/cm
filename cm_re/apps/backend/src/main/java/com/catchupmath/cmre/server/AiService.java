package com.catchupmath.cmre.server;

/**
 * The "Learn" / AI explanation service.
 *
 * getAIForProblem(pid): look the problem statement up by pid
 * (SolutionStore), build a small prompt with it as context, and ask
 * Claude (ClaudeClient) for an explanation. Returns a JSON string
 * {pid, text, placeholder}. On any failure — no API key, unknown pid,
 * API error — it returns placeholder:true with a readable message so
 * the tutor UI degrades instead of erroring.
 *
 * The pre-generated / cached design is still the end goal (see
 * cm/AI_DISCUSS.org); this is the direct live call.
 */
public final class AiService {

    private final SolutionStore store;
    private final ClaudeClient claude;

    public AiService(SolutionStore store) {
        this.store = store;
        this.claude = new ClaudeClient();
    }

    /**
     * @param grade target grade level ("7", "10", "12", or blank) — woven
     *   into the prompt so the explanation is pitched appropriately.
     */
    public String getAIForProblem(String pid, String grade) {
        String safePid = pid == null ? "" : pid;

        if (!claude.isConfigured()) {
            return payload(safePid, "Set ANTHROPIC_API_KEY on the server to enable AI explanations.", true);
        }

        String problem = store.problemTextFor(safePid).orElse(null);
        if (problem == null) {
            return payload(safePid, "No problem found for id \"" + safePid + "\".", true);
        }

        String prompt = "You are a patient math tutor. A student is stuck on this problem:\n\n"
                + problem
                + "\n\nExplain how to solve it, step by step, in plain language a student can follow. Be concise."
                + gradeLevelPhrase(grade)
                + "\n\nReturn the answer as an HTML fragment. Prose in <p>; steps in <ol><li>;"
                + " emphasis with <strong>. Write EVERY formula, fraction, equation and"
                + " numeric expression as MathML inside <math>...</math> (e.g."
                + " <math><mfrac><mn>20</mn><mn>160</mn></mfrac></math>). No Markdown, no LaTeX,"
                + " no $ delimiters, no <script>/<style>/<img>, no surrounding <html> or"
                + " <body> tags — just the fragment.";

        try {
            String text = claude.complete(prompt);
            return payload(safePid, text, false);
        } catch (Exception e) {
            System.err.println("AiService: " + e);
            return payload(safePid, "Couldn't reach the AI service: " + e.getMessage(), true);
        }
    }

    /** "" when no usable grade, else a sentence telling the model who to pitch to. */
    static String gradeLevelPhrase(String grade) {
        String digits = grade == null ? "" : grade.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return "";
        }
        return " Pitch it for a grade-" + digits + " student — use the vocabulary, notation,"
                + " and math background typical of that level.";
    }

    private static String payload(String pid, String text, boolean placeholder) {
        return "{"
                + "\"pid\":" + jsonString(pid) + ","
                + "\"text\":" + jsonString(text) + ","
                + "\"placeholder\":" + placeholder
                + "}";
    }

    /** Minimal JSON string escaping — the response is hand-built to stay small. */
    static String jsonString(String s) {
        StringBuilder b = new StringBuilder(s.length() + 2).append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> {
                    if (c < 0x20) {
                        b.append(String.format("\\u%04x", (int) c));
                    } else {
                        b.append(c);
                    }
                }
            }
        }
        return b.append('"').toString();
    }
}
