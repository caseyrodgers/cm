package hotmath.gwt.cm.client.service;

import hotmath.gwt.cm.client.util.CmRpcException;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.util.RpcData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PrescriptionServiceAsync {
	  void getPrescriptionSessionJson(int runId,int sessionNumber,boolean updateActiveInfo, AsyncCallback callback);
	  
	  void getSolutionHtml(String pid, AsyncCallback callback);

	  /** Return the quiz segment
	   * 
	   * @param uid
	   * @param segment the test segment
	   * 
	   * @param callback
	   */
	  void getQuizHtml(int uid, int testSegment, AsyncCallback callback);
	  
	  void getQuizHtml(int testId,AsyncCallback callback);
	  
	  void getHmContent(String file, String baseDirectory, AsyncCallback callback);
	  
	  void getQuizCurrentResults(int testId, AsyncCallback callback);
	  
	  void getSolutionProblemStatementHtml(String pid,AsyncCallback callback);
	  
	  void saveWhiteboardData(int uid, String pid, String command, String commandData, AsyncCallback callback);
	  
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
	  void getUserInfo(int uid, AsyncCallback callBack);
	  
	  
	  
	  /** Create a new test run from this test id
	   * 
	   * @param testId
	   * @param callBack
	   */
	  void createTestRun(int testId, AsyncCallback callBack);
}
