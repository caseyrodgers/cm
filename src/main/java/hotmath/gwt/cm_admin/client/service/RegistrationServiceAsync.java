package hotmath.gwt.cm_admin.client.service;

import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RegistrationServiceAsync {

    void getSummariesForActiveStudents(Integer adminUid, AsyncCallback<List<StudentModel>> callback);

	void getSummariesForInactiveStudents(Integer adminUid, AsyncCallback<List<StudentModel>> callback);

    void deactivateUser(StudentModel sm, AsyncCallback<StudentModel> callback);

    void getAccountInfoForAdminUid(Integer uid, AsyncCallback<AccountInfoModel> callback);
}
