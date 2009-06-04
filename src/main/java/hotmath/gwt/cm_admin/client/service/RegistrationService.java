package hotmath.gwt.cm_admin.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.ChapterModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;

public interface RegistrationService extends RemoteService {

	  List<StudyProgramModel> getProgramDefinitions();
	  
	  List<StudentModel> getSummariesForActiveStudents(Integer adminIUid); 

	  List<StudentModel> getSummariesForInactiveStudents(Integer adminUid);
	  
	  List<GroupModel> getActiveGroups(Integer adminUid);

	  List<SubjectModel> getSubjectDefinitions();
	  
	  List<StudentActivityModel> getStudentActivity(StudentModel sm) throws Exception;
	  
	  List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws Exception;
	  
	  StudentModel addUser(StudentModel sm) throws Exception;

	  StudentModel deactivateUser(StudentModel sm);

	  StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			  Boolean passcodeChanged) throws Exception;
	  
	  AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception;
}
