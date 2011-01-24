package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class HighLightStatImplAvgQuiz extends HighLightStatImplBase {

    @Override
    public void getStatsFromDate(Connection conn, Date fromDate) throws Exception {
        
        String sql = 
            "select date(r.run_time) as run_date, u.uid, t.test_id,  avg(floor((answered_correct / (answered_correct + answered_incorrect + not_answered) * 100))) as avg_score " +
            "from HA_USER u " +
            "  JOIN HA_TEST t on t.user_id = u.uid " +
            "  JOIN HA_TEST_RUN r on r.test_id = t.test_id " +
            "where date(r.run_time)  > ? " +
            "group by date(r.run_time),u.uid ";
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int uid = rs.getInt("uid");
                Date runDate = rs.getDate("run_date");
                int cnt = rs.getInt("avg_score");

                writeStatRecord(conn, uid, runDate, getStatName(), cnt);
            }
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }        
    }

}
