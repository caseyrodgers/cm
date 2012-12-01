package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class FixLessonResult {

    final static Logger __logger = Logger.getLogger(FixLessonResult.class);

    public FixLessonResult(int start, int limit) throws Exception {
        String sql1 = "select * from HA_TEST_RUN_RESULTS_TMP where rid >= ?" + " order by rid LIMIT " + limit;
        String sql2 = "update HA_TEST_RUN_RESULTS set answer_status = ? where run_id = ? and pid = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        try {
            conn = HMConnectionPool.getConnection();

            psUpdate = conn.prepareStatement(sql2);
            ps = conn.prepareStatement(sql1);
            ps.setInt(1, start);

            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            int lastRid = 0;
            while (rs.next()) {
                int rid = rs.getInt("rid");
                int answerIndex = rs.getInt("answer_index");
                int runId = rs.getInt("run_id");
                String pid = rs.getString("pid");

                lastRid = rid;

                if ((cnt++ % 1000) == 0) {
                    __logger.info("Updating row: " + cnt++ + " rid=" + rid);
                }

                psUpdate.setInt(1, answerIndex);
                psUpdate.setInt(2, runId);
                psUpdate.setString(3, pid);

                if (psUpdate.executeUpdate() != 1) {
                    __logger.error("COULD NOT UPDATE RECORD: " + rid);
                }
            }

            __logger.info("UPDATE COMPLETE, last RID=" + lastRid);

        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }

    }

    static public void main(String as[]) {
        try {
            int start = Integer.parseInt(as[0]);
            int limit = Integer.parseInt(as[1]);
            new FixLessonResult(start, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
