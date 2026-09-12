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

    /**
     * "Ask AI about my work" — the student's whiteboard scratch work,
     * captured as a PNG on the client, sent here as base64. Reviews it
     * against the problem and gives qualitative feedback on whether it
     * shows real understanding — not a correct/incorrect verdict, no
     * grade. Same degrade-gracefully posture as getAIForProblem: no key
     * / no image / unknown pid / API error all come back
     * placeholder:true with a readable message.
     *
     * @param imageBase64 PNG bytes, base64-encoded (a leading
     *   "data:image/png;base64," prefix, if present, is stripped).
     */
    public String checkWork(String pid, String imageBase64) {
        String safePid = pid == null ? "" : pid;

        if (!claude.isConfigured()) {
            return workPayload(safePid, "Set ANTHROPIC_API_KEY on the server to enable AI feedback.", true);
        }

        String image = stripDataUrlPrefix(imageBase64 == null ? "" : imageBase64.trim());
        if (image.isEmpty()) {
            return workPayload(safePid, "No whiteboard image was sent.", true);
        }

        String problem = store.problemTextFor(safePid).orElse(null);
        if (problem == null) {
            return workPayload(safePid, "No problem found for id \"" + safePid + "\".", true);
        }

        List<ClaudeClient.ImageAttachment> images = loadImages(store.problemImagesFor(safePid));
        boolean hasProblemImages = !images.isEmpty();
        images.add(new ClaudeClient.ImageAttachment("image/png", image));

        String prompt = "You are a patient math tutor reviewing a student's scratch work on this problem:\n\n"
                + problem
                + (hasProblemImages ? "\n\n(Part of this problem — the equation and/or its answer choices — is"
                        + " shown to you only as image(s), not as text above. Read them carefully.)" : "")
                + "\n\nThe LAST attached image is a photo of the student's handwritten/drawn work on a digital"
                + " whiteboard while solving this problem. FIRST, look carefully and note to yourself what"
                + " marks, numbers, symbols or text are actually visible — don't default to calling it unclear"
                + " or illegible just because it's sparse or handwritten; read it the way you'd read anyone's"
                + " quick scratch work. THEN assess whether what's there shows real understanding of THIS"
                + " problem — not just whether a final answer happens to match, but whether the steps or"
                + " reasoning shown actually make sense for it, or whether it's only a final answer with no"
                + " work shown. Be encouraging but honest: call out what's right, and gently point out anything"
                + " missing, confused, or incorrect. Only say the board is blank or genuinely illegible if,"
                + " after really looking, there's truly nothing legible there — don't hedge on content you can"
                + " actually make out."
                + " Quick freehand digits are easy to misread or transpose (36 vs 63, 15 vs 51, 6 vs 9) — if a"
                + " number you read is close to one of this problem's real answer choices but not an exact"
                + " match, that's more likely a reading slip than the student inventing a number that isn't"
                + " even an option. In that case treat it as the matching choice and say so as a question,"
                + " not a correction — e.g. \"looks like you wrote 63 — did you mean 36?\" — rather than"
                + " asserting the off-by-transposition reading as the student's definite, wrong answer."
                + " Keep it to a few sentences, conversational — no letter grade, no percentage, no pass/fail"
                + " verdict."
                + "\n\nReturn the answer as an HTML fragment. Prose in <p>; write any formula/equation you"
                + " reference as MathML inside <math>...</math>. No Markdown, no LaTeX, no $ delimiters, no"
                + " <script>/<style>/<img>, no surrounding <html> or <body> tags — just the fragment.";

        try {
            String text = claude.complete(prompt, images);
            return workPayload(safePid, text, false);
        } catch (Exception e) {
            System.err.println("AiService.checkWork: " + e);
            return workPayload(safePid, "Couldn't reach the AI service: " + e.getMessage(), true);
        }
    }

    /**
     * "Snap" mode — reads back the handwritten numbers/math on the
     * whiteboard as typed text, e.g. "x = 7", "3/4x + 5 = 2x". Pure
     * transcription, not an assessment: no correctness judgment, no
     * feedback on the work, just "here's what I can make out". Unlike
     * checkWork, a missing/unknown pid doesn't fail the request —
     * problem context only helps disambiguate ambiguous strokes (e.g.
     * knowing fractions are in play), it isn't required to read digits
     * off a page.
     *
     * @param imageBase64 PNG bytes, base64-encoded (a leading
     *   "data:image/png;base64," prefix, if present, is stripped).
     */
    public String readWork(String pid, String imageBase64) {
        String safePid = pid == null ? "" : pid;

        if (!claude.isConfigured()) {
            return readPayload(safePid, "Set ANTHROPIC_API_KEY on the server to enable this.", true);
        }

        String image = stripDataUrlPrefix(imageBase64 == null ? "" : imageBase64.trim());
        if (image.isEmpty()) {
            return readPayload(safePid, "No whiteboard image was sent.", true);
        }

        String problem = store.problemTextFor(safePid).orElse(null);

        String prompt = (problem != null
                ? "A student is using a digital whiteboard while solving this math problem:\n\n" + problem + "\n\n"
                : "")
                + "The attached image is a photo of what's written on their whiteboard."
                + " FIRST, look carefully and note to yourself what marks, numbers, symbols or text are"
                + " actually visible — don't default to calling it unclear or illegible just because it's"
                + " sparse or handwritten; read it the way you'd read anyone's quick scratch work."
                + " THEN transcribe it as typed text: convert handwritten digits and math symbols into their"
                + " typed equivalents (e.g. a handwritten \"7\" becomes \"7\", a fraction becomes \"3/4\","
                + " an equation becomes \"x = 7\"). Transcribe ONLY what's actually there — do not solve the"
                + " problem, do not correct or complete their work, do not add anything they didn't write."
                + " If, after really looking, there's truly nothing legible on the board, say so plainly in"
                + " one short sentence instead of guessing."
                + "\n\nReply with ONLY the transcription (or that one-sentence note if nothing's legible) —"
                + " plain text, no HTML, no Markdown, no commentary before or after it.";

        try {
            String text = claude.complete(prompt, List.of(new ClaudeClient.ImageAttachment("image/png", image)));
            return readPayload(safePid, text.strip(), false);
        } catch (Exception e) {
            System.err.println("AiService.readWork: " + e);
            return readPayload(safePid, "Couldn't reach the AI service: " + e.getMessage(), true);
        }
    }

    private static String readPayload(String pid, String transcription, boolean placeholder) {
        return "{"
                + "\"pid\":" + jsonString(pid) + ","
                + "\"transcription\":" + jsonString(transcription) + ","
                + "\"placeholder\":" + placeholder
                + "}";
    }

    private static String stripDataUrlPrefix(String s) {
        if (s.startsWith("data:")) {
            int comma = s.indexOf(',');
            if (comma >= 0) {
                return s.substring(comma + 1);
            }
        }
        return s;
    }

    private static String workPayload(String pid, String feedback, boolean placeholder) {
        return "{"
                + "\"pid\":" + jsonString(pid) + ","
                + "\"feedback\":" + jsonString(feedback) + ","
                + "\"placeholder\":" + placeholder
                + "}";
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
