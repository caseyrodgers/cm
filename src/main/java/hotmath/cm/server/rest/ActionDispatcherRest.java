package hotmath.cm.server.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.google.gson.Gson;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.server.model.CmPaymentDao;
import hotmath.cm.server.model.DeviceStorage;
import hotmath.cm.server.model.QuizSelection;
import hotmath.cm.test.HaTestSet;
import hotmath.cm.test.HaTestSetQuestion;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_core.client.model.TopicResource;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2MobileUser;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemAnnotation;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction.ResetType;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.mathml.MathMlTransform;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;
import sb.util.SbFile;

@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ActionDispatcherRest {

	@POST
	@GET
	@Path("/user/{userId}/program/current")
	public String getUserProgram(@PathParam("userId") int userId) throws Exception {
		return Cm2ActionManager.getUserCurrentProgram(userId);
	}
	
	@POST
	@GET
	@Path("/user/{userId}/program/load")
	public String loadUserProgram(@PathParam("userId") int userId, String subject) throws Exception {
		return Cm2ActionManager.loadUserProgram(userId, subject);
	}
	
	@POST
	@GET
	@Path("/user/{userId}/program/purchase")
	public String purchaseProgram(@PathParam("userId") int userId, String purchaseJson) throws Exception {
		CmPaymentDao.PurchaseData purchase = new Gson().fromJson(purchaseJson, CmPaymentDao.PurchaseData.class);
		CmPaymentDao.getInstance().addPurchase(userId, purchase);
		return new Gson().toJson(new RpcData("status=OK"));
	}
	
	@POST
	@GET
	@Path("/user/{userId}/program/purchaseRestore")
	public String purchaseProgramRestoreAll(@PathParam("userId") final int userId, final String dataJson) throws Exception {
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				CmPaymentDao.PurchasedDataRestore purchases[] = new Gson().fromJson(dataJson, CmPaymentDao.PurchasedDataRestore[].class);
				CmPaymentDao.getInstance().restorePurchases(userId, purchases);
				return "OK";
			}
		});
	}
	
	
	
	
	@POST
	@GET
	@Path("/user/{userId}/reset_user")
	public String resetCurrentUser(@PathParam("userId") int userId) throws Exception {
		Cm2ActionManager.resetCurrentUser(userId);
		return new Gson().toJson(new RpcData("status=OK"));
	}
	
	@POST
	@GET
	@Path("/user/{userId}/refresh_user")
	public String refresCurrentUser(@PathParam("userId") int userId) throws Exception {
		return Cm2ActionManager.refreshUser(userId);
	}


	/** @deprecated
	 * 
	 * @param deviceId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/login/user/mobile")
	public String loginMobileUser(String deviceId) throws Exception {
		return Cm2ActionManager.loginUserMobile(deviceId, null);
	}
	
	@POST
	@Path("/login/user/mobile/{deviceId}")
	public String loginMobileUser2(@PathParam("deviceId") final String deviceId, final String version) throws Exception {
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				return Cm2ActionManager.loginUserMobile(deviceId, version);
			}
		});
	}

	@POST
	@Path("/login/user")
	public String loginUser(String userInfo) throws Exception {
		JSONObject jo = new JSONObject(userInfo);
		String un = jo.getString("user");
		String pwd = jo.getString("pass");
		int uid = jo.getInt("uid");
		String subject = jo.getString("subject");
		String token = jo.has("device") ? jo.getString("device") : null;

		return Cm2ActionManager.loginUser(uid, un, pwd, subject, token);
	}
	
	
	@POST
	@Path("/login/school/user")
	public String loginSchoolUser(final String userInfo) throws Exception {
		
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				JSONObject jo = new JSONObject(userInfo);
				Cm2MobileUser userLogin=null;
				if(jo.has("uid")) {
					int uid = jo.getInt("uid");
					userLogin = Cm2ActionManager.loginSchoolByUid(uid);
				}
				else {
					String un = jo.getString("user");
					String pwd = jo.getString("pass");
					String subject = jo.has("subject")?jo.getString("subject"):null;
					userLogin = Cm2ActionManager.loginSchoolUser(un, pwd, subject);
				}
				return new Gson().toJson(userLogin);
			}
		});
	}
	
	@POST
	@GET
	@Path("/quiz/{testId}/set_all_correct")
	public String setAllQuizQuestionsCorrect(@PathParam("testId") int testId) throws Exception {
		return doAllQuizQuestionsCorrect(testId);
	}

	
	private String doAllQuizQuestionsCorrect(int testId) throws Exception {
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			HaTest test =  HaTestDao.getInstance().loadTest(testId);
			HaTestSet testSet = new HaTestSet(conn,test.getPids());
            for(HaTestSetQuestion q: testSet.getQuestions()) {
            	int correctAnswer = q.getCorrectAnswer();
            	
            	Cm2ActionManager.setQuizAnswer(testId, q.getProblemIndex(), correctAnswer, true);
            }			
		} 
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
		
		return new Gson().toJson(new RpcData("status=OK"));
	}
	
	@POST
	@GET
	@Path("/quiz/{testId}/check")
	public String checkQuiz(@PathParam("testId") int testId, String params) throws Exception {
		return doCheckQuiz(testId, params);
	}

	private String doCheckQuiz(int testId, String json) throws Exception {

		boolean isAutoTestMode = false;

		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			
			HaTest test =  HaTestDao.getInstance().loadTest(testId);
			HaTestSet testSet = new HaTestSet(conn,test.getPids());
			JSONObject dataO = null;
			if (json != null && json.length() > 0) {
				JSONObject jo = new JSONObject(json);
				isAutoTestMode = jo.has("testMode") ? jo.getBoolean("testMode") : false;

				dataO = jo.getJSONObject("data");
				 JSONArray sel = dataO.getJSONArray("selections");
				 
				 List<QuizSelection> selections = new ArrayList<QuizSelection>();
				 for(int i=0;i<sel.length();i++) {
					 JSONObject o = sel.getJSONObject(i);
					 String pid = o.getString("pid");
					 int choice = o.getInt("choice");
					 boolean isCorrect = testSet.isCorrect(pid, choice);
					 QuizSelection selection = new QuizSelection(pid, choice,isCorrect);
					 
					 Cm2ActionManager.setQuizAnswer(testId, selection.getPid(), selection.getChoice(), selection.isCorrect());
				 }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
		
		return Cm2ActionManager.checkQuiz(testId, isAutoTestMode);
	}


	@POST
	@GET
	@Path("/prescription/{runId}/topic/{topicIndex}")
	public String getPrescription(@PathParam("runId") int runId, @PathParam("topicIndex") int topicIndex)
			throws Exception {
		return Cm2ActionManager.getPrescriptionTopic(runId, topicIndex);
	}

	@POST
	@Path("/prescription/{runId}/topic/{topicIndex}/resource")
	// /prescription/3545220/topic/1/resource/practice/7
	public String getPrescriptionResource(@PathParam("runId") int runId, @PathParam("topicIndex") int topicIndex,
			String json) throws Exception {
		TopicResource resource = (TopicResource) JsonReader.jsonToJava(json);
		return Cm2ActionManager.getPrescriptionResource(resource.getFile());
	}

	@POST
	@Path("/solution/{pid}")
	// /prescription/3545220/topic/1/resource/practice/7
	public String getSolution(@PathParam("pid") String pid) throws Exception {
		return Cm2ActionManager.getSolution(pid);
	}

	@POST
	@Path("/prescription/{rid}/solution/{pid}")
	// /prescription/3545220/topic/1/resource/practice/7
	public String getSolutionForTestRun(@PathParam("rid") int rid, @PathParam("pid") String pid) throws Exception {
		return Cm2ActionManager.getSolution(rid, pid);
	}

	@POST
	@Path("/run/{rid}/resource/view")
	public String markResourceAsViewed(@PathParam("rid") int rid, String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		int topicIndex = jo.getInt("topicIndex");
		String type = jo.getString("type");
		String file = jo.getString("file");

		return Cm2ActionManager.markResourceAsViewed(rid, topicIndex, type, file);
	}

	@POST
	@Path("/quiz/{tid}/answer")
	public String setQuizAnswer(@PathParam("tid") int tid, String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		int choiceIndex = jo.getInt("choiceIndex");
		String pid = jo.getString("pid");
		boolean isCorrect = jo.getBoolean("isCorrect");

		return Cm2ActionManager.setQuizAnswer(tid, pid, choiceIndex, isCorrect);
	}

	@POST
	@Path("/prescription/{rid}/context/{pid}")
	public String setPrescriptionSolutionContext(@PathParam("rid") int rid, @PathParam("pid") String pid,
			String context) throws Exception {
		return Cm2ActionManager.setPrescriptionSolutionContext(rid, pid, context);
	}

	@POST
	@Path("/prescription/{rid}/solution/{pid}/widget")
	public String saveTutorInputWidgetAnswer(@PathParam("rid") int rid, @PathParam("pid") String pid, String jsonData)
			throws Exception {
		JSONObject jo = new JSONObject(jsonData);

		int uid = jo.getInt("uid");
		String value = jo.getString("value");
		boolean isCorrect = jo.getBoolean("isCorrect");

		return Cm2ActionManager.saveTutorInputWidgetAnswer(uid, rid, pid, value, isCorrect);
	}

	@POST
	@Path("/prescription/{rid}/solution/{pid}/whiteboard/{uid}")
	public String getPrescriptionSolutionWhiteboard(@PathParam("rid") int rid, @PathParam("pid") String pid,
			@PathParam("uid") int uid) throws Exception {
		return Cm2ActionManager.getPrescriptionSolutionWhiteboard(uid, rid, pid);
	}

	@POST
	@Path("/whiteboard/write")
	public String writePrescriptionSolutionWhiteboard(String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		int uid = jo.getInt("uid");
		int rid = jo.getInt("rid");
		int tid = jo.getInt("tid");
		String pid = jo.getString("pid");
		String cmdType = jo.getString("type");
		String json = jo.getString("json");

		return Cm2ActionManager.savePrescriptionSolutionWhiteboard(uid, tid, rid, pid, cmdType, json);
	}

	@POST
	@Path("/search")
	public String getSearchResults(String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		int uid = jo.getInt("uid");
		String search = jo.getString("searchFor");
		return Cm2ActionManager.getSearchResults(uid, search);
	}

	@POST
	@Path("/search/topic")
	public String doSearchLesson(String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		int uid = jo.getInt("uid");
		String search = jo.getString("topic");
		return Cm2ActionManager.getSearchTopic(search);
	}

	@POST
	@Path("/user/{uid}/program/advance")
	public String advanceUserProgramSection(@PathParam("uid") int uid, String json) throws Exception {
		JSONObject jo = new JSONObject(json);
		boolean retake = jo.getInt("retake") == 0 ? false : true;

		return Cm2ActionManager.advanceUserProgram(uid, !retake);
	}
	
	@POST
	@Path("/assignments/{uid}/{assignKey}")
	public String getAssignment(@PathParam("uid") final int uid, @PathParam("assignKey") final int assignKey) throws Exception {
		
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				try {
					StudentAssignment ass = Cm2ActionManager.getCm2Assignment(uid, assignKey);
					
					ass.getStudentStatuses().getStudentAssignment().getStudentStatuses().setStudentAssignment(null);
					new Gson().toJson(ass);
					//String json = JsonWriter.objectToJson(ass);
					String json = new Gson().toJson(ass);
					//System.out.print(json);
					
					return json;
				}
				catch(Throwable t) {
					t.printStackTrace();	
					throw t;
				}
			}
		});
		
		
	}


	@POST
	@Path("/assignments/{uid}")
	public String getAssignmentsListing(@PathParam("uid") int uid) throws Exception {
		return Cm2ActionManager.getAssignmentsListing(uid);
	}


	@POST
	@Path("/assignments/{uid}/{assignKey}/turn_in")
	public String turnInAssignment(@PathParam("uid") int uid, @PathParam("assignKey") int assignKey) throws Exception {
		return Cm2ActionManager.turnInAssignment(uid, assignKey);
	}

	@POST
	@Path("/user/{uid}/assignment/{assignKey}/{pid}")
	public String getAssignmentProblem(@PathParam("uid") int uid, @PathParam("assignKey") int assignKey,
			@PathParam("pid") String pid) throws Exception {
		return Cm2ActionManager.getCm2AssignmentProblem(uid, assignKey, pid);
	}

	@GET
	@POST
	@Path("/user/{uid}/info")
	public String getAssignmentProblem(@PathParam("uid") int uid) throws Exception {
		return Cm2ActionManager.getUserInfo(uid);
	}

	@POST
	@Path("/user/{uid}/messages")
	public String getUserMessages(@PathParam("uid") int uid) throws Exception {
		return Cm2ActionManager.getUserMessages(uid);
	}

	@GET
	@POST
	@Path("/user/{uid}/events")
	public String getUserSyncEvents(@PathParam("uid") final int uid, final String json) throws Exception {
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				String currentProgram="";
				int activeMinutes=0;
				if(json != null && json.startsWith("{")) {
					JSONObject jo = new JSONObject(json);
					currentProgram = jo.has("programName")?jo.getString("programName"):"";
					activeMinutes = jo.has("activeMinutes")?jo.getInt("activeMinutes"):0;
				}
				return Cm2ActionManager.getUserSyncEvents(uid, currentProgram, activeMinutes);				
			}
		});
	}

	@POST
	@Path("user/{uid}/assignment/{assignKey}/{pid}/widget")
	public String saveAssignmentProblemWidgetValue(@PathParam("uid") int uid, @PathParam("assignKey") int assKey,
			@PathParam("pid") String pid, String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		String value = jo.getString("value");
		boolean correct = jo.getBoolean("correct");

		return Cm2ActionManager.saveAssignmentProblemWidgetValue(uid, assKey, pid, value, correct);
	}

	@GET
	@POST
	@Path("user/{uid}/assignment/{assignKey}/{pid}/whiteboard")
	public String getWhiteboardForAssignmentProblem(@PathParam("uid") int uid, @PathParam("assignKey") int assKey,
			@PathParam("pid") String pid) throws Exception {
		return Cm2ActionManager.getWhiteboardForAssignmentProblem(uid, assKey, pid);
	}

	@POST
	@Path("user/{uid}/assignment/{assignKey}/{pid}/whiteboard/save")
	public String saveWhiteboardForAssignmentProblem(@PathParam("uid") int uid, @PathParam("assignKey") int assKey,
			@PathParam("pid") String pid, String json) throws Exception {
		JSONObject jo = new JSONObject(json);
		String data = jo.getString("data");
		return Cm2ActionManager.saveWhiteboardForAssignmentProblem(uid, assKey, pid, data);
	}

	@POST
	@Path("user/{uid}/assignment/{assignKey}/{pid}/status")
	public String updateAssignmentProblemStatus(@PathParam("uid") int uid, @PathParam("assignKey") int akey,
			@PathParam("pid") String pid, String json) throws Exception {
		JSONObject jo = new JSONObject(json);
		String status = jo.getString("status");
		return Cm2ActionManager.updateAssignmentProblemStatus(uid, akey, pid, status);
	}

	@POST
	@Path("review")
	public String getTopicReviewText(String jsonData) throws Exception {
		JSONObject jo = new JSONObject(jsonData);
		String file = jo.getString("file");
		String language = jo.getString("language");
		return Cm2ActionManager.getTopicReviewText(file, language.equalsIgnoreCase("spanish"));
	}

	
	@POST
	@Path("device/{deviceId}/new_user")
	public String createNewUserForDeviceID(@PathParam("deviceId") String deviceId) throws Exception {
		Cm2ActionManager.deleteUserByDeviceId(deviceId);
		return new Gson().toJson(new RpcData("status=OK"));
	}
	
	
	@POST
	@Path("user/{uid}/reset")
	public String doRetailReset(@PathParam("uid") int uid, String data) throws Exception {
		return processDoRetailReset(uid, data);
	}

	private String processDoRetailReset(int uid, String data) throws Exception {
		JSONObject jo = new JSONObject(data);
		String type = jo.getString("type");
		
		Connection conn=null; 
		try {
			conn = HMConnectionPool.getConnection();
			if(type == null || type.equals("quiz")) {
				
                CmProgramFlow programFlow = new CmProgramFlow(conn, uid);
                programFlow.getActiveInfo().setActiveRunId(0); // 
                programFlow.getActiveInfo().setActiveTestId(0); // 
				ActionDispatcher.getInstance().execute(new ResetUserAction(ResetType.RESENT_QUIZ, programFlow.getUserProgram().getId(),0));
				programFlow.saveActiveInfo(conn);
			}
			else {
			   ResetUserAction action = new ResetUserAction(ResetType.FULL,uid, 0);
			   ActionDispatcher.getInstance().execute(action);
			}
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
		return JsonWriter.objectToJson(new RpcData());
	}
	
	@POST
	@Path("user/{uid}/feedback")
	public String saveUserFeedback(@PathParam("uid") int uid, String feedbackMessage) throws Exception {
		return JsonWriter.objectToJson(Cm2ActionManager.saveFeedback(uid, feedbackMessage));
	}
	
	
	
	@POST
	@Path("user/{uid}/teacher_notes")
	public String getTeacherNotes(@PathParam("uid") final int uid, final String data) throws Exception {
		
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				List<ProblemAnnotation> ann = AssignmentDao.getInstance().getUnreadAnnotatedProblems(uid);
				return new Gson().toJson(ann);
			}
		});
		
	}

	
	
	
	
	

	@POST
	@Path("quiz/{rid}/results")
	public String getQuizResults(@PathParam("rid") int rid) throws Exception {
		return JsonWriter.objectToJson(Cm2ActionManager.getQuizResults(rid));
	}
	
	
	@POST
	@Path("storage/{deviceId}/save")
	public String saveDeviceStorage(@PathParam("deviceId") String deviceId, String json) throws Exception {
		saveDeviceStorageAux(deviceId, json);
		return JsonWriter.objectToJson(new RpcData("status=ok"));
	}
	
	private void saveDeviceStorageAux(String deviceId, String json) throws Exception {
		DeviceStorage storage = new DeviceStorage();
		JSONObject jo = new JSONObject(json);
		String storageJson = jo.getString("data");
		
		JSONObject joData = new JSONObject(storageJson);
		Iterator iter = joData.keys();
		while(iter.hasNext()) {
			String k = (String)iter.next();
			storage.getStorage().put(k, joData.getString(k));
		}
		DeviceStorageDao.saveStorage(deviceId, storage);
	}

	@POST
	@Path("storage/{deviceId}")
	public String getDeviceStorage(@PathParam("deviceId") String deviceId) throws Exception {
		return JsonWriter.objectToJson(DeviceStorageDao.getStorage(deviceId));
	}
	
	@POST
	@Path("storage/{deviceId}/delete")
	public String deleteDeviceStorage(@PathParam("deviceId") String deviceId) throws Exception {
		DeviceStorageDao.deleteStorage(deviceId);
		return JsonWriter.objectToJson(new RpcData("status=ok"));
	}	
	
	
	@POST
	@Path("pid_search")
	public String searchForPids(String json) throws Exception {
		JSONObject jo = new JSONObject(json);
		String searchFor = jo.getString("data");
		int limit = jo.getInt("limit");
		return new Gson().toJson(doPidSearch(searchFor, limit));
	}	
	
	private List<String> doPidSearch(String searchFor, int limit) throws Exception {
		return new CmSolutionManagerDao().searchForPids(searchFor, limit);
	}
	
	
	@POST
	@Path("transform")
	public String processMathMlTransformation(final String mathMl) throws Exception {
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				return new MathMlTransform().processMathMlTransformations(mathMl);
			}
		});
	}
	
	
	
	@POST
	@Path("version")
	public String getVersion() throws Exception {
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
		        SbFile file = new SbFile(CatchupMathProperties.getInstance().getCatchupRuntime() + "/cm_app_ver.txt");
		        String minVersion = file.getFileContents().toString();
		        return minVersion; 
		    }
		});
	}
	

	@GET
	@Path("testing_pids")
	public String getTestingPids() throws Exception {
		return RestResult.getResultObject(new CmRestCommand() {
			@Override
			public String execute() throws Exception {
				return new Gson().toJson(Cm2ActionManager.getTestingPids());
		    }
		});
	}
}
