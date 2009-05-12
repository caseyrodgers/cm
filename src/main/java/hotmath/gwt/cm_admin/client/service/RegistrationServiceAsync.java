package hotmath.gwt.cm_admin.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;

public interface RegistrationServiceAsync {
	
	  void getProgramDefinitions(AsyncCallback<List<StudyProgramModel>> callback);
	  
	  void getSummariesForActiveStudents(Integer adminUid, AsyncCallback<List<StudentModel>> callback);
	  
	  void getSummariesForInactiveStudents(Integer adminUid, AsyncCallback<List<StudentModel>> callback);
	  
	  void getActiveGroups(Integer adminUid, AsyncCallback<List<GroupModel>> callback);
	  
	  void getSubjectDefinitions(AsyncCallback<List<SubjectModel>> callback);
	  
	  void getStudentActivity(StudentModel sm, AsyncCallback<List<StudentActivityModel>> callback);
	  
	  void getAccountInfo(Integer adminUid, AsyncCallback<AccountInfoModel> callback);
	  
	  void addUser(StudentModel sm, AsyncCallback<StudentModel> callback);
	  
	  void deactivateUser(StudentModel sm, AsyncCallback<StudentModel> callback);
	  
	  void updateUser(StudentModel sm, AsyncCallback<StudentModel> callback);
}
