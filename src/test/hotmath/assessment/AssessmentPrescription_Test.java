package hotmath.assessment;

import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUserDao;

import java.util.List;

public class AssessmentPrescription_Test extends CmDbTestCase {

    public AssessmentPrescription_Test(String name) throws Exception {
        super(name);
    }

    
    String CA_STATE_TEST="California State Exit Exam";

    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	if(_test == null) {
    		setupDemoAccountTest();
    	}
    }
    
    int testRunId=161269;
    public void testCreatePrescriptionRppWithJson() throws Exception {
        HaTestDao.getInstance().removeTestRuns(_test);
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn,_user.getUid(), _test.getTestId(), 0, 1,0);
        
        String file = "topics/mixed-numbers.html";
        String title = "mixed-numbers.html";
        InmhItemData inmhData = new InmhItemData(new INeedMoreHelpItem("Review",file , title));
        int uid = testRun.getHaTest().getUser().getUid();
        List<RppWidget> workBookPids = inmhData.getWidgetPool(conn,uid + "/" + testRun.getRunId());

        assertTrue(!workBookPids.get(0).isFlashRequired());
        
        ClientEnvironment clientEnvironment = new ClientEnvironment(false);

        AssessmentPrescriptionSession ap = new AssessmentPrescription(conn,testRun).createSession(0, workBookPids, inmhData, true, new ClientEnvironment());
        
        assertTrue(ap.getSessionItems().get(0).getRpp().getWidgetJsonArgs() != null);
    }

    public void testCreatePrescriptionWithOutFlash() throws Exception {
        HaTestDao.getInstance().removeTestRuns(_test);
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn,_user.getUid(), _test.getTestId(), 0, 1,0);
        
        String file = "topics/simplifying-radical-expressions.html";
        String title = "simplifying-radical-expressions";
        InmhItemData inmhData = new InmhItemData(new INeedMoreHelpItem("Review",file , title));
        int uid = testRun.getHaTest().getUser().getUid();
        List<RppWidget> workBookPids = inmhData.getWidgetPool(conn,uid + "/" + testRun.getRunId());

        ClientEnvironment clientEnvironment = HaUserDao.getInstance().getLatestClientEnvironment(testRun.getHaTest().getUser().getUserKey());

        new AssessmentPrescription(conn,testRun).createSession(0, workBookPids, inmhData, true,new ClientEnvironment(false));
    }

    
    public void testCreatePrescriptionWithFlash() throws Exception {
        HaTestDao.getInstance().removeTestRuns(_test);
    	HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn,_user.getUid(), _test.getTestId(), 0, 1,0);
    	
    	String file = "topics/simplifying-radical-expressions.html";
    	String title = "simplifying-radical-expressions";
    	InmhItemData inmhData = new InmhItemData(new INeedMoreHelpItem("Review",file , title));
    	int uid = testRun.getHaTest().getUser().getUid();
    	List<RppWidget> workBookPids = inmhData.getWidgetPool(conn,uid + "/" + testRun.getRunId());

    	ClientEnvironment clientEnvironment = HaUserDao.getInstance().getLatestClientEnvironment(testRun.getHaTest().getUser().getUserKey());
    	
    	new AssessmentPrescription(conn,testRun).createSession(0, workBookPids, inmhData, true,new ClientEnvironment(true));
    }
    
    

    public void testCreateNew1() throws Exception {
        HaTestDao.getInstance().removeTestRuns(_test);
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn,_user.getUid(), _test.getTestId(), 1, 2,0);

        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn,testRun.getRunId());
        assertNotNull(pres);
        assertNotNull(pres.getSessions());
    }
//    
//    public void testIsPassing() throws Exception {
//
//        _testNumber = testRun.getHaTest().getTestId();
//
//        String wrongGuids[] = { "test1", "test2", "test3" };
//        HaTest test = testRun.getHaTest();
//        HaTestRun testRun2 = HaTestDao.getInstance().createTestRun(conn,test.getUser().getUid(), test.getTestId(), 1, 2,0);
//        
//        assertTrue(!testRun2.isPassing());
//    }
//
//    
//    
//    public void testCreate() throws Exception {
//        AssessmentPrescription assTest = new AssessmentPrescription(conn, testRun);
//
//        assertTrue(assTest.getSessions().size() > 1);
//    }
//
//    static int _testNumber = 0;
//    static int _runId = 0;
//
//  
//
//    public void testCreateNew() throws Exception {
//    	HaTestDao.getInstance().removeTestRuns(testRun.getHaTest());
//        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().createPrescription(conn, testRun.getHaTest().getTestId(),"alg1ptests_CourseTest_1_Algebra1PracticeTest_1_1", 1, 2, 0);
//        _runId = pres.getTestRun().getRunId();
//        assertNotNull(pres);
//        assertTrue(pres.getSessions().size() > 0);
//    }
//
//    public void testSession() throws Exception {
//        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
//        AssessmentPrescriptionSession sess = pres.getSessions().get(0);
//        List<INeedMoreHelpResourceType> types = sess.getInmhTypes();
//        assertNotNull(types);
//    }
//
//    public void testSession2() throws Exception {
//        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
//        AssessmentPrescriptionSession sess = pres.getSessions().get(0);
//        List<INeedMoreHelpResourceType> items = sess.getPrescriptionInmhTypes(conn,null);
//        assertNotNull(items);
//    }
//
//    public void testCheckIfReady() throws Exception {
//        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
//        assertTrue(pres.getSessionStatusJson() != null);
//    }
//
//    public void testLookup() throws Exception {
//        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
//        assertNotNull(pres);
//
//        assertTrue(pres.getSessions().size() > 0);
//    }

}
