package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.shared.client.rpc.action.AddStudentAction;
import hotmath.testset.ha.CmProgram;

public class AddStudentCommand_Test extends CmDbTestCase {
	
	public AddStudentCommand_Test(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if(_user == null)
			setupDemoAccount();
	}

	
	public void testCreate() throws Exception {
		StudentModelI sm = CmStudentDao.getInstance().getStudentModelBasic(conn, _user.getUid());
		sm.setPasscode("NEW_" + System.currentTimeMillis());
		AddStudentAction action = new AddStudentAction(sm);
		StudentModelI ret = new AddStudentCommand().execute(conn, action);
		assertTrue(ret.getPasscode().equals(sm.getPasscode()));
	}
	
	public void testcreate2() throws Exception {
	    StudentModel student = new StudentModel();
        student.setName("Student");
        student.setPasscode("pwd_" + System.currentTimeMillis());
        student.setAdminUid(0);
        student.setGroupId("1");
        
        StudentProgramModel program = new StudentProgramModel();
        program.setProgramType(CmProgram.PREALG_PROF.getProgramType());
        program.setSubjectId(CmProgram.PREALG_PROF.getSubject());
        student.setProgram(program);
        
        student.getSettings().setTutoringAvailable(false);
        student.getSettings().setShowWorkRequired(false);
        
        AddStudentAction action = new AddStudentAction(student);
        StudentModelI sm = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(sm);
        assertTrue(sm.getUid() > 0);
	}	
}
