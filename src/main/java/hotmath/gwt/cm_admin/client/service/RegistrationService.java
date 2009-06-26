package hotmath.gwt.cm_admin.client.service;

import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistrationService extends RemoteService {

	  List<StudentModel> getSummariesForActiveStudents(Integer adminIUid); 

	  List<StudentModel> getSummariesForInactiveStudents(Integer adminUid);

	  StudentModel deactivateUser(StudentModel sm) throws Exception;
	  
	  void removeUser(StudentModel sm);

	  AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception;

}
