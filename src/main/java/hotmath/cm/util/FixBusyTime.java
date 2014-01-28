package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

public class FixBusyTime {

    final static Logger __logger = Logger.getLogger(FixBusyTime.class);

    PreparedStatement ps = null;
    PreparedStatement psUids = null;
    PreparedStatement psUpdate = null;

    public FixBusyTime() throws Exception {
    	String sql0 = "select distinct uid from CM_USER_BUSY_TMP limit 3";
        String sql1 = "select * from CM_USER_BUSY_TMP where uid = ? order by busy_time asc";
        String sql2 = "update CM_USER_BUSY_TMP set active_minutes = ? where id = ?";

        Connection conn = null;
        ResultSet rsUids = null;
        try {
            conn = HMConnectionPool.getConnection();

            psUids = conn.prepareStatement(sql0);
            psUpdate = conn.prepareStatement(sql2);
            ps = conn.prepareStatement(sql1);

            rsUids = psUids.executeQuery();
            while (rsUids.next()) {
            	int uid = rsUids.getInt("uid");
            	fixBusyTimeForUid(uid);
            }

        } finally {
            SqlUtilities.releaseResources(rsUids, psUids, null);
            SqlUtilities.releaseResources(null, psUpdate, null);
            SqlUtilities.releaseResources(null, ps, conn);
        }

    }

    private void fixBusyTimeForUid(int uid) throws Exception {
    	ResultSet rs = null;
    	Date prevTime = null;
    	Date currTime = null;
    	int activeMinutes;
    	int count = 0;
    	int totalAdjustment = 0;

    	try {
        	ps.setInt(1,  uid);
    		rs = ps.executeQuery();
    		while (rs.next()) {
    			if (prevTime == null) {
    				prevTime = rs.getDate("busy_time");
    				continue;
    			}
    			currTime = rs.getDate("busy_time");
    			activeMinutes = rs.getInt("active_minutes");
    			long prevMsec = prevTime.getTime();
    			long currMsec = currTime.getTime();
    			int diffMinutes = (int)((currMsec - prevMsec)/60000L);
    			if (diffMinutes < activeMinutes) {
    				int id = rs.getInt("id");
    				psUpdate.setInt(1, id);
    				psUpdate.setInt(2, diffMinutes);
    				psUpdate.executeUpdate();
    				count++;
    				totalAdjustment += (activeMinutes - diffMinutes);
    			}
    			prevTime = currTime;
    		}
    	}
    	finally {
            SqlUtilities.releaseResources(rs, null, null);
    	}
    	System.out.println(String.format("UID: %d, count: %d, totalAdjustment: %d", uid, count, totalAdjustment));
		
	}

	static public void main(String as[]) {
        try {
            new FixBusyTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
