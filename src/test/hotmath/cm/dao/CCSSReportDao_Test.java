package hotmath.cm.dao;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.model.CCSSData;

import java.util.Date;
import java.util.List;
//import hotmath.gwt.shared.client.model.CCSSGroupCoverageData;

public class CCSSReportDao_Test extends CmDbTestCase {

	static final long MSEC_IN_YEAR = 365L*24L*60L*60L*1000L;
    
    public CCSSReportDao_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testGetStudentQuizStandardNames() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<CCSSCoverageData> standardNames = dao.getStudentQuizStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }
    
    public void testGetStudentReviewStandardNames() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<CCSSCoverageData> standardNames = dao.getStudentReviewStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }
    
    public void testGetStudentAssignmentStandardNames() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<CCSSCoverageData> standardNames = dao.getStudentAssignmentStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }

    public void testGetStudentCombinedStandardNames() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<CCSSCoverageData> standardNames = dao.getStudentCombinedStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }

    public void testGetCCSSData() throws Exception {
        CCSSReportDao dao = CCSSReportDao.getInstance();
        CCSSData data = dao.getCCSSData();

        assertTrue(data.getLevels().size() > 0);
    }

//    public void testGetCCSSGroupCoverageData() throws Exception {
//    	Date toDate = new Date();
//    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
//        CCSSReportDao dao = CCSSReportDao.getInstance();
//        List<CCSSGroupCoverageData> list = dao.getCCSSGroupCoverageDate(2, 1976, "cm_test", fromDate, toDate, 0, 24);
//        list = dao.getCCSSGroupCoverageDate(2, 1976, "cm_test", fromDate, toDate, 25, 49);
//        assertTrue(list != null);
//        list = dao.getCCSSGroupCoverageDate(2, 1976, "cm_test", fromDate, toDate, 50, 74);
//        assertTrue(list != null);
//        list = dao.getCCSSGroupCoverageDate(2, 1976, "cm_test", fromDate, toDate, 75, 99);
//        assertTrue(list != null);
//        list = dao.getCCSSGroupCoverageDate(2, 1976, "cm_test", fromDate, toDate, 100, 100);
//        assertTrue(list != null);
//    	
//    }
}
