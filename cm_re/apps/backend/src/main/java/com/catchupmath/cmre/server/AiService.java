package com.catchupmath.cmre.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
 *
 * Prompt templates live as plain text files under {@code promptsDir}
 * (cm_re/prompts/), NOT as Java string literals — IDEAS.org, Casey,
 * 2026-09-19: "AI Context should be stored in a text file and read
 * fresh everytime it is used." Every prompt on this class had already
 * been iterated on live, in production, multiple times this whole
 * project (the buildSkeleton "no words/no restatement" saga, checkWork's
 * "circled option" fabrication fix, readWork's context-removal fix,
 * getAIForProblem's answer-leakage fix) — each of those was a Java edit
 * + recompile + redeploy cycle for what's really just wording. {@link
 * #loadPrompt} re-reads the file on every single call, no caching, so
 * an operator can edit a prompt file on the running server (dev or
 * deploy/) and the very next request picks it up — no rebuild, no
 * restart. {@link #fill} is deliberately not a real templating engine
 * (literal {@code {{TOKEN}}} string substitution, in the same spirit as
 * this codebase's other "no eval" choices, e.g. the whiteboard
 * calculator) — these are short, fixed, developer-authored templates,
 * not untrusted input.
 */
public final class AiService {

    private final SolutionStore store;
    private final ClaudeClient claude;
    private final Path promptsDir;

    public AiService(SolutionStore store, Path promptsDir) {
        this.store = store;
        this.claude = new ClaudeClient();
        this.promptsDir = promptsDir;
    }

    /** Re-reads the named prompt file fresh on every call — see the class doc. Package-visible for AiServiceTest. */
    String loadPrompt(String filename) {
        Path p = promptsDir.resolve(filename);
        try {
            return Files.readString(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("missing/unreadable prompt file: " + p, e);
        }
    }

    /** Literal {{TOKEN}} -> value substitution, applied in order. Not a templating engine — see the class doc. Package-visible for AiServiceTest. */
    static String fill(String template, String... tokenValuePairs) {
        String out = template;
        for (int i = 0; i + 1 < tokenValuePairs.length; i += 2) {
            out = out.replace(tokenValuePairs[i], tokenValuePairs[i + 1]);
        }
        return out;
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

        String prompt;
        try {
            String imagesNote = images.isEmpty() ? "" : "\n\n" + loadPrompt("images-note.txt");
            prompt = fill(loadPrompt("learn.txt"),
                    "{{PROBLEM}}", problem,
                    "{{IMAGES_NOTE}}", imagesNote,
                    "{{GRADE_PHRASE}}", gradeLevelPhrase(grade));
        } catch (UncheckedIOException e) {
            return payload(safePid, e.getMessage(), true);
        }

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
     * A follow-up question about a "Learn" explanation the student
     * already got — "what is the form of the function?" etc. Reuses the
     * same problem context (+ images, when the problem's content is
     * pictured rather than typed) plus the prior explanation text as
     * context, so the model isn't re-deriving from scratch or drifting
     * off-topic. Same non-disclosure posture as getAIForProblem: never
     * states the original problem's final numeric result or names the
     * correct multiple-choice option — everything else is fair to
     * answer in full, which covers a genuinely conceptual question like
     * the example above.
     *
     * @param priorAnswer the HTML explanation previously returned by
     *   getAIForProblem for this pid — supplies context, not re-sent
     *   verbatim in the reply.
     * @param question the student's follow-up, plain text.
     */
    public String followUp(String pid, String grade, String priorAnswer, String question) {
        String safePid = pid == null ? "" : pid;

        if (!claude.isConfigured()) {
            return payload(safePid, "Set ANTHROPIC_API_KEY on the server to enable AI explanations.", true);
        }

        String problem = store.problemTextFor(safePid).orElse(null);
        if (problem == null) {
            return payload(safePid, "No problem found for id \"" + safePid + "\".", true);
        }

        if (question == null || question.isBlank()) {
            return payload(safePid, "No follow-up question was sent.", true);
        }

        List<ClaudeClient.ImageAttachment> images = loadImages(store.problemImagesFor(safePid));

        String prompt;
        try {
            String imagesNote = images.isEmpty() ? "" : "\n\n" + loadPrompt("images-note.txt");
            prompt = fill(loadPrompt("follow-up.txt"),
                    "{{PROBLEM}}", problem,
                    "{{IMAGES_NOTE}}", imagesNote,
                    "{{PRIOR_ANSWER}}", priorAnswer == null ? "" : priorAnswer,
                    "{{QUESTION}}", question == null ? "" : question,
                    "{{GRADE_PHRASE}}", gradeLevelPhrase(grade));
        } catch (UncheckedIOException e) {
            return payload(safePid, e.getMessage(), true);
        }

        try {
            AiLog.logRequest("followUp", safePid, prompt, images);
            String text = claude.complete(prompt, images);
            return payload(safePid, text, false);
        } catch (Exception e) {
            System.err.println("AiService.followUp: " + e);
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
        String imagesNote = hasProblemImages ? "\n\n(Part of this problem — the equation and/or its answer choices — is"
                + " shown to you only as image(s), not as text above. Read them carefully.)" : "";

        String prompt;
        try {
            prompt = fill(loadPrompt("check-work.txt"),
                    "{{PROBLEM}}", problem,
                    "{{IMAGES_NOTE}}", imagesNote);
        } catch (UncheckedIOException e) {
            return workPayload(safePid, e.getMessage(), true);
        }

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

        String prompt;
        try {
            prompt = loadPrompt("read-work.txt");
        } catch (UncheckedIOException e) {
            return readPayload(safePid, e.getMessage(), true);
        }

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
     *
     * Some legacy problems author their figure as an embedded image
     * rather than text (same corpus quirk getAIForProblem/checkWork
     * already work around — see loadImages) — a triangle, a graph, a
     * diagram of a shape. Case (a) explicitly asks for "a redrawn,
     * larger, clearer" version of exactly that kind of figure, so this
     * now attaches those images (vision), and the prompt tells the
     * model to trace what it actually sees rather than guess a generic
     * shape from the text alone.
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

        List<ClaudeClient.ImageAttachment> images = loadImages(store.problemImagesFor(safePid));
        boolean hasProblemImages = !images.isEmpty();
        String imagesNoteTop = hasProblemImages ? "\n\n(Part of this problem — a figure, graph, or diagram — is shown to"
                + " you only as the attached image(s), not as text above. Look at the image(s)"
                + " carefully: they are the actual figure, not decoration.)" : "";
        String imagesNoteCaseA = hasProblemImages ? " — if the figure is one of the attached images, this case applies:"
                + " trace its actual outline (the real shape and proportions you see — a triangle's"
                + " actual vertices, a circle's actual size relative to what's inside it, an"
                + " irregular polygon's actual number of sides) rather than substituting a generic"
                + " or default shape" : "";

        String prompt;
        try {
            prompt = fill(loadPrompt("sketch.txt"),
                    "{{PROBLEM}}", problem,
                    "{{IMAGES_NOTE_TOP}}", imagesNoteTop,
                    "{{IMAGES_NOTE_CASE_A}}", imagesNoteCaseA);
        } catch (UncheckedIOException e) {
            return skeletonPayload(safePid, new JsonArray(), e.getMessage(), true);
        }

        try {
            AiLog.logRequest("buildSkeleton", safePid, prompt, images);
            // Low temperature: this is closer to constrained classification
            // ("is there real structure here, and if so which kind") than
            // open-ended writing, and the default temperature was visibly
            // inconsistent about following the no-restatement/no-words
            // rules above — sometimes fine, sometimes reaching for an
            // invented "Left side / Right side" restatement anyway.
            String text = claude.complete(prompt, images, 0.2);
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
                out.add(toAttachment(bytes, mediaTypeFor(p)));
            } catch (IOException e) {
                System.err.println("AiService: failed to read image " + p + ": " + e);
            }
        }
        return out;
    }

    // Below this, the model reliably misreads a tiny symbol as a different, more
    // "plausible" one instead of admitting uncertainty — found live 2026-09-20 on a
    // real 51x45px absolute-value expression the model kept inventing a different
    // structure for. The legacy corpus' WIRIS-rendered inline equation snippets are
    // routinely 25-150px (sampled across real solutions); actual figures/graphs are
    // routinely 300x300+ and read fine already, so this only touches the small ones.
    private static final int MIN_IMAGE_DIMENSION = 200;
    private static final int MAX_UPSCALE_FACTOR = 8;

    /**
     * Upscales a genuinely tiny source image before it's attached for vision —
     * bicubic, not nearest-neighbor, so anti-aliased edges stay smooth rather than
     * blocky. Re-encoded as PNG regardless of source format: a GIF's fixed indexed
     * palette isn't a good target for smooth interpolation, and PNG is lossless.
     * Images already at or above {@link #MIN_IMAGE_DIMENSION} on their longer side,
     * or that fail to decode as a raster image (shouldn't happen for this corpus,
     * but not worth failing the whole request over), are sent through unchanged.
     */
    static ClaudeClient.ImageAttachment toAttachment(byte[] bytes, String mediaType) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        if (img == null) {
            return new ClaudeClient.ImageAttachment(mediaType, Base64.getEncoder().encodeToString(bytes));
        }
        int w = img.getWidth();
        int h = img.getHeight();
        int maxDim = Math.max(w, h);
        if (maxDim <= 0 || maxDim >= MIN_IMAGE_DIMENSION) {
            return new ClaudeClient.ImageAttachment(mediaType, Base64.getEncoder().encodeToString(bytes));
        }

        // Flatten onto opaque white, WITH a white margin padded around the
        // source content, before scaling — found live (2026-09-20, same
        // absolute-value pid, two rounds of debugging): a source this
        // small often has real ink (an absolute-value bar, in this case)
        // running the full height of the image at column 0 / column w-1,
        // i.e. literally touching the edge. Java2D's bicubic interpolation
        // needs source samples beyond the image bounds for pixels near an
        // edge; with no real "beyond the edge" data it ends up smearing
        // that edge-touching black column across the whole top/bottom
        // border on scale-up — a plain "|x|" became a misleading solid
        // black picture-frame, which made the model read a boxed "complex
        // fraction" instead of an absolute-value expression. Padding with
        // real white margin BEFORE scaling gives the kernel real
        // background to sample near every edge, so there's nothing to
        // smear. (Flattening onto opaque white first, rather than keeping
        // alpha, isn't itself what fixes the frame — confirmed by testing
        // — but it's still correct: these are equation snippets meant to
        // sit on a white page, and it sidesteps a separate, real class of
        // alpha-interpolation halo bugs.)
        int pad = Math.max(4, maxDim / 8);
        int pw = w + pad * 2;
        int ph = h + pad * 2;
        BufferedImage flattened = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_RGB);
        Graphics2D fg = flattened.createGraphics();
        fg.setColor(java.awt.Color.WHITE);
        fg.fillRect(0, 0, pw, ph);
        fg.drawImage(img, pad, pad, null);
        fg.dispose();

        int scale = Math.min(MAX_UPSCALE_FACTOR, (int) Math.ceil((double) MIN_IMAGE_DIMENSION / maxDim));
        BufferedImage scaled = new BufferedImage(pw * scale, ph * scale, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(flattened, 0, 0, pw * scale, ph * scale, null);
        g.dispose();

        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(scaled, "png", png);
        return new ClaudeClient.ImageAttachment("image/png", Base64.getEncoder().encodeToString(png.toByteArray()));
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
