package hotmath.cm.util;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

public class JsonUtil {

	private static final Logger LOGGER = Logger.getLogger(JsonUtil.class);
	
    public static String getChapter(String json) {
        if (json == null || json.trim().length() == 0)
            return null;

        String chap = null;
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.has("chapters")) {
                JSONArray ja = jo.getJSONArray("chapters");
                chap = ja.getString(0);
            }
        } catch (Exception e) {
            LOGGER.error("*** Error extracting Chapter from JSON", e);
        }

        return chap;
    }

    public static int getSectionCount(String json) {
        if (json == null || json.trim().length() == 0)
            return 0;

        int count = 0;
        try {
            JSONObject jo = new JSONObject(json);

            if (jo.has("segments")) {
                JSONArray ja = jo.getJSONArray("segments");
                count = ja.getInt(0);
            }
        } catch (Exception e) {
            LOGGER.error("*** Error extracting Segments from JSON", e);
        }

        return count;
    }
}
