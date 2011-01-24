package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class HighLightStatImplAvgLoginsWeek extends HighLightStatImplBase {
    
    @Override
    public void getStatsFromDate(Connection conn, Date fromDate) throws Exception {
        
        String sql = 
        		"select date(login_time) as login_date, user_id, count(*) as logins " +
                " from HA_USER_LOGIN " +
                " where date(login_time) > ? " +
                " group by date(login_time), user_id ";
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int uid = rs.getInt("user_id");
                Date runDate = rs.getDate("login_date");
                int cnt = rs.getInt("logins");
                
                writeStatRecord(conn, uid, runDate, "logins_week", cnt, -1);
            }
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }        
    }
}
