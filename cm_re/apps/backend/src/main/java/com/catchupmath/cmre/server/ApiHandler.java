package com.catchupmath.cmre.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * The REST API, mounted at /api/.
 *
 *   GET  /api/health                                  -> {"status":"ok"}
 *   GET  /api/ai/problem/{pid}?grade=7                -> AiService.getAIForProblem(pid, grade)
 *   GET  /api/ai/chapter-name/{subjectId}?label=&pids= -> AiService.getChapterName(subjectId, label, pids)
 *   POST /api/ai/check-work/{pid}  {"image":"<base64 png>"} -> AiService.checkWork(pid, image)
 *   POST /api/ai/read-work/{pid}   {"image":"<base64 png>"} -> AiService.readWork(pid, image)
 */
public class ApiHandler implements HttpHandler {

    // Generous but bounded — a whiteboard PNG base64-encoded is typically
    // well under 1 MB; this just stops a runaway/bogus upload from
    // reading forever.
    private static final int MAX_BODY_BYTES = 8 * 1024 * 1024;

    private final AiService ai;

    public ApiHandler(AiService ai) {
        this.ai = ai;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();

            String checkWorkPrefix = "/api/ai/check-work/";
            if (path.startsWith(checkWorkPrefix)) {
                if (!"POST".equalsIgnoreCase(method)) {
                    send(ex, 405, "application/json", "{\"error\":\"method not allowed\"}");
                    return;
                }
                String pid = URLDecoder.decode(path.substring(checkWorkPrefix.length()), StandardCharsets.UTF_8);
                if (pid.isBlank()) {
                    send(ex, 400, "application/json", "{\"error\":\"missing pid\"}");
                    return;
                }
                String image = readImageField(ex);
                send(ex, 200, "application/json", ai.checkWork(pid, image));
                return;
            }

            String readWorkPrefix = "/api/ai/read-work/";
            if (path.startsWith(readWorkPrefix)) {
                if (!"POST".equalsIgnoreCase(method)) {
                    send(ex, 405, "application/json", "{\"error\":\"method not allowed\"}");
                    return;
                }
                String pid = URLDecoder.decode(path.substring(readWorkPrefix.length()), StandardCharsets.UTF_8);
                if (pid.isBlank()) {
                    send(ex, 400, "application/json", "{\"error\":\"missing pid\"}");
                    return;
                }
                String image = readImageField(ex);
                send(ex, 200, "application/json", ai.readWork(pid, image));
                return;
            }

            if (!"GET".equalsIgnoreCase(method)) {
                send(ex, 405, "application/json", "{\"error\":\"method not allowed\"}");
                return;
            }

            if (path.equals("/api/health")) {
                send(ex, 200, "application/json", "{\"status\":\"ok\"}");
                return;
            }

            String prefix = "/api/ai/problem/";
            if (path.startsWith(prefix)) {
                String pid = URLDecoder.decode(path.substring(prefix.length()), StandardCharsets.UTF_8);
                if (pid.isBlank()) {
                    send(ex, 400, "application/json", "{\"error\":\"missing pid\"}");
                    return;
                }
                String grade = queryParam(ex.getRequestURI().getRawQuery(), "grade");
                send(ex, 200, "application/json", ai.getAIForProblem(pid, grade));
                return;
            }

            String chapterPrefix = "/api/ai/chapter-name/";
            if (path.startsWith(chapterPrefix)) {
                String subjectId = URLDecoder.decode(path.substring(chapterPrefix.length()), StandardCharsets.UTF_8);
                String rawQuery = ex.getRequestURI().getRawQuery();
                String label = queryParam(rawQuery, "label");
                String pidsParam = queryParam(rawQuery, "pids");
                List<String> pids = pidsParam.isBlank() ? List.of() : Arrays.asList(pidsParam.split(","));
                if (subjectId.isBlank() || label.isBlank() || pids.isEmpty()) {
                    send(ex, 400, "application/json", "{\"error\":\"missing subjectId, label, or pids\"}");
                    return;
                }
                send(ex, 200, "application/json", ai.getChapterName(subjectId, label, pids));
                return;
            }

            send(ex, 404, "application/json", "{\"error\":\"not found\"}");
        } catch (RuntimeException e) {
            send(ex, 500, "application/json",
                    "{\"error\":" + AiService.jsonString(String.valueOf(e.getMessage())) + "}");
        }
    }

    /** Reads the request body as JSON and returns its "image" string field ("" if absent/blank/oversized/malformed — the caller treats that as "no image"). */
    private static String readImageField(HttpExchange ex) throws IOException {
        byte[] body = readBounded(ex.getRequestBody(), MAX_BODY_BYTES);
        if (body == null) {
            return "";
        }
        try {
            JsonObject obj = JsonParser.parseString(new String(body, StandardCharsets.UTF_8)).getAsJsonObject();
            return obj.has("image") && obj.get("image").isJsonPrimitive() ? obj.get("image").getAsString() : "";
        } catch (RuntimeException e) {
            return "";
        }
    }

    /** Reads at most {@code max} bytes; returns null if the stream has more than that (caller treats as no body). */
    private static byte[] readBounded(InputStream in, int max) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream(Math.min(max, 64 * 1024));
        byte[] chunk = new byte[16 * 1024];
        int n;
        while ((n = in.read(chunk)) != -1) {
            if (buf.size() + n > max) {
                return null;
            }
            buf.write(chunk, 0, n);
        }
        return buf.toByteArray();
    }

    /** Value of `name` from a raw query string ("a=1&grade=7"), URL-decoded, or "" if absent. */
    static String queryParam(String rawQuery, String name) {
        if (rawQuery == null || rawQuery.isEmpty()) {
            return "";
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            String key = eq < 0 ? pair : pair.substring(0, eq);
            if (key.equals(name)) {
                return eq < 0 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    static void send(HttpExchange ex, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
