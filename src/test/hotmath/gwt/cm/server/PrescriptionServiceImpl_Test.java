package hotmath.gwt.cm.server;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.action.AddStudentAction;
import hotmath.gwt.shared.client.rpc.action.AutoAdvanceUserAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.testset.ha.CmProgram;


public class PrescriptionServiceImpl_Test extends CmDbTestCase {
	
	
	public PrescriptionServiceImpl_Test(String name) {
		super(name);
	}
	
	
	   
    public void testGetQuizHtmChapterTitleNumber() throws Exception {
        int uid = setupDemoAccount();
        
        CmStudentDao dao = new CmStudentDao();
        dao.assignProgramToStudent(conn,uid,CmProgram.PREALG_CHAP,"Integers");
        GetQuizHtmlAction  action = new GetQuizHtmlAction(uid, 1);
        RpcData data = ActionDispatcher.getInstance().execute(action);
        
        String chapter = data.getDataAsString("sub_title");
        assertTrue(chapter.indexOf("Integers") > -1);
    }

	public void testUpdateStudent() throws Exception {

	    int uid = setupDemoAccount();
	    
	    StudentModel sm = new CmStudentDao().getStudentModel(uid);
	    sm.setBackgroundStyle("test");
	    
        UpdateStudentAction action = new UpdateStudentAction(sm, true, false, false, false);
        StudentModel smRet = ActionDispatcher.getInstance().execute(action);
        
        assertTrue(smRet != null);
        assertTrue(smRet.getBackgroundStyle().equals("test"));
	}
	
	public void testAddStudent() throws Exception {
	
	    
        StudentModel student = new StudentModel();
        student.setName("Student");
        student.setPasscode("pwd_" + System.currentTimeMillis());
        student.setAdminUid(0);
        student.setGroupId("1");
        student.setProgId(CmProgram.PREALG_PROF.getProgramId());
        student.setSubjId(CmProgram.PREALG_PROF.getSubject());
        student.setTutoringAvail(false);
        student.setShowWorkRequired(false);
        
        AddStudentAction action = new AddStudentAction(student);
        StudentModel sm = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(sm);
        assertTrue(sm.getUid() > 0);
	}
	


	public void testAutoAdvance() throws Exception {
	    
	    int userId = setupDemoAccount();
	    
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(userId);
        AutoUserAdvanced advanced = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(advanced);
	}
	
	
	public void testGetSubjectDefinitions() throws Exception {
	    GetProgramDefinitionsAction action = new GetProgramDefinitionsAction("Chap");
	    CmList<SubjectModel> sml = ActionDispatcher.getInstance().execute(action);
        assertNotNull(sml);
       // assertTrue(sml.size() > 0);
	}

}
