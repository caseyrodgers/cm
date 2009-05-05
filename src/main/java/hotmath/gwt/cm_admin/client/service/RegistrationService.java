package hotmath.gwt.cm_admin.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;

public interface RegistrationService extends RemoteService {

	  List<StudyProgramModel> getProgramDefinitions();
	  
	  List<StudentModel> getSummariesForActiveStudents(String adminPasscode); 

	  List<StudentModel> getSummariesForInactiveStudents(String adminPasscode);
	  
	  List<GroupModel> getActiveGroups(String adminPasscode);

	  List<SubjectModel> getSubjectDefinitions();
	  
	  List<StudentActivityModel> getStudentActivity(StudentModel sm);
	  
	  AccountInfoModel getAccountInfo(String adminPasscode);

	  StudentModel addUser(StudentModel sm);

	  StudentModel deactivateUser(StudentModel sm);

	  StudentModel updateUser(StudentModel sm);
}
