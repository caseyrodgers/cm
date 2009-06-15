package hotmath.gwt.cm_admin.server.service;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.ChapterModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;
import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


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
	
	public List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws Exception{
		CmAdminDao cma = new CmAdminDao();
		return cma.getChaptersForProgramSubject(progId, subjId);
	}

	public List<StudentActivityModel> getStudentActivity(StudentModel sm) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.getStudentActivity(sm.getUid());
	}
	
	public List<GroupModel> getActiveGroups(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getActiveGroups(adminUid);
	}

	public StudentModel addUser(StudentModel sm) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.addStudent(sm);
	}

	public StudentModel deactivateUser(StudentModel sm) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.deactivateUser(sm);
	}
	
    public void removeUser(StudentModel sm) {
        CmAdminDao cma = new CmAdminDao();
        cma.removeUser(sm);
    }
	

	public StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			Boolean passcodeChanged) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.updateStudent(sm, stuChanged, progChanged, progIsNew, passcodeChanged);
	}

	public AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.getAccountInfo(uid);
	}

	public GroupModel addGroup(Integer adminUid, GroupModel gm) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.addGroup(adminUid, gm);
	}
	
	public List<StudentShowWorkModel> getStudentShowWork(Integer uid, Integer runId) throws Exception {
	       CmAdminDao cma = new CmAdminDao();
	       return cma.getStudentShowWork(uid, runId);
	}
}
