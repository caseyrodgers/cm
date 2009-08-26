package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.action.AddGroupAction;
import hotmath.gwt.shared.client.rpc.action.AutoAdvanceUserAction;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountsAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationPreviewAction;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetAutoRegistrationSetupAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizResultsHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.client.rpc.action.ProcessLoginRequestAction;
import hotmath.gwt.shared.client.rpc.action.SaveQuizCurrentResultAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

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
        ActionDispatcher.flush();
        
        if(uid == 0)
            uid = setupDemoAccount();
    }
    
    public void testProcessLoginRequestAction() throws Exception {
        ProcessLoginRequestAction action = new ProcessLoginRequestAction("cm_1249959105808");
        UserInfo userInfo = ActionDispatcher.getInstance().execute(action);
        assertNotNull(userInfo);
        assertTrue(userInfo.getUid() > 0);
    }
    
    public void testGetLessonItemsForTestRunAction() throws Exception {
        GetLessonItemsForTestRunAction action = new GetLessonItemsForTestRunAction(TEST_RUN_ID);
        CmList<LessonItemModel> lessons = ActionDispatcher.getInstance().execute(action);
        assertNotNull(lessons);
        assertTrue(lessons.size() > 0);
        assertTrue(lessons.get(0).getStateStandards().size() > 0);
    }

    public void testGetStateStandards() throws Exception {
        GetStateStandardsAction action = new GetStateStandardsAction("aas-postulate.html");
        CmList<String> list = ActionDispatcher.getInstance().execute(action);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }
    
    public void testCreateAutoRegistrationAccountCommand() throws Exception {
        int AUTO_SETUP_UID=3953;  // is setup as auto_create_template
        
        String user = "user_" + System.currentTimeMillis();
        String password = "password_" + System.currentTimeMillis();
        CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(AUTO_SETUP_UID, user, password);
        UserInfo sm = ActionDispatcher.getInstance().execute(action);
        assertNotNull(sm);
        assertTrue(sm.getUid() > 0);
    }
    
    public void testAddGroupCommand() throws Exception {
        AddGroupAction action = new AddGroupAction(2, new GroupModel());
        GroupModel group = ActionDispatcher.getInstance().execute(action);
        assertNotNull(group);
    }
    
    
    public void testCreateAutoRegistrationAccountsCommand() throws Exception {
        
        StudentModel student = new CmStudentDao().getStudentModel(uid);
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
        
        StudentModel student = new CmStudentDao().getStudentModel(uid);
        
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
        SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(uid, TEST_RUN_ID, TEST_PID, "draw", "123");
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
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(uid);
        AutoUserAdvanced advanced = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(advanced);
        assertNotNull(advanced.getProgramName());
    }

    public void testGetUserInfo() throws Exception {
        GetUserInfoAction action = new GetUserInfoAction(uid);
        UserInfo user = ActionDispatcher.getInstance().execute(action);
        
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
        GetQuizHtmlAction action = new GetQuizHtmlAction(uid,1);
        RpcData rpcData = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(rpcData);
        assertTrue(rpcData.getDataAsInt("test_id") > 0);
    }

    
    public void testCreateTestRun() throws Exception {
        CreateTestRunAction action = new CreateTestRunAction(TEST_ID);
        RpcData rData = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(rData);
        assertTrue(rData.getDataAsInt("total_questions") > 0);
    }

    
    
    public void testCreateGetInmhViewsForRun() throws Exception {
        
        GetViewedInmhItemsAction action = new GetViewedInmhItemsAction(TEST_RUN_ID);
        List<RpcData> data = ActionDispatcher.getInstance().execute(action).getRpcData();
        
        assertNotNull(data);
        assertTrue(data.size() > 0);
    }

    
    public void testCreatePrescription() throws Exception {
        
        GetPrescriptionAction action = new GetPrescriptionAction(TEST_RUN_ID,0,false);
        RpcData data = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(data);
        assertNotNull(data.getData("program_title"));
    }
  
    public void testGetSolution() throws Exception {
        
        GetSolutionAction action = new GetSolutionAction(uid, TEST_PID);
        RpcData data = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(data);
        assertNotNull(data.getData("solutionHtml"));
        assertNotNull(data.getData("hasShowWork"));
    }

}
