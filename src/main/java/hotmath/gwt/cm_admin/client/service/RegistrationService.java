package hotmath.gwt.cm_admin.client.service;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.ChapterModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface RegistrationService extends RemoteService {

	  List<StudyProgramModel> getProgramDefinitions();
	  
	  List<StudentModel> getSummariesForActiveStudents(Integer adminIUid); 

	  List<StudentModel> getSummariesForInactiveStudents(Integer adminUid);
	  
	  List<GroupModel> getActiveGroups(Integer adminUid);

	  GroupModel addGroup(Integer adminUid, GroupModel gm) throws Exception;

	  List<SubjectModel> getSubjectDefinitions();
	  
	  List<StudentActivityModel> getStudentActivity(StudentModel sm) throws Exception;
	  
	  List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws Exception;
	  
	  StudentModel addUser(StudentModel sm) throws Exception;

	  StudentModel deactivateUser(StudentModel sm) throws Exception;
	  
	  void removeUser(StudentModel sm);

	  StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			  Boolean passcodeChanged) throws Exception;
	  
	  AccountInfoModel getAccountInfoForAdminUid(Integer uid) throws Exception;
	  
	  /** Get this students ShowWork problems.  Restrict to named runId if non null
	   * 
	   * @param uid The student uid
	   * @param runId  The runId of interest, or null for all
	   * @return
	   * @throws Exception
	   */
	  List<StudentShowWorkModel> getStudentShowWork(Integer uid, Integer runId) throws Exception;
}
