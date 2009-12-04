package hotmath.gwt.cm.server;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.gwt.shared.server.service.command.SaveWhiteboardDataCommand;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;

import java.util.List;

public class CmDbTestCase extends DbTestCase {
    
    public CmDbTestCase(String name) {
        super(name);
    }
    
    protected static HaUser _user;
    protected static HaTest _test;
    protected static HaTestRun _testRun;
    protected static AssessmentPrescription _prescription;
    protected static GroupModel _groupModel;
    
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
        _test = HaTestDao.createTest(conn,user.getUid(), testDef, 1);
        
        return _test;
    }
    
    
    public HaTestRun setupDemoAccountTestRun() throws Exception {
        HaTest test = HaTestDao.loadTest(conn, setupDemoAccountTest().getTestId());
        String pids[] = test.getPids().toArray(new String[test.getPids().size()]);
        _testRun = HaTestDao.createTestRun(conn, test.getUser().getUid(), test.getTestId(), pids,0,0,0);
        
        
        /** Write a single record to the whiteboard for this testrun
         * 
         */
        SaveWhiteboardDataAction saveAction = new SaveWhiteboardDataAction(test.getUser().getUid(), _testRun.getRunId(), pids[0], CommandType.DRAW, "{}");
        new SaveWhiteboardDataCommand().execute(conn,saveAction);
        
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
    
    /** Create demo group into demo user
     * 
     * @return
     * @throws Exception
     */
    public GroupModel setupDemoGroup() throws Exception {
        if(_user == null)
            setupDemoAccount();
        
        GroupModel group = new GroupModel();
        group.setName("demo_" + System.currentTimeMillis());
        group.setDescription("Demo Group");
        _groupModel = new CmAdminDao().addGroup(conn, _user.getAid(), group);
        return _groupModel;
    }
}
