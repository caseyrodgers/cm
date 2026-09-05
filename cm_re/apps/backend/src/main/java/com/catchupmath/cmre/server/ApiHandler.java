package com.catchupmath.cmre.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * The REST API, mounted at /api/.
 *
 *   GET /api/health                                  -> {"status":"ok"}
 *   GET /api/ai/problem/{pid}?grade=7                -> AiService.getAIForProblem(pid, grade)
 *   GET /api/ai/chapter-name/{subjectId}?label=&pids= -> AiService.getChapterName(subjectId, label, pids)
 */
public class ApiHandler implements HttpHandler {

    private final AiService ai;

    public ApiHandler(AiService ai) {
        this.ai = ai;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
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
