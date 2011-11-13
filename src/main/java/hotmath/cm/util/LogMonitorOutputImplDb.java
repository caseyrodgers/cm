package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class LogMonitorOutputImplDb implements LogMonitorOutput {
    public void writeRecord(String type, String timeStamp, String actionName, String args, int elapseTime, int userId,
            String userType, String actionId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "insert into HA_ACTION_LOG(type, time_stamp, action_name, action_args, elapse_time, user_id, user_type, action_id)values(?,?,?,?,?,?,?,?)";
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, type);
            ps.setString(2, timeStamp);
            ps.setString(3, actionName);
            ps.setString(4, args);
            ps.setInt(5, elapseTime);
            ps.setInt(6, userId);
            ps.setString(7, userType);
            ps.setString(8, actionId);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
        }
    }
}
