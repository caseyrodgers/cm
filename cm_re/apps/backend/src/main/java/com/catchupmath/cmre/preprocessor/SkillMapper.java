package com.catchupmath.cmre.preprocessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Maps solution PIDs to fine-grained skill slugs using the static
 * inmh_link.json lookup (exported from the cm database's inmh_link table).
 * Also resolves video entries per skill for the video registry.
 */
public final class SkillMapper {

    private final Map<String, List<String>> pidToSkills;
    private final Map<String, List<VideoRef>> skillToVideos;

    public static class VideoRef {
        public final String key;
        public final String title;
        public final String format;

        VideoRef(String key, String title, String format) {
            this.key = key;
            this.title = title;
            this.format = format;
        }

        public String resolveUrl() {
            if (key.contains("youtube")) return key;
            String fileName = key.endsWith(".flv") ? key.replace(".flv", ".mp4")
                    : key + ".mp4";
            if (isNumeric(key)) return "https://catchupmath.com/help/flvs/tw/" + fileName;
            if (key.startsWith("/") || key.startsWith("http")) return "https://catchupmath.com" + fileName.replace(".mp4", "").replace(".flv", "") + ".mp4";
            return "https://catchupmath.com/help/flvs/mathtv/" + fileName;
        }

        private static boolean isNumeric(String s) {
            try { Integer.parseInt(s); return true; }
            catch (NumberFormatException e) { return false; }
        }
    }

    private SkillMapper(Map<String, List<String>> pidToSkills, Map<String, List<VideoRef>> skillToVideos) {
        this.pidToSkills = pidToSkills;
        this.skillToVideos = skillToVideos;
    }

    public static SkillMapper load() throws IOException {
        try (InputStream is = SkillMapper.class.getResourceAsStream("/inmh_link.json")) {
            if (is == null) throw new IOException("inmh_link.json not found on classpath");
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();

            Map<String, List<String>> pidMap = new HashMap<>();
            JsonObject pids = root.getAsJsonObject("pid_to_skills");
            for (var entry : pids.entrySet()) {
                pidMap.put(entry.getKey(), toStringList(entry.getValue().getAsJsonArray()));
            }

            Map<String, List<VideoRef>> videoMap = new HashMap<>();
            JsonObject videos = root.getAsJsonObject("skill_to_videos");
            for (var entry : videos.entrySet()) {
                List<VideoRef> refs = StreamSupport.stream(entry.getValue().getAsJsonArray().spliterator(), false)
                        .map(e -> {
                            JsonObject o = e.getAsJsonObject();
                            return new VideoRef(
                                    o.get("key").getAsString(),
                                    o.get("title").getAsString(),
                                    o.get("format").getAsString());
                        })
                        .collect(Collectors.toList());
                videoMap.put(entry.getKey(), refs);
            }

            return new SkillMapper(pidMap, videoMap);
        }
    }

    public List<String> skillsForPid(String pid) {
        List<String> skills = pidToSkills.get(pid);
        return skills != null ? skills : Collections.emptyList();
    }

    public Map<String, List<VideoRef>> getSkillToVideos() {
        return Collections.unmodifiableMap(skillToVideos);
    }

    public List<VideoRef> videosForSkill(String skill) {
        List<VideoRef> refs = skillToVideos.get(skill);
        return refs != null ? refs : Collections.emptyList();
    }

    private static List<String> toStringList(JsonArray arr) {
        return StreamSupport.stream(arr.spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }
}
