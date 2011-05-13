package hotmath.assessment;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.util.HMConnectionPool;

import java.sql.Connection;
import java.util.List;

public class AssessmentPrescription_Test extends CmDbTestCase {

    public AssessmentPrescription_Test(String name) throws Exception {
        super(name);
    }

    
    String CA_STATE_TEST="California State Exit Exam";
    Connection conn=null;
    
    protected void tearDown() throws Exception {
        conn.close();
    }
    
    int userId;
    int PRE_ALG_PROF = 16;
    HaTestDef testDef;
    HaTestRun testRun=null;

    public void testGetInmhItemsForSession() throws Exception {

        // int ct=0;
        // int _inmhCntVideo=0;
        //        
        // _prescriptionNumber = 181;
        //
        // AssessmentPrescription pres =
        // AssessmentPrescriptionManager.getInstance().getPrescription(_prescriptionNumber);
        // AssessmentPrescriptionSession session = pres.getSessions().get(0);
        //              
        // int cnt = 0;
        // int _inmhCntLesson = 0;
        // for(INeedMoreHelpResourceType moreHelp: pres.readInmhItems()) {
        // for(INeedMoreHelpItem item:moreHelp.getResources()) {
        // if(moreHelp.getTypeDef().getType().equals("video"))
        // _inmhCntVideo++;
        // else
        // _inmhCntLesson++;
        // }
        // }
    }

    @Override
    protected void setUp() throws Exception {

        conn = HMConnectionPool.getConnection();
        
        String guids[] = { "cahseehm_1_1_PracticeTest_1_1", "cahseehm_1_1_PracticeTest_10_1",
                "cahseehm_1_1_PracticeTest_2_1", "cahseehm_1_1_PracticeTest_3_1", "cahseehm_1_1_PracticeTest_4_1",
                "cahseehm_1_1_PracticeTest_5_1", "cahseehm_1_1_PracticeTest_6_1", "cahseehm_1_1_PracticeTest_7_1",
                "cahseehm_1_1_PracticeTest_8_1", "cahseehm_1_1_PracticeTest_9_1" };
        
        String guids2[] = {"prealgptests_7_1_Chapter7PracticeTest_19_7"};
        
        userId = setupDemoAccount();
        
        testDef = HaTestDefDao.getInstance().getTestDef(CA_STATE_TEST);
        
        HaTest test = HaTestDao.getInstance().createTest(userId, testDef, 1);
        testRun = HaTestDao.createTestRun(conn, userId, test.getTestId(),0, 1, 9);
    }
    
    
    public void testIsPassing() throws Exception {

        _testNumber = testRun.getHaTest().getTestId();

        String wrongGuids[] = { "test1", "test2", "test3" };
        HaTest test = testRun.getHaTest();
        HaTestRun testRun2 = HaTestDao.createTestRun(conn,test.getUser().getUid(), test.getTestId(), 1, 2,0);
        
        assertTrue(!testRun2.isPassing());
    }

    public void testCreateNew1() throws Exception {

        _testNumber = testRun.getHaTest().getTestId();

        String wrongGuids[] = { "test1", "test2", "test3" };
        HaTest test = testRun.getHaTest();
        HaTestRun testRun2 = HaTestDao.createTestRun(conn,test.getUser().getUid(), test.getTestId(), 1, 2,0);
        
        _runId = testRun.getRunId();

        AssessmentPrescription pres = new AssessmentPrescription(conn, testRun);
        assertNotNull(pres);
        assertNotNull(pres.getSessions());
    }
    
    
    public void testCreate() throws Exception {
        AssessmentPrescription assTest = new AssessmentPrescription(conn, testRun);

        assertTrue(assTest.getSessions().size() > 1);
    }

    static int _testNumber = 0;
    static int _runId = 0;

  

    public void testCreateNew() throws Exception {
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().createPrescription(conn, _testNumber,
                "alg1ptests_CourseTest_1_Algebra1PracticeTest_1_1", 1, 2, 0);
        _runId = pres.getTestRun().getRunId();
        assertNotNull(pres);
        assertTrue(pres.getSessions().size() > 0);
    }

    public void testSession() throws Exception {
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
        AssessmentPrescriptionSession sess = pres.getSessions().get(0);
        List<INeedMoreHelpResourceType> types = sess.getInmhTypes();
        assertNotNull(types);
    }

    public void testSession2() throws Exception {
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
        AssessmentPrescriptionSession sess = pres.getSessions().get(0);
        List<INeedMoreHelpResourceType> items = sess.getPrescriptionInmhTypes(conn,null);
        assertNotNull(items);
    }

    public void testCheckIfReady() throws Exception {
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
        assertTrue(pres.getSessionStatusJson() != null);
    }

    public void testLookup() throws Exception {
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _runId);
        assertNotNull(pres);

        assertTrue(pres.getSessions().size() > 0);
    }

}
