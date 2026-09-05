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
import java.util.List;

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

    /** A vision attachment for the user turn — base64-encoded, per the Messages API's image content block. mediaType is one of image/jpeg, image/png, image/gif, image/webp. */
    public record ImageAttachment(String mediaType, String base64Data) {}

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public boolean isConfigured() {
        String k = System.getenv("ANTHROPIC_API_KEY");
        return k != null && !k.isBlank();
    }

    /** Sends {@code prompt} as a single user turn with no images; returns the assistant's text. */
    public String complete(String prompt) throws Exception {
        return complete(prompt, null);
    }

    /**
     * Sends {@code prompt} as a single user turn, with any {@code images}
     * attached as vision content blocks ahead of the text (images
     * captures the equation/answer-choice content some legacy problems
     * author as a picture instead of text — see SolutionStore).
     */
    public String complete(String prompt, List<ImageAttachment> images) throws Exception {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("ANTHROPIC_API_KEY is not set");
        }
        String model = System.getenv().getOrDefault("ANTHROPIC_MODEL", DEFAULT_MODEL);

        JsonArray content = new JsonArray();
        if (images != null) {
            for (ImageAttachment img : images) {
                JsonObject source = new JsonObject();
                source.addProperty("type", "base64");
                source.addProperty("media_type", img.mediaType());
                source.addProperty("data", img.base64Data());
                JsonObject block = new JsonObject();
                block.addProperty("type", "image");
                block.add("source", source);
                content.add(block);
            }
        }
        JsonObject textBlock = new JsonObject();
        textBlock.addProperty("type", "text");
        textBlock.addProperty("text", prompt);
        content.add(textBlock);

        JsonObject msg = new JsonObject();
        msg.addProperty("role", "user");
        msg.add("content", content);
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
