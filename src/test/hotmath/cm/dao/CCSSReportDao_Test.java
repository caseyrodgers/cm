package hotmath.cm.dao;

import java.util.Date;
import java.util.List;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.model.CCSSData;
import hotmath.gwt.shared.client.model.CCSSDetail;

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

    public void testGetCCSSGroupCoverageData() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<CCSSCoverageData> list = dao.getCCSSGroupCoverageData(2, 1976, fromDate, toDate, 0, 24);
        list = dao.getCCSSGroupCoverageData(2, 1976, fromDate, toDate, 25, 49);
        assertTrue(list != null);
        list = dao.getCCSSGroupCoverageData(2, 1976, fromDate, toDate, 50, 74);
        assertTrue(list != null);
        list = dao.getCCSSGroupCoverageData(2, 1976, fromDate, toDate, 75, 99);
        assertTrue(list != null);
        list = dao.getCCSSGroupCoverageData(2, 1976, fromDate, toDate, 100, 100);
        assertTrue(list != null);
    }

    public void testGetCCSSDetail() throws Exception {
        CCSSReportDao dao = CCSSReportDao.getInstance();
        CCSSDetail detail = dao.getCCSSDetail("6.G.A.4");
        assertTrue(detail != null);
    }

    public void testGetCCSSNamesForLevel() throws Exception {
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<String> names = dao.getStandardNamesForLevel("Grade 7", false);
        assertTrue(names != null && names.size()>0);
        names = dao.getStandardNamesForLevel("Grade 7", true);
        assertTrue(names != null && names.size()>0);
    }

}
