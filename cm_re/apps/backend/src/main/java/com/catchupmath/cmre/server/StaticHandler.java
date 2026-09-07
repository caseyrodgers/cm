package com.catchupmath.cmre.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Serves a built single-page app from a web root. Any request path
 * that doesn't resolve to a real file under the root and has no file
 * extension falls back to index.html, so a hard refresh on any route
 * still loads the app.
 *
 * With a non-empty {@code urlPrefix} the handler serves an app mounted
 * under a sub-path (e.g. "/editor" -> apps/editor/dist): the prefix is
 * stripped before resolving against the root, and index.html is
 * resolved inside the root, not at the server origin. That lets one
 * server host both the tutor at "/" and the editor at "/editor/".
 *
 * Directory traversal is blocked by resolving against the normalised
 * root and rejecting anything that escapes it.
 */
public class StaticHandler implements HttpHandler {

    private static final Map<String, String> TYPES = Map.ofEntries(
            Map.entry("html", "text/html"),
            Map.entry("js", "text/javascript"),
            Map.entry("mjs", "text/javascript"),
            Map.entry("css", "text/css"),
            Map.entry("json", "application/json"),
            Map.entry("webmanifest", "application/manifest+json"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif", "image/gif"),
            Map.entry("ico", "image/x-icon"),
            Map.entry("woff2", "font/woff2"),
            Map.entry("woff", "font/woff"),
            Map.entry("txt", "text/plain"),
            Map.entry("map", "application/json"));

    private final Path root;
    /** "" for an app at the origin; "/editor" for one mounted under /editor/. No trailing slash. */
    private final String urlPrefix;

    public StaticHandler(Path webRoot) {
        this(webRoot, "");
    }

    public StaticHandler(Path webRoot, String urlPrefix) {
        this.root = webRoot.toAbsolutePath().normalize();
        String p = urlPrefix == null ? "" : urlPrefix.trim();
        while (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        this.urlPrefix = p;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod()) && !"HEAD".equalsIgnoreCase(ex.getRequestMethod())) {
            ApiHandler.send(ex, 405, "text/plain", "method not allowed");
            return;
        }

        String urlPath = ex.getRequestURI().getPath();

        if (!urlPrefix.isEmpty()) {
            if (urlPath.equals(urlPrefix) || urlPath.equals(urlPrefix + "/")) {
                urlPath = "/index.html";
            } else if (urlPath.startsWith(urlPrefix + "/")) {
                urlPath = urlPath.substring(urlPrefix.length());
            } else {
                ApiHandler.send(ex, 404, "text/plain", "not found");
                return;
            }
        }

        if (urlPath.equals("/") || urlPath.isEmpty()) {
            urlPath = "/index.html";
        }

        Path target = root.resolve("." + urlPath).normalize();
        if (!target.startsWith(root)) {
            ApiHandler.send(ex, 403, "text/plain", "forbidden");
            return;
        }

        if (!Files.isRegularFile(target)) {
            // SPA fallback: no extension -> serve index.html
            if (!urlPath.substring(urlPath.lastIndexOf('/') + 1).contains(".")) {
                target = root.resolve("index.html");
            }
            if (!Files.isRegularFile(target)) {
                ApiHandler.send(ex, 404, "text/plain", "not found");
                return;
            }
        }

        byte[] body = Files.readAllBytes(target);
        ex.getResponseHeaders().set("Content-Type", contentType(target));
        if ("HEAD".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(200, -1);
            ex.close();
            return;
        }
        ex.sendResponseHeaders(200, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    private static String contentType(Path p) {
        String name = p.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = dot < 0 ? "" : name.substring(dot + 1).toLowerCase();
        return TYPES.getOrDefault(ext, "application/octet-stream") + "; charset=utf-8";
    }
}
