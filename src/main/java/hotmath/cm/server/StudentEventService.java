package hotmath.cm.server;

import hotmath.cm.server.model.StudentEventsDao;
import hotmath.cm.serverr.IonicPush;
import hotmath.cm.util.CatchupMathProperties;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * manage a queue of student commands that need to be pull from the server.
 * 
 * *
 * 
 * @author casey
 *
 */
public class StudentEventService {

    private static StudentEventService __instance;

    Logger __logger = Logger.getLogger(StudentEventService.class);

    static public StudentEventService getInstance() {
        if (__instance == null) {
            __instance = new StudentEventService();
        }
        return __instance;
    }

    final int WAIT_TIME = 60000;

    Thread _watchThread;
    boolean threadStop;

    public void startQueueWatcher() {
        if (_watchThread != null) {
            __logger.warn("watch thread is already running");
            return;
        }

        __logger.info("Starting student event queue watcher");

        new Thread() {
            public void run() {

                while (!threadStop) {
                    try {
                        Thread.sleep(WAIT_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        checkQueue();
                    } catch (Exception e) {
                        __logger.error("Error checking queue", e);
                    }
                }
            }
        }.start();
    }

    /**
     * Look for user's who are using mobile and have a valid device key... If
     * any found then send them a notification ... which will trigger the client
     * to call back to server for their events.
     * 
     * @throws Exception
     */
    private void checkQueue() throws Exception {
        __logger.debug("Checking student message queue");
        List<String> tokens = StudentEventsDao.getInstance().getDevicesWithEvents();
        if (tokens.size() > 0) {
            notifyUsers(tokens);
        }
    }

    private void notifyUsers(List<String> tokens) throws Exception {
        __logger.info("Notifing student devices: " + tokens);

        JSONObject jo = new JSONObject();

        JSONArray jaTokens = new JSONArray();
        for (String t : tokens) {
            jaTokens.put(t);
        }

        JSONObject joNotification = new JSONObject();
        joNotification.put("alert", "Catchup Math alert");
        JSONObject joIos = new JSONObject();
        joIos.put("badge", 1);
        joIos.put("sound", "ping.aiff");
        joIos.put("priority", 10);
        joIos.put("contentAvailable", 1);

        joNotification.put("ios", joIos);

        JSONObject joAnd = new JSONObject();
        joAnd.put("collapseKey", "catchupmath_alert");
        joAnd.put("delayWhileIdle", false);
        // joAnd.put("timeToLive", 600*24);
        // joAnd.put("retries", 100);

        joNotification.put("android", joAnd);

        jo.put("notification", joNotification);
        jo.put("tokens", jaTokens);

        // String json = FileUtils.readFileToString(new
        // File("/temp/junk.json"));

        String apiKey = CatchupMathProperties.getInstance().getProperty("ionic.secret", null);
        if (apiKey == null) {
            throw new Exception("ionic.secret property cannot be null");
        }
        String apiId = "122a6071";

        System.out.println("Pushing JSON: " + jo.toString());
        String result = new IonicPush(apiId, apiKey).push(jo.toString());
        System.out.println("push result: " + result);
    }
}
