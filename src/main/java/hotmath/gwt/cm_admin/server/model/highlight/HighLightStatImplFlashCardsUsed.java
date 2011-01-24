package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class HighLightStatImplFlashCardsUsed extends HighLightStatImplBase {
    
    @Override
    public void getStatsFromDate(Connection conn, Date fromDate) throws Exception {
        String sql = 
        "select date(i.view_time) as view_date, u.uid, count(*) as flash_cards_viewed " +
        "from HA_USER u " +
        "  JOIN HA_TEST t on t.user_id = u.uid " +
        "  JOIN HA_TEST_RUN r on r.test_id = t.test_id " +
        "  JOIN HA_TEST_RUN_INMH_USE i on i.run_id = r.run_id " +
        "where date(r.run_time)  > ? " +
        "and i.item_type in ('flashcard','flashcard_spanish') " +
        "group by date(i.view_time),u.uid ";
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int uid = rs.getInt("uid");
                Date runDate = rs.getDate("view_date");
                int cnt = rs.getInt("flash_cards_viewed");
                
                writeStatRecord(conn, uid, runDate, "flashcards_used", cnt, -1);
            }
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }        
    }
}
