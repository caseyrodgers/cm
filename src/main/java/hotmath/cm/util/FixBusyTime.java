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
    	String sql0 = "select distinct uid from CM_USER_BUSY_TMP";
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
    	long prevTime = 0;
    	long currTime = 0;
    	int activeMinutes;
    	int count = 0;
    	int totalAdjustment = 0;

    	try {
        	ps.setInt(1,  uid);
    		rs = ps.executeQuery();
    		while (rs.next()) {
    			if (prevTime == 0) {
    				prevTime = rs.getTimestamp("busy_time").getTime();
    				continue;
    			}
    			currTime = rs.getTimestamp("busy_time").getTime();
    			activeMinutes = rs.getInt("active_minutes");
    			double mins = ((double)(currTime - prevTime))/60000.00;
    			long diffMinutes = Math.round(mins);
    			//System.out.println(String.format("prevTime: %d,  currTime: %d, mins: %.2f, diffMinutes: %d", prevTime, currTime, mins, diffMinutes ));
    			if (diffMinutes < activeMinutes) {
    				int id = rs.getInt("id");
    				psUpdate.setLong(1, diffMinutes);
    				psUpdate.setInt(2, id);
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
    	if (count > 0)
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
