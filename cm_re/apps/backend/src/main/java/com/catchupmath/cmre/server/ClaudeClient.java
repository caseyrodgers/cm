package com.catchupmath.cmre.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Smallest usable call to the Claude Messages API — one user message,
 * plain text back. No SDK; java.net.http + Gson (already a dependency).
 *
 * Config from the environment (never hard-coded — this repo is public):
 *   ANTHROPIC_API_KEY   required
 *   ANTHROPIC_MODEL     optional, default claude-haiku-4-5-20251001
 */
public class ClaudeClient {

    private static final String ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final String DEFAULT_MODEL = "claude-haiku-4-5-20251001";
    private static final int MAX_TOKENS = 1024;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public boolean isConfigured() {
        String k = System.getenv("ANTHROPIC_API_KEY");
        return k != null && !k.isBlank();
    }

    /** Sends {@code prompt} as a single user turn; returns the assistant's text. */
    public String complete(String prompt) throws Exception {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("ANTHROPIC_API_KEY is not set");
        }
        String model = System.getenv().getOrDefault("ANTHROPIC_MODEL", DEFAULT_MODEL);

        JsonObject msg = new JsonObject();
        msg.addProperty("role", "user");
        msg.addProperty("content", prompt);
        JsonArray messages = new JsonArray();
        messages.add(msg);

        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("max_tokens", MAX_TOKENS);
        body.add("messages", messages);

        HttpRequest req = HttpRequest.newBuilder(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(60))
                .header("content-type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2) {
            String snippet = res.body() == null ? "" : res.body().substring(0, Math.min(500, res.body().length()));
            throw new RuntimeException("Anthropic API " + res.statusCode() + ": " + snippet);
        }

        JsonObject root = JsonParser.parseString(res.body()).getAsJsonObject();
        for (var block : root.getAsJsonArray("content")) {
            JsonObject o = block.getAsJsonObject();
            if ("text".equals(o.get("type").getAsString())) {
                return o.get("text").getAsString();
            }
        }
        return "";
    }
}
