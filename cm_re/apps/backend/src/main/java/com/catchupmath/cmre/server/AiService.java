package com.catchupmath.cmre.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

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

        List<ClaudeClient.ImageAttachment> images = loadImages(store.problemImagesFor(safePid));

        String prompt = "You are a patient math tutor. A student is stuck on this problem:\n\n"
                + problem
                + (images.isEmpty() ? "" : "\n\n(Part of this problem — the equation and/or its answer"
                        + " choices — is shown to you only as the attached image(s), not as text above."
                        + " Read the image(s) carefully; they are the actual problem content, not decoration.)")
                + "\n\nExplain how to solve it, step by step, in plain language a student can follow. Be concise."
                + gradeLevelPhrase(grade)
                + "\n\nReturn the answer as an HTML fragment. Prose in <p>; steps in <ol><li>;"
                + " emphasis with <strong>. Write EVERY formula, fraction, equation and"
                + " numeric expression as MathML inside <math>...</math> (e.g."
                + " <math><mfrac><mn>20</mn><mn>160</mn></mfrac></math>). No Markdown, no LaTeX,"
                + " no $ delimiters, no <script>/<style>/<img>, no surrounding <html> or"
                + " <body> tags — just the fragment.";

        try {
            String text = claude.complete(prompt, images);
            return payload(safePid, text, false);
        } catch (Exception e) {
            System.err.println("AiService: " + e);
            return payload(safePid, "Couldn't reach the AI service: " + e.getMessage(), true);
        }
    }

    /** Reads each image file and base64-encodes it for the vision content block; unreadable files are skipped (logged), not fatal. */
    private static List<ClaudeClient.ImageAttachment> loadImages(List<Path> paths) {
        List<ClaudeClient.ImageAttachment> out = new ArrayList<>();
        for (Path p : paths) {
            try {
                byte[] bytes = Files.readAllBytes(p);
                out.add(new ClaudeClient.ImageAttachment(mediaTypeFor(p), Base64.getEncoder().encodeToString(bytes)));
            } catch (IOException e) {
                System.err.println("AiService: failed to read image " + p + ": " + e);
            }
        }
        return out;
    }

    private static String mediaTypeFor(Path p) {
        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (name.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/gif"; // the legacy corpus is almost entirely .gif
    }

    /**
     * Deduces a short topic name for a chapter/set from a sample of its
     * own problems — the legacy export carries no chapter-title field
     * anywhere (checked tutor_data.js / inmh_list.json: only a
     * book-level title), so this is inference from real content, not a
     * lookup. Returns {chapterLabel, name, placeholder}; name is "" when
     * unavailable (no key, no sample problems, or an API error) so the
     * caller can fall back to the numeric label alone.
     */
    public String getChapterName(String subjectId, String chapterLabel, List<String> samplePids) {
        String safeLabel = chapterLabel == null ? "" : chapterLabel;

        if (!claude.isConfigured()) {
            return chapterPayload(safeLabel, "", true);
        }

        List<String> texts = new ArrayList<>();
        for (String pid : samplePids) {
            store.problemTextFor(pid).ifPresent(texts::add);
            if (texts.size() >= 3) {
                break;
            }
        }
        if (texts.isEmpty()) {
            return chapterPayload(safeLabel, "", true);
        }

        String prompt = "Here are sample problems from \"" + safeLabel + "\" of a \"" + subjectId
                + "\" math course:\n\n"
                + String.join("\n---\n", texts)
                + "\n\nBased on their content, give a short, specific topic name for this chapter"
                + " (2-5 words, e.g. \"Linear Equations\" or \"Quadratic Functions\"). Respond with"
                + " ONLY the topic name — no punctuation, quotes, or explanation.";

        try {
            String name = claude.complete(prompt).trim().replaceAll("^[\"'.]+|[\"'.]+$", "");
            return chapterPayload(safeLabel, name, false);
        } catch (Exception e) {
            System.err.println("AiService.getChapterName: " + e);
            return chapterPayload(safeLabel, "", true);
        }
    }

    private static String chapterPayload(String chapterLabel, String name, boolean placeholder) {
        return "{"
                + "\"chapterLabel\":" + jsonString(chapterLabel) + ","
                + "\"name\":" + jsonString(name) + ","
                + "\"placeholder\":" + placeholder
                + "}";
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
