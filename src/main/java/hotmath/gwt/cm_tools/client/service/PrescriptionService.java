package hotmath.gwt.cm_tools.client.service;

import hotmath.gwt.cm_tools.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;

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
	String getSolutionHtml(String pid);
	
	
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
	RpcData getQuizHtml(int uid, int testSegment);
	
	
    RpcData getQuizHtml(int testId) throws CmRpcException;

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
	 * @param pid
	 * @param json
	 * @throws CmRpcException
	 */
	void saveWhiteboardData(int uid, String pid, String command, String commandData) throws CmRpcException;
	

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
}
