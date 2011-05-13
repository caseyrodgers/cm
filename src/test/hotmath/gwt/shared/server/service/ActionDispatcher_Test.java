package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.AddGroupAction;
import hotmath.gwt.shared.client.rpc.action.AutoAdvanceUserAction;
import hotmath.gwt.shared.client.rpc.action.CheckUserAccountStatusAction;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountsAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationPreviewAction;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetAutoRegistrationSetupAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizResultsHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;
import hotmath.gwt.shared.client.rpc.action.GetSummariesForActiveStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.client.rpc.action.LogUserInAction;
import hotmath.gwt.shared.client.rpc.action.ProcessLoginRequestAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.shared.client.rpc.action.SetLessonCompletedAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;
import hotmath.testset.ha.HaTestRunDao;

import java.util.List;

public class ActionDispatcher_Test extends CmDbTestCase {
    
    /** @TODO: create temp values for testing
     * 
     */
    static final int TEST_RUN_ID=13351;
    static final int TEST_ID = 2057;
    static final String TEST_PID="genericalg1_2_4_GraphingLinearEquations_5_115";
    
    public ActionDispatcher_Test(String name) {
        super(name);
    }
    
    static int uid;
    @Override    
    protected void setUp() throws Exception {
        
        super.setUp();
        ActionDispatcher.flush();
        
        if(_testRun == null)
            setupDemoAccountTestRun();
        
        uid = _user.getUid();
    }

    public void testSendErrorNotification() throws Exception {
        GetSolutionAction action = new GetSolutionAction(uid, "DOES_NOT_EXIST");
        try {
            SolutionInfo data = ActionDispatcher.getInstance().execute(action);
        }
        catch(Exception e) {
            /** should throw an error */
            
            /** how to know error was reported?
             * 
             * ?send email and check for result..?
             * 
             * ?register message in queue and
             * make sure it was removed when complete?
             * 
             * ?mock up an email server and wait
             * for the email to be sent ...?
             * 
             * 
             */
            return;
        }
        assertTrue("Should never reach here",false);
    }
    
    public void testGetSolution() throws Exception {
        
        GetSolutionAction action = new GetSolutionAction(uid, TEST_PID);
        SolutionInfo data = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(data);
        assertNotNull(data.getHtml());
        assertNotNull(data.getJs());;
    }
    
    public void testLogUserInAction() throws Exception {
        LogUserInAction action = new LogUserInAction(_user.getLoginName(),_user.getPassword(),"test");
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        
        assertTrue(rdata.getDataAsString("key") != null);
        
        action = new LogUserInAction(_user.getLoginName(),"NOT_EXIST-" + System.currentTimeMillis(),"test");
        rdata = ActionDispatcher.getInstance().execute(action);
        
        assertTrue(rdata.getDataAsString("message") != null);

   }

    public void testCheckUserAccountStatusAction() throws Exception {
        CheckUserAccountStatusAction action = new CheckUserAccountStatusAction(_user.getPassword());
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        
        assertTrue(rdata.getDataAsString("message") != null);
    }
    
    public void testSetLessonCompletedCommand() throws Exception {

        String lesson = HaTestRunDao.getInstance().getTestRunLessons(conn, _testRun.getRunId()).get(0).getLesson();
        
        SetLessonCompletedAction action = new SetLessonCompletedAction(lesson,_testRun.getRunId(), 0);
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertNotNull(rdata);
        assertTrue(rdata.getDataAsString("status").equals("OK"));

    }
    public void testGetSummariesForActiveStudentsTest() throws Exception {
        GetSummariesForActiveStudentsAction action = new GetSummariesForActiveStudentsAction(2);
        CmList<StudentModelI> students = ActionDispatcher.getInstance().execute(action);
        assertNotNull(students);
        assertTrue(students.size() > 0);
    }

    
    public void testProcessLoginRequestAction() throws Exception {
        ProcessLoginRequestAction action = new ProcessLoginRequestAction("does_not_exist");
        try {
            UserInfo userInfo = ActionDispatcher.getInstance().execute(action);
            assertNotNull(userInfo);
            assertTrue(userInfo.getUid() > 0);
            assert(false);
        }
        catch(Exception e) {
            /** expected */
        }
    }
    
    public void testGetLessonItemsForTestRunAction() throws Exception {
        GetLessonItemsForTestRunAction action = new GetLessonItemsForTestRunAction(TEST_RUN_ID);
        CmList<LessonItemModel> lessons = ActionDispatcher.getInstance().execute(action);
        assertNotNull(lessons);
        assertTrue(lessons.size() > 0);
        assertTrue(lessons.get(0).getStateStandards().size() > 0);
    }

    public void testGetStateStandards() throws Exception {
        GetStateStandardsAction action = new GetStateStandardsAction("aas-postulate.html","CA");
        CmList<String> list = ActionDispatcher.getInstance().execute(action);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }
    
    public void testCreateAutoRegistrationAccountCommand() throws Exception {
        int AUTO_SETUP_UID=3953;  // is setup as auto_create_template
        
        String user = "user_" + System.currentTimeMillis();
        String password = "password_" + System.currentTimeMillis();
        CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(AUTO_SETUP_UID, user, password);
        RpcData  sm = ActionDispatcher.getInstance().execute(action);
        assertNotNull(sm);
        assertTrue(sm.getDataAsString("key") != null);
    }
    
    public void testAddGroupCommand() throws Exception {
        AddGroupAction action = new AddGroupAction(2, new GroupInfoModel());
        GroupInfoModel group = ActionDispatcher.getInstance().execute(action);
        assertNotNull(group);
    }
    
    
    public void testCreateAutoRegistrationAccountsCommand() throws Exception {
        
        StudentModelI student = CmStudentDao.getInstance().getStudentModelBase(conn, uid);
        AutoRegistrationSetup preview = new AutoRegistrationSetup();
        
        CmList<AutoRegistrationEntry> entries = new CmArrayList<AutoRegistrationEntry>();
        AutoRegistrationEntry entry = new AutoRegistrationEntry();
        entry.setName("TEST-1");
        entry.setPassword("TESTPWD-1");
        entries.add(entry);
        entry = new AutoRegistrationEntry();
        entry.setName("TEST-2");
        entry.setPassword("TESTPWD-2");
        preview.setEntries(entries);
        
        CreateAutoRegistrationAccountsAction action = new CreateAutoRegistrationAccountsAction(2, student,entries);
        AutoRegistrationSetup autoSetup = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(autoSetup);
    }
    
    public void testCreateAutoRegistrationPreviewCommand() throws Exception {
        
        StudentModelI student = CmStudentDao.getInstance().getStudentModelBase(conn, uid);
        
        CreateAutoRegistrationPreviewAction action = new CreateAutoRegistrationPreviewAction(student,"key");
        AutoRegistrationSetup autoSetup = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(autoSetup);
        
        assertTrue(autoSetup.getEntries().size() == 10);
    }

    public void testGetAutoRegistrationSetupCommand() throws Exception {
        GetAutoRegistrationSetupAction action = new GetAutoRegistrationSetupAction(2);
        AutoRegistrationSetup autoSetup = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(autoSetup);
    }
    
    public void testSaveWhiteboardDataCommand() throws Exception {
        SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(uid, TEST_RUN_ID, TEST_PID, CommandType.DRAW, "123");
        RpcData rd = ActionDispatcher.getInstance().execute(action);
        assertNotNull(rd);
    }
    
    public void testClearWhiteboardDataCommand() throws Exception {
        ClearWhiteboardDataAction action = new ClearWhiteboardDataAction(uid, TEST_RUN_ID, TEST_PID);
        RpcData rd = ActionDispatcher.getInstance().execute(action);
        assertNotNull(rd);
    }



    public void testGetQuizHtmlCheckedCommand() throws Exception {
        GetQuizHtmlCheckedAction action = new GetQuizHtmlCheckedAction(TEST_ID);
        RpcData rpcData = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(rpcData);
    }

    
    public void testAutoAdvanceUserCommand() throws Exception {
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(25828);
        AutoUserAdvanced advanced = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(advanced);
        assertNotNull(advanced.getProgramName());
    }

    public void testGetUserInfo() throws Exception {
        GetUserInfoAction action = new GetUserInfoAction(uid,"Testing");
        UserLoginResponse user = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(user);
    }
    
    
    public void testGetQuizResultsHtmlCommand() throws Exception {
        GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(TEST_RUN_ID);
        RpcData rpcData = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(rpcData);
        assertTrue(rpcData.getDataAsString("quiz_result_json").length() > 0);
        
    }
    public void testSaveQuizCurrentResult() throws Exception {
        SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(TEST_ID, true, 1, TEST_PID);
        RpcData rpcData = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(rpcData);
        assertTrue(rpcData.getDataAsString("complete").equals("true"));

    }
    
    public void testGetQuizHtmlChecked() throws Exception {
        GetQuizHtmlCheckedAction action = new GetQuizHtmlCheckedAction(TEST_ID);
        RpcData rpcData = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(rpcData);
        assertTrue(rpcData.getDataAsString("quiz_html").length() > 0);
    }
    
    
    public void testGetQuizHtml() throws Exception {
        GetQuizHtmlAction action = new GetQuizHtmlAction(_test.getTestId());
        QuizHtmlResult result = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(result.getQuizHtml());
    }

    
    public void testCreateTestRun() throws Exception {
        CreateTestRunAction action = new CreateTestRunAction(TEST_ID);
        CreateTestRunResponse userStatus = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(userStatus);
        assertTrue(userStatus.getTotal() > 0);
    }

    
    
    public void testCreateGetInmhViewsForRun() throws Exception {
        
        GetViewedInmhItemsAction action = new GetViewedInmhItemsAction(TEST_RUN_ID);
        List<RpcData> data = ActionDispatcher.getInstance().execute(action).getRpcData();
        
        assertNotNull(data);
        assertTrue(data.size() > 0);
    }

    
    public void testCreatePrescription() throws Exception {
        
        GetPrescriptionAction action = new GetPrescriptionAction(TEST_RUN_ID,0,false);
        PrescriptionSessionResponse data = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(data);
        assertNotNull(data.getProgramTitle());
    }


}
