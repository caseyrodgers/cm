package hotmath.cm.server;

import hotmath.cm.serverr.IonicPush;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/** manage a queue of student commands
 *  that need to be pull from the server.
 *  
 *   *  
 * @author casey
 *
 */
public class HaUserCommandService {
    
    private static HaUserCommandService __instance;

    Logger __logger = Logger.getLogger(HaUserCommandService.class);
    
    static public HaUserCommandService getInstance() {
        if(__instance == null) {
            __instance = new HaUserCommandService();
        }
        return __instance;
    }

    
    
    final int WAIT_TIME=60000;
    
    Thread _watchThread;
    boolean threadStop;
    
    public void startQueueWatcher() {
        if(_watchThread != null) {
            __logger.warn("watch thread is already running");
            return;
        }
        
        __logger.info("Starting student message queue watcher");
        
        new Thread() {
            public void run() {
                
                while(!threadStop) {
                    try {
                        Thread.sleep(WAIT_TIME);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        checkQueue();
                    }
                    catch(Exception e) {
                        __logger.error("Error checking queue", e);
                    }
                }    
            }
        }.start();
    }

    
    /** Look for user's who are using mobile and
     *  have a valid device key...  If any found
     *  then send them a notification ... which will
     *  trigger the client to call back to server for
     *  their events.
     *  
     * @throws Exception
     */
    private void checkQueue() throws Exception {
        
        __logger.debug("Checking student message queue");
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        try {
            conn = HMConnectionPool.getConnection();
            
            psUpdate = conn.prepareStatement("update HA_USER_EVENTS set mobile_notified = now() where id = ? ");
            
            String sql = 
                    "select e.id, u.uid, d.device_token " +
                            "from HA_USER u " +
                            "   join HA_USER_EVENTS e on e.uid = u.uid " +
                            "   join HA_USER_DEVICE d on d.uid = e.uid " +
                            "where    mobile_notified is null " +
                            " and device_token != 'null' " +
                            "order by e.id    "; 
            
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            List<String> tokens = new ArrayList<String>();
            while(rs.next()) {
                int eventId = rs.getInt("id");
                
                
                // make this event as having been been
                // delivered to the mobile device
                psUpdate.setInt(1, eventId);
                if(psUpdate.executeUpdate() != 1) {
                    __logger.warn("Could not mark event record as having been sent to mobile device: " + eventId);
                }
                
                int uidToNotify = rs.getInt("uid");
                String token = rs.getString("device_token");
                
                tokens.add(token);
            }
            
            if(tokens.size()>0) {
                notifyUsers(tokens);
            }
        }
        finally {
            SqlUtilities.releaseResources(null, ps, conn);
        }
        
    }

    private void notifyUsers(List<String> tokens) throws Exception {
        __logger.info("Notifing student devices: " + tokens);
        
        JSONObject jo = new JSONObject();
        
        JSONArray jaTokens = new JSONArray();
        for(String t: tokens) {
            jaTokens.put(t);    
        }

        
        JSONObject joNotification = new JSONObject();
        joNotification.put("alert", "Catchup Math alert");
        JSONObject joIos = new JSONObject();
        joIos.put("badge",1);
        joIos.put("sound", "ping.aiff");
        joIos.put("priority", 10);
        joIos.put("contentAvailable", 1);
        
        joNotification.put("ios", joIos);
        
        JSONObject joAnd = new JSONObject();
        joAnd.put("collapseKey", "catchupmath_alert");
        joAnd.put("delayWhileIdle", false);
        //joAnd.put("timeToLive", 600*24);
        // joAnd.put("retries", 100);
        
        
        joNotification.put("android", joAnd);
        
        jo.put("notification",joNotification);
        jo.put("tokens", jaTokens);  
        
        //String json = FileUtils.readFileToString(new File("/temp/junk.json"));
        
        
        String apiKey = CatchupMathProperties.getInstance().getProperty("ionic.secret", null);
        if(apiKey == null) {
            throw new Exception("ionic.secret property cannot be null");
        }
        String apiId = "122a6071";

        
        System.out.println("Pushing JSON: " + jo.toString());
        String result = new IonicPush(apiId, apiKey).push(jo.toString());
        System.out.println("push result: " + result);
    }
}
