package com.catchupmath.cmre.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.concurrent.Executors;

/**
 * Dependency-free dev server for cm_re. Serves the built tutor
 * (apps/tutor/dist) as static files and exposes the REST API under
 * /api/ — both on one origin so the app's relative fetches and the
 * PWA service worker work with no CORS.
 *
 * Usage: TutorServer [httpPort] [webRoot] [keystorePath] [keystorePass]
 *   httpPort     default 5173
 *   webRoot      the built tutor. If omitted (or the given path doesn't
 *                exist) it's found by walking up from the working
 *                directory for apps/tutor/dist — so it works whether
 *                launched from cm_re/, cm_re/apps/backend/ (Eclipse), or
 *                anywhere in between.
 *   keystore*    optional PKCS12 — if given, HTTPS is also served on
 *                httpPort+1. Env CM_KEYSTORE / CM_KEYSTORE_PASS work too.
 *
 * `make run` in cm_re/ builds the tutor then starts this instead of
 * python -m http.server.
 */
public final class TutorServer {

    public static void main(String[] args) throws Exception {
        int httpPort = args.length > 0 ? Integer.parseInt(args[0]) : 5173;
        Path webRoot = resolveWebRoot(args.length > 1 ? args[1] : null);

        String keystorePath = args.length > 2 ? args[2] : System.getenv("CM_KEYSTORE");
        String keystorePass = args.length > 3 ? args[3] : System.getenv("CM_KEYSTORE_PASS");

        if (webRoot == null) {
            System.err.println("web root (apps/tutor/dist) not found. CWD=" + Path.of("").toAbsolutePath()
                    + "\n  build it first: (cd cm_re && make build)"
                    + "\n  or pass an explicit path as program arg 2");
            System.exit(1);
            return;
        }

        var apiHandler = new ApiHandler(new AiService(new SolutionStore(webRoot)));
        var staticHandler = new StaticHandler(webRoot);
        var executor = Executors.newFixedThreadPool(8);

        HttpServer http = HttpServer.create(new InetSocketAddress(httpPort), 0);
        http.createContext("/api/", apiHandler);
        http.createContext("/", staticHandler);
        http.setExecutor(executor);
        http.start();
        System.out.println("cm_re server");
        System.out.println("  http    http://localhost:" + httpPort);
        System.out.println("  webroot " + webRoot);
        System.out.println("  api     GET /api/health , GET /api/ai/problem/{pid}");
        System.out.println("  ai      " + (System.getenv("ANTHROPIC_API_KEY") == null || System.getenv("ANTHROPIC_API_KEY").isBlank()
                ? "ANTHROPIC_API_KEY not set — Learn returns a placeholder"
                : "live (model: " + System.getenv().getOrDefault("ANTHROPIC_MODEL", "claude-haiku-4-5-20251001") + ")"));

        if (keystorePath != null && !keystorePath.isBlank()) {
            int httpsPort = httpPort + 1;
            HttpsServer https = HttpsServer.create(new InetSocketAddress(httpsPort), 0);
            https.setHttpsConfigurator(new HttpsConfigurator(sslContext(keystorePath, keystorePass)));
            https.createContext("/api/", apiHandler);
            https.createContext("/", staticHandler);
            https.setExecutor(executor);
            https.start();
            System.out.println("  https   https://localhost:" + httpsPort + "  (keystore: " + keystorePath + ")");
        } else {
            System.out.println("  https   off (set CM_KEYSTORE / CM_KEYSTORE_PASS to a PKCS12 to enable)");
        }

        System.out.println("Ctrl+C to stop.");
        Thread.currentThread().join();
    }

    /**
     * Uses the given path if it's a real directory; otherwise walks up
     * from the working directory looking for apps/tutor/dist (or
     * cm_re/apps/tutor/dist). Returns null if nothing is found.
     */
    private static Path resolveWebRoot(String arg) {
        if (arg != null && !arg.isBlank()) {
            Path p = Path.of(arg).toAbsolutePath().normalize();
            if (Files.isDirectory(p)) {
                return p;
            }
        }
        Path dir = Path.of("").toAbsolutePath();
        for (int i = 0; i < 8 && dir != null; i++, dir = dir.getParent()) {
            for (String rel : new String[] { "apps/tutor/dist", "cm_re/apps/tutor/dist" }) {
                Path cand = dir.resolve(rel).normalize();
                if (Files.isDirectory(cand)) {
                    return cand;
                }
            }
        }
        return null;
    }

    private static SSLContext sslContext(String keystorePath, String pass) throws Exception {
        char[] pw = (pass == null ? "" : pass).toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream in = Files.newInputStream(Path.of(keystorePath))) {
            ks.load(in, pw);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, pw);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return ctx;
    }
}
