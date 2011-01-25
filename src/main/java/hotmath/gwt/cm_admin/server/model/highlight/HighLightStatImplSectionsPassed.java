package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

/** Update Sections Passed stats
 * 
 * @author casey
 *
 */
public class HighLightStatImplSectionsPassed extends HighLightStatImplBase {
    
    public HighLightStatImplSectionsPassed() {}
    
    final static Logger __logger = Logger.getLogger(HighLightStatImplSectionsPassed.class);

    @Override
    public void getStatsFromDate(final Connection conn, Date fromDate, Date toDate) throws Exception {
        String sql =
            "select date(run_time) as run_date, uid, count(*) as cnt_passed " +
            "from   HA_USER u " +
            "       join HA_TEST t " +
            "         on t.user_id = u.uid " +
            "       join HA_TEST_RUN r " +
            "         on r.test_id = t.test_id " +
            "where  date(r.run_time) >=  ? " +
            "       and r.is_passing = 1 " +
            "group by run_date, uid " +
            " order by run_date";
        
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Date runDate = rs.getDate("run_date");
                int uid = rs.getInt("uid");
                int cntPass = rs.getInt("cnt_passed");
                writeStatRecord(conn, uid, runDate, "sections_passed", cntPass,-1);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
}
