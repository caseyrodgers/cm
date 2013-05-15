package hotmath.cm.dao;

import java.util.Date;
import java.util.List;

import hotmath.gwt.cm.server.CmDbTestCase;

public class CCSSReportDao_Test extends CmDbTestCase {

	static final long MSEC_IN_YEAR = 365*24*60*60*1000;
    
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
        List<String> standardNames = dao.getStudentQuizStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }
    
    public void testGetStudentReviewStandardNames() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<String> standardNames = dao.getStudentReviewStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }
    
    public void testGetStudentAssignmentStandardNames() throws Exception {
    	Date toDate = new Date();
    	Date fromDate = new Date(toDate.getTime() - MSEC_IN_YEAR);
        CCSSReportDao dao = CCSSReportDao.getInstance();
        List<String> standardNames = dao.getStudentAssignmentStandardNames(9451, fromDate, toDate);
        
        assertTrue(standardNames != null);
    }
        
}
