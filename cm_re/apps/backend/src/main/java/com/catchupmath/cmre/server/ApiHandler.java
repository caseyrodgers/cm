package com.catchupmath.cmre.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * The REST API, mounted at /api/.
 *
 *   GET /api/health                 -> {"status":"ok"}
 *   GET /api/ai/problem/{pid}       -> AiService.getAIForProblem(pid)
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
                send(ex, 200, "application/json", ai.getAIForProblem(pid));
                return;
            }

            send(ex, 404, "application/json", "{\"error\":\"not found\"}");
        } catch (RuntimeException e) {
            send(ex, 500, "application/json",
                    "{\"error\":" + AiService.jsonString(String.valueOf(e.getMessage())) + "}");
        }
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
