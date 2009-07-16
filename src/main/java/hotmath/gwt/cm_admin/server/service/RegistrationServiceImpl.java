package hotmath.gwt.cm_admin.server.service;


import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class RegistrationServiceImpl extends RemoteServiceServlet implements RegistrationService {

	private static final long serialVersionUID = -1395419758214416432L;
	
	public RegistrationServiceImpl() {
	}

	public List<StudentModel> getSummariesForActiveStudents(Integer adminUid) throws Exception {
		CmStudentDao cma = new CmStudentDao();
		return cma.getSummariesForActiveStudents(adminUid);
	}
	
	public List<StudentModel> getSummariesForInactiveStudents(Integer adminUid) throws Exception {
        CmStudentDao cma = new CmStudentDao();
		return cma.getSummariesForInactiveStudents(adminUid);
	}

	public StudentModel deactivateUser(StudentModel sm) throws Exception {
        CmStudentDao cma = new CmStudentDao();
		return cma.deactivateUser(sm);
	}

	@Deprecated
    public void removeUser(StudentModel sm) {
        CmStudentDao cma = new CmStudentDao();
        cma.removeUser(sm);
    }

	public AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception {
		CmAdminDao cma = new CmAdminDao();
		return cma.getAccountInfo(uid);
	}
}
