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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface PrescriptionService extends RemoteService {
    
    /** Save any feedback for the current state
     * 
     * @param comments
     * @param commentsUrl
     * @param stateInfo
     * @throws CmRpcException
     */
    void saveFeedback(String comments, String commentsUrl, String stateInfo) throws CmRpcException;
    

	/**
	 * Return the JSON representing this prescription
	 * 
	 * @param pids
	 * @param sessionNumber
	 * @param should the active state of user be updated?
	 * 
	 * @return JSON string that represents this prescription session
	 */
	RpcData getPrescriptionSessionJson(int runId, int sessionNumber,boolean updateActiveInfo) throws CmRpcException;

	
	
	/** return the entire solution HTML that can be inserted into existing div 
	 * 
	 * @param pid
	 * @return
	 */
	RpcData getSolutionHtml(int userId, String pid) throws CmRpcException;
	
	
	/** return just the problem statement as embeddable HTML
	 *  
	 * @param pid
	 * @return
	 */
	String getSolutionProblemStatementHtml(String pid);
	

	/**
	 * Return the teset quiz HTML. This will be an entire section of nested div
	 * elements containing the test problem definitions.
	 * 
	 * @param uid
	 * @param testSegment
	 * @return
	 */
	RpcData getQuizHtml(int uid, int testSegment) throws CmRpcException;
	
	
    RpcData getQuizHtmlChecked(int testId) throws CmRpcException;

	/**
	 * Return the 'hm_content' from the requested file
	 * 
	 * @param file
	 * @param baseDirectory The directory relative files should be made absolute to.
	 * @return
	 */
	String getHmContent(String file, String baseDirectory);

	/**
	 * Return the current results for the given test
	 * 
	 * @param testId
	 * @return Method used to get a list of contacts
	 * @return ArrayList of Contact objects
	 * 
	 */
	ArrayList<RpcData> getQuizCurrentResults(int testId) throws CmRpcException;
	
	
	
	/** Save a single quiz queston selection 
	 * 
	 * @param testId
	 * @param correct
	 * @param answerIndex
	 * @param pid
	 * @throws CmRpcException
	 */
	void saveQuizCurrentResult(int testId, boolean isCorrect, int answerIndex, String pid) throws CmRpcException;
	
	
	
	/** Save whiteboard data for this user
	 * 
	 * @param uid
	 * @param runId
	 * @param pid
	 * @param json
	 * @throws CmRpcException
	 */
	void saveWhiteboardData(int uid, int runId, String pid, String command, String commandData) throws CmRpcException;
	

	/** Get the list of whiteboard commands for this user/pid
	 * 
	 * @param uid
	 * @param pid
	 * @return
	 * @throws CmRpcException
	 */
	ArrayList<RpcData>   getWhiteboardData(int uid, String pid) throws CmRpcException;
	
	
	
	/** Return array of of viewed inmh items
	 * 
	 * @param uid
	 * @return
	 * @throws CmRpcException
	 */
	ArrayList<RpcData> getViewedInmhItems(int runId) throws CmRpcException;
	
	
	/** Mark this item as being viewed
	 * 
	 * @param runId
	 * @param type
	 * @param file
	 * @throws CmRpcException
	 */
	void setInmhItemAsViewed(int runId, String type, String file) throws CmRpcException;
	
	
	
	RpcData getUserInfo(int uid) throws CmRpcException;
	
	
	/** Create a new test run for this test id
	 * 
	 *  return the run_id+meta
	 * 
	 * @param testId
	 * @param callBack
	 * @return
	 */
	RpcData createTestRun(int testId) throws CmRpcException;
	
	
	
	/** Reset the current user's path through CM
	 * 
	 * @param userId
	 * @throws CmRpcException
	 */
	void resetUser(int userId) throws CmRpcException;
	
	
	/** Returns the quiz with current results marked
	 * 
	 * rpcData: quiz_html, title
	 * 
	 * @param runId
	 * @return
	 * @throws CmRpcException
	 */
	RpcData getQuizResultsHtml(int runId) throws CmRpcException;
	
	
	/** Set this users background style image (in CatchupMath.css) 
	 * 
	 * @param userId
	 * @param backgroundStyle
	 * @throws CmRpcException
	 */
	void setUserBackground(int userId, String backgroundStyle) throws CmRpcException;
	
	
    /** Get this students ShowWork problems.  Restrict to named runId if non null
     * 
     * @param uid The student uid
     * @param runId  The runId of interest, or null for all
     * @return
     * @throws Exception
     */
    List<StudentShowWorkModel> getStudentShowWork(Integer uid, Integer runId) throws CmRpcException;
    

    /** return the list of activities for the specified Student 
     * 
     * @param sm The student model
     * @return
     * @throws Exception
     */
    List<StudentActivityModel> getStudentActivity(StudentModel sm) throws CmRpcException;

    /** return the list of Lesson Items for the specified Test Run Id 
     * 
     * @param run The test run id
     * @return
     * @throws Exception
     */
    List<LessonItemModel> getLessonItemsForTestRun(Integer runId) throws CmRpcException;
    
    /** Create StudentModel for user with matching user_id
     * 
     * @param user_id
     * @return
     * @throws CmRpcException
     */
    StudentModel getStudentModel(Integer user_id) throws CmRpcException;

    /** Read all defined program definitions
     * 
     * @return
     */
    List<StudyProgramModel> getProgramDefinitions() throws CmRpcException;
    
    
    /** Return all defined subject definitions
     * 
     * @return
     */
    List<SubjectModel> getSubjectDefinitions(String progId) throws CmRpcException;
    

    /** Return list of all active groups defined for this admin
     * 
     * @param adminUid
     * @return
     */
    List<GroupModel> getActiveGroups(Integer adminUid) throws CmRpcException;

    
    
    /** Add user defined in StudentModel
     * 
     * @param sm
     * @return
     * @throws Exception
     */
    StudentModel addUser(StudentModel sm) throws CmRpcException;
    

    /** Update user information for student defined in model
     * 
     * @param sm
     * @param stuChanged
     * @param progChanged
     * @param progIsNew
     * @param passcodeChanged
     * @return
     * @throws Exception
     */
    StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
            Boolean passcodeChanged) throws Exception;
    
    
    /** Return all chapters defined for this program and subject
     * 
     * @param progId
     * @param subjId
     * @return
     * @throws CmRpcException
     */
    List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws CmRpcException;
 
    
    

    /** Return the Group defined for this admin matching group model
     * 
     * @param adminUid
     * @param gm
     * @return
     * @throws Exception
     */
    GroupModel addGroup(Integer adminUid, GroupModel gm) throws CmRpcException;
    
    
    /** Automatically advance user to next logical program
     * 
     * @param userId
     * @return
     * @throws CmRpcException
     */
    AutoUserAdvanced autoAdvanceUser(Integer userId) throws CmRpcException;
}
