package hotmath.gwt.cm_admin.server.service;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;
import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;


public class RegistrationServiceImpl extends RemoteServiceServlet implements RegistrationService {

	private static final long serialVersionUID = -1395419758214416432L;
	
	public RegistrationServiceImpl() {
	}

	public List<StudyProgramModel> getProgramDefinitions() {	
		CmAdminDao cma = new CmAdminDao();
		return cma.getProgramDefinitions();
	}

	public List<StudentModel> getSummariesForActiveStudents(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getSummariesForActiveStudents(adminUid);
	}
	
	public List<StudentModel> getSummariesForInactiveStudents(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getSummariesForInactiveStudents(adminUid);
	}
	
	public List<SubjectModel> getSubjectDefinitions() {
		CmAdminDao cma = new CmAdminDao();
		return cma.getSubjectDefinitions();
	}

	public List<StudentActivityModel> getStudentActivity(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getStudentActivity(sm.getName());
	}
	
	public List<GroupModel> getActiveGroups(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getActiveGroups(adminUid);
	}

	public AccountInfoModel getAccountInfo(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getAccountInfo(adminUid);
	}

	public StudentModel addUser(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.addStudent(sm);
	}

	public StudentModel deactivateUser(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.deactivateUser(sm);
	}

	public StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew) {
		CmAdminDao cma = new CmAdminDao();
		return cma.updateStudent(sm, stuChanged, progChanged, progIsNew);
	}
}
