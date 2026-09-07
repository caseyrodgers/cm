package com.catchupmath.cmre.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * apps/editor's CRUD API, mounted at /api/editor/. Separate from
 * {@link ApiHandler} (the tutor's read-only API) because it needs
 * PUT/POST and is gated: {@link TutorServer} mounts it only when
 * CM_EDITOR is set, so the public serving deployment never exposes
 * write endpoints.
 *
 * <pre>
 *   GET  /api/editor/subjects                      -> ["alg1ptests", ...]
 *   GET  /api/editor/solutions?subject=&lt;id&gt;        -> [{pid, identification, statementPreview, ...}]
 *   GET  /api/editor/solutions/&lt;pid&gt;               -> full Solution
 *   PUT  /api/editor/solutions/&lt;pid&gt;               -> body: Solution JSON; stores it, echoes it back
 *   POST /api/editor/modules/&lt;subjectId&gt;/publish   -> recompute manifest + copy to the served web root
 * </pre>
 */
public final class EditorHandler implements HttpHandler {

    private final SolutionSource source;

    public EditorHandler(SolutionSource source) {
        this.source = source;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod().toUpperCase();

            if (path.equals("/api/editor/subjects") && method.equals("GET")) {
                send(ex, 200, jsonStringArray(source.subjects()));
                return;
            }

            if (path.equals("/api/editor/solutions") && method.equals("GET")) {
                String subject = ApiHandler.queryParam(ex.getRequestURI().getRawQuery(), "subject");
                if (subject.isBlank()) {
                    send(ex, 400, err("missing ?subject="));
                    return;
                }
                send(ex, 200, source.listSolutions(subject));
                return;
            }

            String solPrefix = "/api/editor/solutions/";
            if (path.startsWith(solPrefix)) {
                String pid = URLDecoder.decode(path.substring(solPrefix.length()), StandardCharsets.UTF_8);
                if (pid.isBlank()) {
                    send(ex, 400, err("missing pid"));
                    return;
                }
                if (method.equals("GET")) {
                    String doc = source.getSolution(pid);
                    if (doc == null) {
                        send(ex, 404, err("unknown pid: " + pid));
                        return;
                    }
                    send(ex, 200, doc);
                    return;
                }
                if (method.equals("PUT")) {
                    String stored = source.saveSolution(pid, readBody(ex));
                    if (stored == null) {
                        send(ex, 404, err("unknown pid: " + pid));
                        return;
                    }
                    send(ex, 200, stored);
                    return;
                }
                send(ex, 405, err("method not allowed"));
                return;
            }

            String pubPrefix = "/api/editor/modules/";
            String pubSuffix = "/publish";
            if (path.startsWith(pubPrefix) && path.endsWith(pubSuffix) && method.equals("POST")) {
                String subjectId = URLDecoder.decode(
                        path.substring(pubPrefix.length(), path.length() - pubSuffix.length()),
                        StandardCharsets.UTF_8);
                if (subjectId.isBlank()) {
                    send(ex, 400, err("missing subjectId"));
                    return;
                }
                send(ex, 200, source.publish(subjectId));
                return;
            }

            send(ex, 404, err("not found"));
        } catch (IllegalArgumentException e) {
            send(ex, 400, err(String.valueOf(e.getMessage())));
        } catch (RuntimeException | IOException e) {
            send(ex, 500, err(String.valueOf(e.getMessage())));
        }
    }

    private static String readBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String jsonStringArray(List<String> items) {
        StringBuilder b = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                b.append(',');
            }
            b.append(AiService.jsonString(items.get(i)));
        }
        return b.append(']').toString();
    }

    private static String err(String msg) {
        return "{\"error\":" + AiService.jsonString(msg == null ? "" : msg) + "}";
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        ApiHandler.send(ex, status, "application/json", body);
    }
}
