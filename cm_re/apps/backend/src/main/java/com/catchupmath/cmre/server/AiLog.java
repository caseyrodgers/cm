package com.catchupmath.cmre.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;

/**
 * Appends every prompt actually sent to Claude to a local log file —
 * the exact context submitted to the AI, for debugging/auditing
 * without re-deriving it from scratch. (This session leaned on ad-hoc
 * curl replays and screenshot-cropping more than once, purely because
 * there was no record of the real prompt behind a given response —
 * this is that record.)
 *
 * Deliberately never logs raw image bytes — a whiteboard PNG's base64
 * would bloat the file fast and isn't useful as text — just the
 * prompt, which feature/pid it was for, and how many images (with
 * media types) rode along.
 *
 * File: logs/ai-requests.log, relative to wherever the server process
 * runs (apps/backend when run via `mvn exec:java`; next to the fat
 * jar's working directory when run via `make serve`/deploy). Append-
 * only, one record per request, human-readable — grep-able, not meant
 * to be machine-parsed. Logged only on an actual send, not on
 * early-return placeholder paths (no key, no problem found, etc.) —
 * those never reach Claude at all.
 */
public final class AiLog {
    private static final Path LOG_FILE = Paths.get("logs", "ai-requests.log");

    private AiLog() {
    }

    /** @param images the images sent alongside the prompt, if any — pass null or an empty list for a text-only request. */
    public static synchronized void logRequest(String feature, String pid, String prompt, List<ClaudeClient.ImageAttachment> images) {
        try {
            Path dir = LOG_FILE.toAbsolutePath().getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
            StringBuilder header = new StringBuilder();
            header.append("==== ").append(Instant.now()).append(" | ").append(feature);
            if (pid != null && !pid.isBlank()) {
                header.append(" | pid=").append(pid);
            }
            int imageCount = images == null ? 0 : images.size();
            header.append(" | images=").append(imageCount);
            if (imageCount > 0) {
                header.append(" (");
                for (int i = 0; i < images.size(); i++) {
                    if (i > 0) {
                        header.append(", ");
                    }
                    header.append(images.get(i).mediaType());
                }
                header.append(")");
            }
            header.append(" ====\n");

            Files.writeString(LOG_FILE, header.toString() + prompt + "\n\n",
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Logging is best-effort — never let a logging failure break the actual AI feature.
            System.err.println("AiLog: failed to write " + LOG_FILE + ": " + e);
        }
    }
}
