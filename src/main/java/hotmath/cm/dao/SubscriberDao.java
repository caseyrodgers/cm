package hotmath.cm.dao;

import hotmath.spring.SpringManager;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.SalesZone;
import hotmath.subscriber.SalesZone.Representative;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Simple DAO for Subscriber data.
 * 
 * @author Bob
 * 
 */
public class SubscriberDao extends SimpleJdbcDaoSupport {

    private static final Logger __logger = Logger.getLogger(SubscriberDao.class);

    static private SubscriberDao __instance;

    static SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static public SubscriberDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (SubscriberDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(SubscriberDao.class.getName());
        }
        return __instance;
    }

    private SubscriberDao() {
    }

    /**
     * Set the max students for specified subscriber
     * 
     * @param sub
     * @param maxStudents
     * @throws Exception
     */
    public void setMaxStudents(Connection conn, HotMathSubscriber sub, int maxStudents) throws Exception {
    	Connection localConnection = null;
    	ResultSet rs = null;
    	PreparedStatement ps = null;

    	try {
    		if (conn == null) {
    			localConnection = HMConnectionPool.getConnection();
    			conn = localConnection;
    		}

    		rs = conn.createStatement().executeQuery(
    				"select * from SUBSCRIBERS_SERVICES where service_name = 'catchup' and subscriber_id = '" + sub.getId()
    				+ "'");
    		if (!rs.first())
    			throw new Exception("No 'catchup' service found");

    		int ssid = rs.getInt("ssid");
    		conn.createStatement().executeUpdate(
    				"delete from SUBSCRIBERS_SERVICES_CONFIG_CATCHUP where subscriber_id = '" + sub.getId() + "'");

    		String sql = "insert into SUBSCRIBERS_SERVICES_CONFIG_CATCHUP(subscriber_id, max_students, subscriber_svc_id)values(?,?,?)";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, sub.getId());
    		ps.setInt(2, maxStudents);
    		ps.setInt(3, ssid);
    		int cnt = ps.executeUpdate();
    		if (cnt != 1)
    			throw new Exception("Could not set max_students for the catchup math service");
    	} finally {
    		SqlUtilities.releaseResources(rs, ps, localConnection);
    	}
    }

    public void setExpireDate(String subscriberId, Date expirationDate) throws Exception {
    	String sql = "update SUBSCRIBERS_SERVICES set date_expire = ? where subscriber_id = ? and service_name = 'catchup'";

    	Connection conn = null;
    	PreparedStatement ps = null;

    	try {
    		conn = HMConnectionPool.getConnection();

    		ps = conn.prepareStatement(sql);
    		ps.setString(1, _dateFormat.format(expirationDate));
    		ps.setString(2, subscriberId);
    		int cnt = ps.executeUpdate();
    		if (cnt != 1)
    			throw new Exception("Could not set date_expire for the catchup math service");
    	} finally {
    		SqlUtilities.releaseResources(null, ps, conn);
    	}
    	
    }

    public boolean isCmPilot(String subscriberId) throws Exception {
    	String sql = "select comments from SUBSCRIBERS where id = ?";

    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	boolean ret = false;

    	try {
    		conn = HMConnectionPool.getConnection();

    		ps = conn.prepareStatement(sql);
    		ps.setString(1, subscriberId);
    		rs = ps.executeQuery();
    		if (rs.next()) {
    			String comments = rs.getString("comments");
    			int cmPilotOffset = comments.indexOf("TYPE_SERVICE_CATCHUP_PILOT");
    			int nextCmOffset = (cmPilotOffset > 0) ?
    					comments.indexOf("TYPE_SERVICE_CATCHUP", cmPilotOffset+1) : -1;
    			ret = (cmPilotOffset > 0 && nextCmOffset < 0);
    		}
    	}
    	finally {
    		SqlUtilities.releaseResources(null, ps, conn);
    	}
    	return ret;
    }

    public Representative getSalesRepresentativeByName(String name) {
    	Connection conn = null;
    	try {
    		conn = HMConnectionPool.getConnection();
        	return SalesZone.getSalesRepresentativeByName(conn, name);
    	}
    	catch (Exception e) {
    		__logger.error("getSalesRepresentativeByName(): name: " + name, e);
    		
    	}
    	finally {
    		SqlUtilities.releaseResources(null, null, conn);
    	}
    	return null;
    }
}
