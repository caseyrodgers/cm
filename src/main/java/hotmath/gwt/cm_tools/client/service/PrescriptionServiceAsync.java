package hotmath.gwt.cm_tools.client.service;

import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PrescriptionServiceAsync {
    
    /** Save any context comments
     * 
     * @param comments
     * @param commentsUrl
     * @param stateInfo
     * @param callback
     */
      void saveFeedback(String comments, String commentsUrl, String stateInfo, AsyncCallback callback);
    
	  void getPrescriptionSessionJson(int runId,int sessionNumber,boolean updateActiveInfo, AsyncCallback <RpcData> callback);
	  
	  
	  /** Return the solution HTML for the given pid, and lookup if
	   *  this user has any show work.  (piggyback ?)
	   *  
	   * @param userId
	   * @param pid
	   * @param callback
	   */
	  void getSolutionHtml(int userId, String pid, AsyncCallback callback);

	  /** Return the quiz segment
	   * 
	   * @param uid
	   * @param segment the test segment
	   * 
	   * @param callback
	   */
	  void getQuizHtml(int uid, int testSegment, AsyncCallback callback);
	  
	  
	  void getQuizHtmlChecked(int testId,AsyncCallback callback);
	  
	  void getHmContent(String file, String baseDirectory, AsyncCallback<RpcData> callback);
	  
	  void getQuizCurrentResults(int testId, AsyncCallback callback);
	  
	  void getSolutionProblemStatementHtml(String pid,AsyncCallback callback);
	  
	  void saveWhiteboardData(int uid, int runId, String pid, String command, String commandData, AsyncCallback callback);
	  
	  void getWhiteboardData(int uid, String pid, AsyncCallback callback);
	  
	  void saveQuizCurrentResult(int testId, boolean isCorrect, int answerIndex, String pid,AsyncCallback callback);
	  
	  
	  /** Get list of viewed items 
	   * 
	   * @param runId
	   * @param callback
	   */
	  void getViewedInmhItems(int runId, AsyncCallback callback);
	  
	  
	  /** Mark this item as viewed
	   * 
	   * @param runId
	   * @param type
	   * @param file
	   * @param callBack
	   */
	  void setInmhItemAsViewed(int runId, String type, String file, AsyncCallback callBack);
	  
	  
	  /** Return user information matching uid 
	   * 
	   * @param uid
	   * @param callBack
	   * @return
	   * @throws CmRpcException
	   */
	  void getUserInfo(int uid, AsyncCallback<UserInfo> callBack);
	  

	  
	  /** Reset this user's program and their current state information. 
	   * 
	   * @param userId
	   * @param callback
	   */
	  void resetUser(int userId, AsyncCallback callback);
	  
	  
	  /** Return quiz with results displayed
	   * 
	   * @param runid
	   * @param callback
	   */
	  void getQuizResultsHtml(int runid, AsyncCallback callback);
	  
	  
	  /** Sets the users custom background style */
	  void setUserBackground(int userId, String backgroundStyle, AsyncCallback callback);
	  
	  
	  /** Get information about this student's show work
	   * 
	   * @param uid The user id
	   * @param runId the run id
	   * @param callback
	   */
	  void getStudentShowWork(Integer uid,Integer runId,AsyncCallback<List<StudentShowWorkModel>> callback);
	  

	  /** Get the student activity for this student
	   * 
	   * @param sm the Student model
	   * @param callback
	   */
      void getStudentActivity(StudentModel sm, AsyncCallback<List<StudentActivityModel>> callback);
      
      /** get the Lesson Items for the specified Test Run Id 
       * 
       * @param run the test run id
	   * @param callback
       */
      void getLessonItemsForTestRun(Integer runId, AsyncCallback<List<LessonItemModel>> callback);

      /** Lookup and create a StudentModel for the specified uid
       * 
       * @param uid
       * @param callBack
       */
      void getStudentModel(Integer uid, AsyncCallback<StudentModel> callBack);

      /** Get list of all current Program Definitions
       * 
       * @param callback
       */
      void getProgramDefinitions(AsyncCallback<List<StudyProgramModel>> callback);
      

      /** Return subject definitions for specified Program
       * 
       * @param progId
       * @param callback
       */
      void getSubjectDefinitions(String progId, AsyncCallback<List<SubjectModel>> callback);

      
      /** Return all active groups defined for user
       * 
       * @param adminUid
       * @param callback
       */
      void getActiveGroups(Integer adminUid, AsyncCallback<List<GroupModel>> callback);
      

      /** Add user defined in StudentMode
       * 
       * @param sm
       * @param callback
       */
      void addUser(StudentModel sm, AsyncCallback<StudentModel> callback);
      

      /** Update user named in model
       * 
       * @param sm
       * @param stuChanged
       * @param progChanged
       * @param progIsNew
       * @param passcodeChanged
       * @param callback
       */
      void updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
              Boolean passcodeChanged, AsyncCallback<StudentModel> callback);


      /** Get all changes defined for program with subject
       * 
       * @param progId
       * @param subjId
       * @param callback
       */
      void getChaptersForProgramSubject(String progId, String subjId, AsyncCallback<List<ChapterModel>> callback);


      /** Add named group to list groups available for this admin 
       * 
       * @param adminUid
       * @param gm
       * @param callback
       */
      void addGroup(Integer adminUid, GroupModel gm, AsyncCallback<GroupModel> callback);
      
      
      
      void autoAdvanceUser(Integer userId, AsyncCallback<AutoUserAdvanced> advanced);
}

