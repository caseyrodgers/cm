package hotmath.gwt.cm_admin.client.service;

import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RegistrationServiceAsync {
	
	  void getProgramDefinitions(AsyncCallback<List<StudyProgramModel>> callback);
	  
      void getSummariesForActiveStudents(Integer adminUid, AsyncCallback<List<StudentModel>> callback);
	  
	  void getSummariesForInactiveStudents(Integer adminUid, AsyncCallback<List<StudentModel>> callback);
	  
	  void getActiveGroups(Integer adminUid, AsyncCallback<List<GroupModel>> callback);
	  
	  void addGroup(Integer adminUid, GroupModel gm, AsyncCallback<GroupModel> callback);
	  
	  void getSubjectDefinitions(AsyncCallback<List<SubjectModel>> callback);
	  
	  void getChaptersForProgramSubject(String progId, String subjId, AsyncCallback<List<ChapterModel>> callback);
	  
	  void addUser(StudentModel sm, AsyncCallback<StudentModel> callback);
	  
	  void deactivateUser(StudentModel sm, AsyncCallback<StudentModel> callback);
	  
	  void removeUser(StudentModel sm, AsyncCallback<StudentModel> callback);
	  
	  void updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			  Boolean passcodeChanged, AsyncCallback<StudentModel> callback);

	  void getAccountInfoForAdminUid(Integer uid, AsyncCallback<AccountInfoModel> callback);
}
