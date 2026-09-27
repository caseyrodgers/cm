package com.catchupmath.cmre.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IDEAS.org, Casey, 2026-09-19: "AI Context should be stored in a text
 * file and read fresh everytime it is used." Covers the two load-bearing
 * pieces of that: {@link AiService#loadPrompt} re-reads the file from
 * disk on every call (no caching — an edit takes effect on the very next
 * request, no restart), and {@link AiService#fill} does the placeholder
 * substitution correctly.
 *
 * Also covers {@link AiService#toAttachment} — the tiny-image upscale
 * added 2026-09-20 after a real 51x45px absolute-value expression got
 * reliably misread (a different hallucinated structure every call)
 * until it was scaled up before attaching for vision.
 */
class AiServiceTest {

    private AiService service(Path promptsDir) {
        return new AiService(new SolutionStore(promptsDir), promptsDir);
    }

    @Test
    void loadPromptReadsTheFileFreshOnEveryCall(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("greeting.txt");
        Files.writeString(file, "hello v1", StandardCharsets.UTF_8);
        AiService svc = service(tmp);

        assertEquals("hello v1", svc.loadPrompt("greeting.txt"));

        // Editing the file on disk — no cache, no restart — must be
        // visible on the very next call. This is the entire point of
        // the feature: a live prompt tweak takes effect immediately.
        Files.writeString(file, "hello v2", StandardCharsets.UTF_8);
        assertEquals("hello v2", svc.loadPrompt("greeting.txt"));
    }

    @Test
    void loadPromptFailsLoudNotSilentOnAMissingFile(@TempDir Path tmp) {
        AiService svc = service(tmp);
        UncheckedIOException e = assertThrows(UncheckedIOException.class, () -> svc.loadPrompt("nope.txt"));
        assertTrue(e.getMessage().contains("nope.txt"), "error should name the missing file: " + e.getMessage());
    }

    @Test
    void fillSubstitutesEveryToken() {
        String out = AiService.fill(
                "Hi {{NAME}}, your score is {{SCORE}}. Bye {{NAME}}.",
                "{{NAME}}", "Ada",
                "{{SCORE}}", "97");
        assertEquals("Hi Ada, your score is 97. Bye Ada.", out);
    }

    @Test
    void fillLeavesTemplateUntouchedWhenAPlaceholderValueIsEmpty() {
        // The "no images attached" case — {{IMAGES_NOTE}} -> "" — must
        // collapse cleanly, not leave a stray blank marker behind.
        String out = AiService.fill("A{{IMAGES_NOTE}}B", "{{IMAGES_NOTE}}", "");
        assertEquals("AB", out);
    }

    @Test
    void allSixPromptFilesShipAndAreNonEmpty() throws IOException {
        // The real cm_re/prompts/ directory, not a temp fixture — a
        // regression guard against a file getting renamed/deleted
        // without updating the Java side that references it by name.
        Path realPromptsDir = Path.of("../../prompts").toAbsolutePath().normalize();
        assertTrue(Files.isDirectory(realPromptsDir), "expected " + realPromptsDir + " to exist");
        for (String name : new String[] {
                "learn.txt", "follow-up.txt", "check-work.txt", "read-work.txt", "sketch.txt", "chapter-name.txt",
                "images-note.txt"
        }) {
            Path f = realPromptsDir.resolve(name);
            assertTrue(Files.isRegularFile(f), "missing prompt file: " + f);
            assertFalse(Files.readString(f, StandardCharsets.UTF_8).isBlank(), name + " is blank");
        }
    }

    private static byte[] png(int w, int h) throws IOException {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }

    private static BufferedImage decode(String base64) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(java.util.Base64.getDecoder().decode(base64)));
    }

    @Test
    void toAttachmentUpscalesATinyImage() throws IOException {
        // The real reproduction was 51x45 — matches this shape (small, non-square).
        var attachment = AiService.toAttachment(png(51, 45), "image/gif");
        BufferedImage result = decode(attachment.base64Data());
        assertEquals("image/png", attachment.mediaType(), "re-encoded as PNG regardless of the GIF source");
        assertTrue(Math.max(result.getWidth(), result.getHeight()) >= 200,
                "expected the longer side scaled up to at least 200px, got " + result.getWidth() + "x" + result.getHeight());
        // Exact expected size: a white margin is padded around the source
        // before scaling (see the class doc on toAttachment for why — a
        // real bicubic edge-smearing bug found live), so the output isn't
        // simply w*scale x h*scale; it's (w+2*pad)*scale x (h+2*pad)*scale.
        int pad = Math.max(4, 51 / 8);
        int scale = Math.min(8, (int) Math.ceil(200.0 / 51));
        assertEquals((51 + pad * 2) * scale, result.getWidth());
        assertEquals((45 + pad * 2) * scale, result.getHeight());
    }

    @Test
    void toAttachmentLeavesAnAdequateSizedImageByteForByteUnchanged() throws IOException {
        byte[] original = png(400, 400);
        var attachment = AiService.toAttachment(original, "image/gif");
        assertEquals("image/gif", attachment.mediaType(), "not re-encoded — original mediaType preserved");
        assertArrayEquals(original, java.util.Base64.getDecoder().decode(attachment.base64Data()));
    }

    @Test
    void toAttachmentCapsTheUpscaleFactorOnAnExtremelyTinyImage() throws IOException {
        var attachment = AiService.toAttachment(png(1, 1), "image/gif");
        BufferedImage result = decode(attachment.base64Data());
        // MAX_UPSCALE_FACTOR is 8 — a 1x1 source (padded to 9x9) must not
        // balloon anywhere near 200x200.
        int pad = Math.max(4, 1 / 8);
        assertEquals((1 + pad * 2) * 8, result.getWidth());
        assertEquals((1 + pad * 2) * 8, result.getHeight());
    }

    @Test
    void toAttachmentPaddedBorderIsWhiteNotBlack() throws IOException {
        // The actual regression: a source image with real ink running to
        // the very edge (an absolute-value bar at column 0) got its edge
        // color smeared into a solid black frame around the WHOLE scaled
        // image. Build exactly that shape — a 1px-wide black column at
        // x=0, transparent elsewhere — and confirm the padded corner
        // stays white, not black.
        BufferedImage src = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 20; y++) {
            src.setRGB(0, y, 0xff000000); // opaque black, full-height, at the very left edge
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(src, "png", out);

        var attachment = AiService.toAttachment(out.toByteArray(), "image/gif");
        BufferedImage result = decode(attachment.base64Data());
        int corner = result.getRGB(result.getWidth() - 1, 0); // opposite corner from the ink
        assertEquals(0xffffffff, corner, "the far corner (nowhere near the real ink) must stay white, not smear black");
    }

    @Test
    void toAttachmentFallsBackToOriginalBytesWhenNotADecodableImage() throws IOException {
        byte[] garbage = "not actually an image".getBytes(StandardCharsets.UTF_8);
        var attachment = AiService.toAttachment(garbage, "image/gif");
        assertEquals("image/gif", attachment.mediaType());
        assertArrayEquals(garbage, java.util.Base64.getDecoder().decode(attachment.base64Data()));
    }
}
