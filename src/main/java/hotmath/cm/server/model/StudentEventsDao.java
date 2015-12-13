package hotmath.cm.server.model;

import hotmath.gwt.cm_core.client.model.StudentEvent;
import hotmath.gwt.cm_core.client.model.StudentEventInfo;
import hotmath.spring.SpringManager;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * 
 * @author Bob
 * 
 */

public class StudentEventsDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(StudentEventsDao.class);

    static private StudentEventsDao __instance;

    static public StudentEventsDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (StudentEventsDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(StudentEventsDao.class.getName());
        }
        return __instance;
    }

    private StudentEventsDao() {
        /** empty */
    }

    /** get all messages/events that have been delivered
     * 
     * @param uid
     * @return
     * @throws Exception
     */
    public StudentEventInfo getEventHistoryFor(final int uid) throws Exception {

        String sql = "select * from HA_USER_EVENTS where uid = ? and  delivered_time is not null order by create_time desc";

        List<StudentEvent> list = this.getJdbcTemplate().query(sql, new Object[] { uid },
                new RowMapper<StudentEvent>() {
                    public StudentEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentEvent model;
                        try {
                            model = new StudentEvent(uid, rs.getString("event_data"));
                        } catch (Exception e) {
                            __logger.error("error: " + e.getMessage(), e);
                            throw new SQLException(e.getMessage());
                        }
                        return model;
                    }
                });

        return new StudentEventInfo(list);
    }

    /** Find any devices that have pending events
     * 
     * @return
     * @throws Exception
     */
    public List<String> getDevicesWithEvents() throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        try {
            conn = HMConnectionPool.getConnection();

            psUpdate = conn.prepareStatement("update HA_USER_EVENTS set mobile_notified = now() where id = ? ");

            String sql = "select e.id, u.uid, d.device_token " + "from HA_USER u "
                    + "   join HA_USER_EVENTS e on e.uid = u.uid " + "   join HA_USER_DEVICE d on d.uid = e.uid "
                    + "where delivered_time is null and mobile_notified is null " + " and device_token != 'null' " + "order by e.id    ";

            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<String> tokens = new ArrayList<String>();
            while (rs.next()) {
                int eventId = rs.getInt("id");

                // make this event as having been been
                // delivered to the mobile device
                psUpdate.setInt(1, eventId);
                if (psUpdate.executeUpdate() != 1) {
                    __logger.warn("Could not mark event record as having been sent to mobile device: " + eventId);
                }
                String token = rs.getString("device_token");

                tokens.add(token);
            }

            return tokens;
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
        }

    }
    
    
    
    /** Return event data for named user
     * 
     *  remove after extraction
     *  
     *   Only one event per request
     *  
     * 
     * @param conn
     * @param uid
     * @return
     * @throws Exception
     */
    public StudentEvent getStudentEventCurrent(int uid) throws Exception {
        Connection conn=null;
        
        String sql = "select * from HA_USER_EVENTS where uid = ? and delivered_time is null order by id limit 1";
        PreparedStatement ps =null;
        String eventData=null;
        try {
            
            
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            ResultSet rs = ps.executeQuery();
            
            
            PreparedStatement ps2=null;
            
            String sqlUpdate = "update HA_USER_EVENTS set delivered_time = now() where id = ?";
            ps2 = conn.prepareStatement(sqlUpdate);
            if(rs.next()) {
                int id = rs.getInt("id");
                eventData = rs.getString("event_data");
                
                ps2.setInt(1, id);
                if(ps2.executeUpdate() != 1) {
                    __logger.info("user event record not updated!: " + id);
                }
            }
            
            StudentEvent studentEvent = new StudentEvent(eventData);
            
            return studentEvent;
            
        } finally {
            SqlUtilities.releaseResources(null, ps,conn);
        }
    }
    
    

    public enum EventType{MESSAGE("message"), ASSIGNMENT_MESSAGE("assignment_message");
        private String tag;
        private EventType(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }
    };
    
    
    

    public void addStudentEvent(int uid, EventType type, String data) throws Exception {
        
        JSONObject jo = new JSONObject();
        jo.put("type", type.getTag());
        
        
        if(data.startsWith("{")) {
        	// is json
        	JSONObject joN = new JSONObject(data);
        	jo.put("data", joN);
        }
        else {
        	jo.put("data",  data); // not json
        }
        

        String sql = "insert into HA_USER_EVENTS(uid, event_data, create_time) values(?, ?, now())";
        PreparedStatement ps =null;
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            ps.setString(2, jo.toString());
            if(ps.executeUpdate() != 1) {
                __logger.warn("message not added: " + uid + ", " + jo);
            }
            
        } finally {
            SqlUtilities.releaseResources(null, ps,conn);
        }
    }

    public static void sendEventsToGroup(int groupId, String message) throws Exception {
        Connection conn=null;
        PreparedStatement st=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            String sql = "select uid from HA_USER where is_active = 1 and group_id = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, groupId) ;
            
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
               StudentEventsDao.getInstance().addStudentEvent(rs.getInt("uid"), EventType.MESSAGE, message);
            }
            
        }
        finally {
            SqlUtilities.releaseResources(null,st,conn);
        }
                
    }    
    
    

}
