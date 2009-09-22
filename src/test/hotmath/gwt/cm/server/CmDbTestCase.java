package hotmath.gwt.cm.server;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;

public class CmDbTestCase extends DbTestCase {
    
    public CmDbTestCase(String name) {
        super(name);
    }
    
    protected static HaUser _user;
    protected HaTest _test;
    protected HaTestRun _testRun;
    protected AssessmentPrescription _prescription;
    
    /** Create a new test account to use for test/samples/demo
     * 
     * @return The userid of the test account
     * 
     * @throws Exception
     */
    public int setupDemoAccount() throws Exception {
        
        int uid = CmTestUtils.setupDemoAccount();
        
        _user = HaUser.lookUser(conn, uid, null);
        return uid;
    }
    
    /** Create a demo user, then create a default test
     * 
     * @return The new test_id
     * @throws Exception
     */
    public HaTest setupDemoAccountTest() throws Exception {
        
        HaUser user = HaUser.lookUser(conn, setupDemoAccount(),null);
        
        HaTestDef testDef = new HaTestDef(conn, user.getAssignedTestName());
        _test = HaTest.createTest(conn,user.getUid(), testDef, 1);
        
        return _test;
    }
    
    
    public HaTestRun setupDemoAccountTestRun() throws Exception {
        HaTest test = HaTest.loadTest(conn, setupDemoAccountTest().getTestId());
        String pids[] = {"pid_1", "pid_2"};
        _testRun = test.createTestRun(conn,pids,0,0,0);
        
        return _testRun;
    }
    
    
    public AssessmentPrescription setupDemoAccountPrescription() throws Exception {

        HaTestRun testRun = HaTestRun.lookupTestRun(conn, setupDemoAccountTestRun().getRunId());
        
        int c =  testRun.getAnsweredCorrect();
        int i = testRun.getAnsweredIncorrect();
        int n = testRun.getHaTest().getNumTestQuestions() - (c +  i);
        _prescription = AssessmentPrescriptionManager.getInstance().createPrescription(conn, testRun.getHaTest().getTestId(), testRun.getPidList(),c,i,n);
        
        return _prescription;
    }
}
