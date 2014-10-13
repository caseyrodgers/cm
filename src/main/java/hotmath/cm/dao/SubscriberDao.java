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
