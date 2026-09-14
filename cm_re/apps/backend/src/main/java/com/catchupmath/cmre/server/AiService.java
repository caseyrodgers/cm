package com.catchupmath.cmre.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                + " Walk through the method and the reasoning all the way up to — but not including — the"
                + " final answer: do NOT state the final numeric result, do NOT say which multiple-choice"
                + " option is correct, and do NOT solve the very last arithmetic/simplification step for"
                + " them. Leave that last step for the student to do themselves once they understand the"
                + " approach — this is a teaching explanation, not an answer key."
                + gradeLevelPhrase(grade)
                + "\n\nReturn the answer as an HTML fragment. Prose in <p>; steps in <ol><li>;"
                + " emphasis with <strong>. Write EVERY formula, fraction, equation and"
                + " numeric expression as MathML inside <math>...</math> (e.g."
                + " <math><mfrac><mn>20</mn><mn>160</mn></mfrac></math>). No Markdown, no LaTeX,"
                + " no $ delimiters, no <script>/<style>/<img>, no surrounding <html> or"
                + " <body> tags — just the fragment.";

        try {
            AiLog.logRequest("getAIForProblem", safePid, prompt, images);
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
                + " whiteboard while solving this problem. IMPORTANT: this image shows ONLY the student's own"
                + " ink strokes on a blank background — it does NOT include the problem statement, the answer"
                + " choices, or their layout/position on the page (the whiteboard is a separate overlay with no"
                + " fixed spatial relationship to where choices are rendered). Never claim to see something"
                + " \"circled\" or \"selected\" as corresponding to a particular lettered choice because of"
                + " where it sits in the image — there is no such correspondence to read. The only valid way to"
                + " match the work to a choice is by its actual written content (a number, word, or letter the"
                + " student wrote) compared against the choice list given above — never by position."
                + " FIRST, look carefully and note to yourself what"
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
            AiLog.logRequest("checkWork", safePid, prompt, images);
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
     * feedback on the work, just "here's what I can make out".
     *
     * Deliberately sends NO problem context (unlike checkWork) — this
     * used to include the problem statement on the theory that it
     * "only helps disambiguate ambiguous strokes", but that was found
     * to actively cause fabrication: given a board of genuinely
     * illegible scribbles (an X, some dashes, a hook shape — nothing
     * that read as actual digits) alongside a problem like "if y=2,
     * 15y-4 is?", the model didn't say so — it confidently transcribed
     * a complete, textbook-correct derivation ("y = 2 / 15y - 4 /
     * 15(2) - 4") built entirely from the problem's own numbers, not
     * from anything actually drawn. Random scribbles with no problem
     * context read correctly as illegible; the moment the model has
     * the "expected" numbers sitting right there, it reaches for them
     * instead of admitting uncertainty. Removing the context removes
     * the material there'd be to fabricate from.
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

        String prompt = "The attached image is a photo of handwritten/drawn work on a digital whiteboard."
                + " FIRST, look carefully and note to yourself what marks, numbers, symbols or text are"
                + " actually visible — don't default to calling it unclear or illegible just because it's"
                + " sparse or handwritten; read it the way you'd read anyone's quick scratch work."
                + " THEN transcribe it as typed text: convert handwritten digits and math symbols into their"
                + " typed equivalents (e.g. a handwritten \"7\" becomes \"7\", a fraction becomes \"3/4\","
                + " an equation becomes \"x = 7\")."
                + " Transcribe ONLY the actual shapes you can see on the board. Never guess, complete, or"
                + " invent content based on what a plausible or expected answer might look like — you have"
                + " no information about what problem this work is for, and must not assume any. If several"
                + " marks are grouped like separate lines of work but you can't actually resolve what most of"
                + " them say, transcribe only the specific characters you can genuinely identify and describe"
                + " the rest plainly as illegible marks — do not fill the gaps in with something that merely"
                + " looks like a coherent derivation. If, after really looking, none of it is legible at all,"
                + " say so plainly in one short sentence instead of guessing."
                + "\n\nReply with ONLY the transcription (or that one-sentence note if nothing's legible) —"
                + " plain text, no HTML, no Markdown, no commentary before or after it.";

        try {
            List<ClaudeClient.ImageAttachment> images = List.of(new ClaudeClient.ImageAttachment("image/png", image));
            AiLog.logRequest("readWork", safePid, prompt, images);
            String text = claude.complete(prompt, images);
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

    private static final int MAX_SKELETON_SHAPES = 60;

    /**
     * "Sketch a starting point" — asks Claude for genuinely useful
     * mathematical scaffolding as a short list of basic drawing
     * primitives (lines, polylines, circles, text labels), which the
     * client converts directly into the whiteboard's own native
     * Stroke[] format and appends to the board. No image round-trip:
     * this is a structured-JSON request, not a vision one — cheaper
     * and more reliable than asking for an image, and the result is
     * real, editable ink (same Undo/Clear as anything hand-drawn), not
     * a separate picture layer.
     *
     * Deliberately NOT a restatement of the problem, and deliberately
     * NOT prose. Two corrections layered onto the original version of
     * this prompt, both from Casey watching real output:
     *   1. It used to just copy the equation/expression and answer
     *      choices back as text — useless, since the problem itself is
     *      already visible right below/through the board.
     *   2. Once that was fixed, it started reaching for words instead —
     *      guiding questions ("What is the radius?"), prose labels
     *      ("Base pay per hour:"), bullet-point "things to identify"
     *      lists. Still not what was asked for: "not words... only
     *      mathematical structures... not steps."
     * The prompt now forbids both and asks only for actual diagrams —
     * a redrawn (larger, clearer) geometric figure with given values
     * labeled, coordinate axes or a number line, a blank grid/table
     * structure, a blank symbolic template (blanks and operators only,
     * no words) — and permits an empty response when no real structure
     * applies, rather than forcing prose in to have drawn something.
     * Same "don't reveal the answer" posture as Learn: never solves
     * anything or points at a choice.
     */
    public String buildSkeleton(String pid) {
        String safePid = pid == null ? "" : pid;

        if (!claude.isConfigured()) {
            return skeletonPayload(safePid, new JsonArray(), "Set ANTHROPIC_API_KEY on the server to enable this.", true);
        }

        String problem = store.problemTextFor(safePid).orElse(null);
        if (problem == null) {
            return skeletonPayload(safePid, new JsonArray(), "No problem found for id \"" + safePid + "\".", true);
        }

        String prompt = "Here is a math problem:\n\n" + problem
                + "\n\nDecide whether this problem describes any of exactly these three things:"
                + "\n(a) a geometric figure (a shape, points, a diagram) that could be redrawn larger and"
                + " clearer than a small embedded picture, with its given values labeled on it;"
                + "\n(b) something to graph, or an inequality/interval, where blank coordinate axes or a"
                + " number line would help;"
                + "\n(c) several given values or inputs that would be clearer organized into a blank grid or"
                + " table (cells holding only a given number, or left empty)."
                + "\n\nIf NONE of (a), (b), or (c) genuinely apply — this covers the majority of problems,"
                + " including any problem that is just an equation or expression to solve, simplify, or"
                + " evaluate, with no figure or graph described — output exactly this and stop: []"
                + "\n\nDo not invent a substitute structure for that case. A \"Left side / Right side\" box, a"
                + " balance diagram, a general template using letters like a/b/c standing in for the"
                + " problem's own numbers, or any other container whose real purpose is to redo or restate the"
                + " equation belongs in this empty-array case too — those are not (a), (b), or (c), no matter"
                + " how structured they look."
                + "\n\nExample of what NOT to output, for \"Solve for x: 4/5x + 5 = 2x\": anything like"
                + " [{\"type\":\"text\",\"text\":\"Left side\"},{\"type\":\"text\",\"text\":\"4/5 x\"},"
                + "{\"type\":\"text\",\"text\":\"Right side\"},{\"type\":\"text\",\"text\":\"2x\"}] — the"
                + " correct output for that problem is []."
                + "\n\nIf (a), (b), or (c) genuinely does apply, build ONLY the diagram/axes/table itself —"
                + " never restate the problem's own equation or answer choices as text anywhere, and never use"
                + " words: no prose, no sentences, no guiding questions, no explanatory labels like \"Base"
                + " pay:\". Every \"text\" shape must be SHORT — a bare number, a unit, a single variable"
                + " letter, or a blank placeholder like \"___\" — labeling a given value directly on the"
                + " diagram (e.g. a side length on a triangle) is fine; anything longer is not."
                + "\n\nNever solve anything, never simplify the problem's own expression, and never reveal or"
                + " hint at which multiple-choice option is correct."
                + "\n\nWhen you do output shapes, use ONLY a JSON array (no markdown code fences, no"
                + " commentary before or after) with this exact schema — nothing else:"
                + "\n[{\"type\":\"line\",\"from\":[x,y],\"to\":[x,y]},"
                + " {\"type\":\"polyline\",\"points\":[[x,y],[x,y],...]},"
                + " {\"type\":\"circle\",\"center\":[x,y],\"radius\":r},"
                + " {\"type\":\"text\",\"at\":[x,y],\"text\":\"...\"}]"
                + "\n\nCoordinate space: 480 units wide, up to 1200 tall, origin at top-left, y increases"
                + " downward. Keep the whole thing compact — stay within the top 400 units so it's visible"
                + " without scrolling. For text, write plainly the way you'd write by hand: fractions as"
                + " \"4/5\", exponents as \"x^2\" — no MathML, no LaTeX. Keep it simple, a handful of shapes,"
                + " not an elaborate illustration.";

        try {
            AiLog.logRequest("buildSkeleton", safePid, prompt, null);
            // Low temperature: this is closer to constrained classification
            // ("is there real structure here, and if so which kind") than
            // open-ended writing, and the default temperature was visibly
            // inconsistent about following the no-restatement/no-words
            // rules above — sometimes fine, sometimes reaching for an
            // invented "Left side / Right side" restatement anyway.
            String text = claude.complete(prompt, null, 0.2);
            JsonArray shapes = parseAndValidateShapes(text);
            return skeletonPayload(safePid, shapes, "", false);
        } catch (Exception e) {
            System.err.println("AiService.buildSkeleton: " + e);
            return skeletonPayload(safePid, new JsonArray(), "Couldn't reach the AI service: " + e.getMessage(), true);
        }
    }

    /** Parses the model's response as a JSON array of shapes, tolerating a stray markdown code fence even though the prompt asks for none, and keeps only well-formed entries — an unknown/malformed shape is dropped, not fatal to the whole response. */
    private static JsonArray parseAndValidateShapes(String text) {
        String cleaned = text.strip();
        if (cleaned.startsWith("```")) {
            int firstNewline = cleaned.indexOf('\n');
            int lastFence = cleaned.lastIndexOf("```");
            if (firstNewline >= 0 && lastFence > firstNewline) {
                cleaned = cleaned.substring(firstNewline + 1, lastFence).strip();
            }
        }
        JsonArray out = new JsonArray();
        try {
            JsonElement parsed = JsonParser.parseString(cleaned);
            if (!parsed.isJsonArray()) {
                return out;
            }
            for (JsonElement el : parsed.getAsJsonArray()) {
                if (out.size() >= MAX_SKELETON_SHAPES) {
                    break;
                }
                if (isValidShape(el)) {
                    out.add(el);
                }
            }
        } catch (RuntimeException ignored) {
            // Malformed JSON from the model -> empty shapes; the client treats that as "nothing to draw".
        }
        return out;
    }

    private static boolean isValidShape(JsonElement el) {
        if (!el.isJsonObject()) {
            return false;
        }
        JsonObject o = el.getAsJsonObject();
        if (!o.has("type") || !o.get("type").isJsonPrimitive()) {
            return false;
        }
        switch (o.get("type").getAsString()) {
            case "line":
                return isPoint(o.get("from")) && isPoint(o.get("to"));
            case "polyline":
                return o.has("points") && o.get("points").isJsonArray()
                        && o.getAsJsonArray("points").size() >= 2 && allPoints(o.getAsJsonArray("points"));
            case "circle":
                return isPoint(o.get("center")) && o.has("radius") && o.get("radius").isJsonPrimitive();
            case "text":
                return isPoint(o.get("at")) && o.has("text") && o.get("text").isJsonPrimitive();
            default:
                return false;
        }
    }

    private static boolean isPoint(JsonElement el) {
        return el != null && el.isJsonArray() && el.getAsJsonArray().size() == 2;
    }

    private static boolean allPoints(JsonArray points) {
        for (JsonElement p : points) {
            if (!isPoint(p)) {
                return false;
            }
        }
        return true;
    }

    private static String skeletonPayload(String pid, JsonArray shapes, String message, boolean placeholder) {
        return "{"
                + "\"pid\":" + jsonString(pid) + ","
                + "\"shapes\":" + shapes + ","
                + "\"message\":" + jsonString(message) + ","
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
            AiLog.logRequest("getChapterName", safeLabel, prompt, null);
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
