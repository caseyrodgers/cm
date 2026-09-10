package com.catchupmath.cmre.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Dev-only endpoints under /api/dev/. Mounted by {@link TutorServer}
 * ONLY when the env var CM_DEV is set — `make run` / `make api` /
 * `make debug` set it; production (the deploy fat jar via run.sh) does
 * not, so /api/dev/* simply isn't there.
 *
 *   POST /api/dev/shutdown  -> 200, then this JVM exits (so a dev can
 *                              stop just this server without a
 *                              `taskkill /IM java.exe` that also kills
 *                              their editor/tools). See `make kill-server`.
 */
final class DevHandler implements HttpHandler {

    static boolean enabled() {
        String v = System.getenv("CM_DEV");
        return v != null && !v.isBlank();
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();

        if (path.equals("/api/dev/shutdown") && "POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 200, "{\"ok\":true,\"shuttingDown\":true}");
            System.out.println("shutdown requested via POST /api/dev/shutdown");
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(150); // let the response flush
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                System.exit(0);
            }, "dev-shutdown");
            t.setDaemon(true);
            t.start();
            return;
        }

        send(ex, 404, "not found");
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        if (body.startsWith("{")) {
            ex.getResponseHeaders().set("content-type", "application/json");
        }
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
