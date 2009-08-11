package hotmath.gwt.cm_admin.client.service;

import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistrationService extends RemoteService {

    List<StudentModel> getSummariesForActiveStudents(Integer adminIUid) throws Exception; 

    List<StudentModel> getSummariesForInactiveStudents(Integer adminUid) throws Exception;

    StudentModel deactivateUser(StudentModel sm) throws Exception;

    AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception;
}
