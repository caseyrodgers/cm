package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmHighlightsDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CmHighlightsDao_Test extends CmDbTestCase {
    
    public CmHighlightsDao_Test(String name) {
        super(name);
    }
    
    

    public void testGroupUsage() throws Exception {
        
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        CmList<HighlightReportData> data =new CmHighlightsDao().getReportGroupUsage(conn, 2, uids, from, to);
        assertTrue(data.size() > 1);
    }
    
    public void testGroupProgress() throws Exception {
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        CmList<HighlightReportData> data =new CmHighlightsDao().getReportGroupProgress(conn, 2, uids, from, to);
        assertTrue(data.size() > 1);
    }
        
    public void testMostGamesViewed() throws Exception {
        CmList<HighlightReportData> data = new CmHighlightsDao().getReportMostGames(conn, uids, from, to);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getQuizzesTaken() > 0);
    }
    
    
    public void testgetReportAvgQuizScores() throws Exception {
        CmList<HighlightReportData> data = new CmHighlightsDao().getReportAvgQuizScores(conn, uids, from, to);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getQuizzesTaken() > 0);
    }

    public void testgetReportQuizzesPassed() throws Exception {
        CmList<HighlightReportData> data = new CmHighlightsDao().getReportQuizzesPassed(conn, uids, from, to);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getQuizzesTaken() > 0);
    }


    public void testLeastEffor() throws Exception {
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        CmList<HighlightReportData> data = new CmHighlightsDao().getReportLeastEffort(conn, uids, from, to);
        assertTrue(data.size() > 0);
    }
    
    
    public void testGreatestEffort() throws Exception {
        
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        CmList<HighlightReportData> data = new CmHighlightsDao().getReportGreatestEffort(conn, uids, from, to);
        assertTrue(data.size() > 0);
    }

    
    List<String> uids = new ArrayList<String>()
    {
        {
            add("24412");
            add("26645");
        }
    };
    Date from = new GregorianCalendar(2010,1,1).getTime();
    Date to = new Date();    
}
