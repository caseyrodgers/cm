package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmHighlightsDao;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
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
        CmList<HighlightReportData> data = CmHighlightsDao.getInstance().getReportGroupUsage(conn, 2, uids, from, to);
        assertTrue(data.size() > 1);
    }
    
    public void testGroupProgress() throws Exception {
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        CmList<HighlightReportData> data = CmHighlightsDao.getInstance().getReportGroupProgress(conn, 2, uids, from, to);
        assertTrue(data.size() > 1);
    }
        
    public void testMostGamesViewed() throws Exception {
        CmList<HighlightReportData> data = CmHighlightsDao.getInstance().getReportMostGames(conn, uids, from, to);
        assertNotNull(data);
    }
    
    
    public void testgetReportAvgQuizScores() throws Exception {
        CmList<HighlightReportData> data = CmHighlightsDao.getInstance().getReportAvgQuizScores(conn, uids, from, to);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getQuizzesTaken() > 0);
    }

    public void testgetReportQuizzesPassed() throws Exception {
        CmList<HighlightReportData> data = CmHighlightsDao.getInstance().getReportQuizzesPassed(conn, uids, from, to);
        assertTrue(data.size() > 0);
        assertTrue(data.get(0).getQuizzesTaken() > 0);
    }


    public void testLeastEffort() throws Exception {
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        CmList<HighlightReportData> data = CmHighlightsDao.getInstance().getReportLeastEffort(conn, uids, from, to);
        assertTrue(data.size() > 0);
    }
    
    
    public void testGreatestEffort() throws Exception {
        
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        List<HighlightReportData> data = CmHighlightsDao.getInstance().getReportGreatestEffort(uids, from, to);
        assertTrue(data.size() > 0);
    }

    public void testWidgetAnswersPercent() throws Exception {
        
        Date from = new GregorianCalendar(2010,1,1).getTime();
        Date to = new Date();
        List<HighlightReportData> data = CmHighlightsDao.getInstance().getReportWidgetAnswersPercent(uids, from, to);
        assertTrue(data.size() > 0);
    }
    
    List<String> uids = new ArrayList<String>()
    {
        {
            add("24412");
            add("26645");
            add("23472");
            add("28001");
        }
    };
    Date from = new GregorianCalendar(2010,1,1).getTime();
    Date to = new Date();    
}
