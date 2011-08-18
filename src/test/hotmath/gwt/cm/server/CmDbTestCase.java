package hotmath.gwt.cm.server;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.gwt.shared.server.service.command.SaveWhiteboardDataCommand;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUser;

public class CmDbTestCase extends DbTestCase {
    
    public CmDbTestCase(String name) {
        super(name);
    }
    
    protected static HaUser _user;
    protected static HaTest _test;
    protected static HaTestRun _testRun;
    protected static AssessmentPrescription _prescription;
    protected static GroupInfoModel _groupModel;
    
    /** Create a new test account to use for test/samples/demo
     * 
     * @return The userid of the test account
     * 
     * @throws Exception
     */
    public int setupDemoAccount() throws Exception {
        
        int uid = CmTestUtils.setupDemoAccount(CmProgram.PREALG_PROF);
        
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
        
        HaTestDef testDef = HaTestDefDao.getInstance().getTestDef(user.getAssignedTestName());
        _test = HaTestDao.getInstance().createTest(user.getUid(), testDef, 1);
        
        return _test;
    }
    
    
    public HaTestRun setupDemoAccountTestRun() throws Exception {
        HaTest test = HaTestDao.getInstance().loadTest(setupDemoAccountTest().getTestId());
        String pids[] = test.getPids().toArray(new String[test.getPids().size()]);
        _testRun = HaTestDao.getInstance().createTestRun(conn, test.getUser().getUid(), test.getTestId(), 0,0,0);
        
        
        /** Write a single record to the whiteboard for this testrun
         * 
         */
        SaveWhiteboardDataAction saveAction = new SaveWhiteboardDataAction(test.getUser().getUid(), _testRun.getRunId(), pids[0], CommandType.DRAW, "{}");
        new SaveWhiteboardDataCommand().execute(conn,saveAction);
        
        return _testRun;
    }
    
    
    public AssessmentPrescription setupDemoAccountPrescription() throws Exception {

        HaTestRun testRun = HaTestRunDao.getInstance().lookupTestRun(setupDemoAccountTestRun().getRunId());
        
        int c =  testRun.getAnsweredCorrect();
        int i = testRun.getAnsweredIncorrect();
        int n = testRun.getHaTest().getNumTestQuestions() - (c +  i);
        _prescription = AssessmentPrescriptionManager.getInstance().createPrescription(conn, testRun.getHaTest().getTestId(), testRun.getPidList(),c,i,n);
        
        return _prescription;
    }
    
    /** Create demo group into demo user
     * 
     * @return
     * @throws Exception
     */
    public GroupInfoModel setupDemoGroup() throws Exception {
        if(_user == null)
            setupDemoAccount();
        
        GroupInfoModel group = new GroupInfoModel();
        group.setGroupName("demo_" + System.currentTimeMillis());
        group.setDescription("Demo Group");
        _groupModel = CmAdminDao.getInstance().addGroup(conn, _user.getAid(), group);
        return _groupModel;
    }
}
