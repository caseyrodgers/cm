package hotmath.gwt.cm_admin.server.service;

import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.util.CmRpcExceptionUseAction;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RegistrationServiceImpl extends RemoteServiceServlet implements RegistrationService {

	private static final long serialVersionUID = -1395419758214416432L;
	
	public RegistrationServiceImpl() {
	}

	public List<StudentModel> getSummariesForActiveStudents(Integer adminUid) throws Exception {
		throw new CmRpcExceptionUseAction();
	}
	
	public List<StudentModel> getSummariesForInactiveStudents(Integer adminUid) throws Exception {
		CmStudentDao dao = new CmStudentDao();
		return dao.getSummariesForInactiveStudents(adminUid);
	}

	public AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception {
		CmAdminDao dao = new CmAdminDao();
		return dao.getAccountInfo(uid);
	}
}