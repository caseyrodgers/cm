package com.catchupmath.cmre.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for a real bug (found 2026-09-18): {@link SolutionSource#publish}
 * built a brand-new manifest.json from scratch on every editor publish, silently
 * dropping the AI-inferred {@code chapters} names and the {@code scorableCount}
 * that ModuleAssembler originally baked in — a live subject published once through
 * the editor came back with every chapter renamed to a bare "Chapter N" fallback.
 */
class SolutionSourceTest {

    private static final String BUNDLE = "{\"solutions\":["
            + "{\"pid\":\"s1\",\"question\":{\"correctIndex\":0}},"
            + "{\"pid\":\"s2\",\"question\":{\"correctIndex\":1}},"
            + "{\"pid\":\"s3\"}"
            + "]}";

    private static final String CHAPTERS = "[{\"key\":\"ch-1\",\"label\":\"Chapter 1\","
            + "\"name\":\"Linear Equations\",\"rank\":1}]";

    @Test
    void publishPreservesExistingChaptersAndRecountsScorable(@TempDir Path tmp) throws IOException {
        Path subjectDir = tmp.resolve("alg1ptests");
        Files.createDirectories(subjectDir);
        Files.writeString(subjectDir.resolve("bundle.json"), BUNDLE, StandardCharsets.UTF_8);
        // Simulate a manifest already on disk from the original ModuleAssembler run —
        // carrying chapters + a (now stale) scorableCount, same as a real deployed module.
        Files.writeString(subjectDir.resolve("manifest.json"),
                "{\"subjectId\":\"alg1ptests\",\"version\":\"3\",\"solutionIds\":[\"s1\",\"s2\",\"s3\"],"
                        + "\"approxSizeBytes\":123,\"scorableCount\":99,\"chapters\":" + CHAPTERS + "}",
                StandardCharsets.UTF_8);

        SolutionSource source = new SolutionSource(tmp, null);
        String manifestJson = source.publish("alg1ptests");
        JsonObject manifest = JsonParser.parseString(manifestJson).getAsJsonObject();

        assertTrue(manifest.has("chapters"), "publish must not drop the chapters array");
        assertEquals("Linear Equations", manifest.getAsJsonArray("chapters").get(0).getAsJsonObject()
                .get("name").getAsString(), "publish must carry forward the real chapter name");
        assertEquals(2, manifest.get("scorableCount").getAsInt(),
                "scorableCount must be recounted from the current bundle (2 MC questions), not left stale/dropped");

        JsonObject onDisk = JsonParser.parseString(
                Files.readString(subjectDir.resolve("manifest.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        assertTrue(onDisk.has("chapters"), "the written manifest.json must also carry chapters forward");
    }

    @Test
    void publishOmitsChaptersWhenNoneExistedYet(@TempDir Path tmp) throws IOException {
        // A subject that was never chapter-named (e.g. ChapterNamer never ran, or a
        // fresh createSolution-only bundle with no prior manifest at all) shouldn't
        // get a fabricated empty/garbage chapters field.
        Path subjectDir = tmp.resolve("freshsubject");
        Files.createDirectories(subjectDir);
        Files.writeString(subjectDir.resolve("bundle.json"),
                "{\"solutions\":[{\"pid\":\"a1\"}]}", StandardCharsets.UTF_8);

        SolutionSource source = new SolutionSource(tmp, null);
        String manifestJson = source.publish("freshsubject");
        JsonObject manifest = JsonParser.parseString(manifestJson).getAsJsonObject();

        assertFalse(manifest.has("chapters"), "no prior manifest -> no chapters field to fabricate");
        assertEquals(0, manifest.get("scorableCount").getAsInt());
    }
}
