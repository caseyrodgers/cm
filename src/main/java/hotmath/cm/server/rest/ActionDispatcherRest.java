package hotmath.cm.server.rest;

import java.sql.Connection;
import java.util.ArrayList;
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

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.server.model.QuizSelection;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_core.client.model.TopicResource;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction.ResetType;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

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
	@Path("/login/user/mobile")
	public String loginMobileUser(String deviceId) throws Exception {
		return Cm2ActionManager.loginUserMobile(deviceId);
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
	@GET
	@Path("/quiz/{testId}/check")
	public String checkQuiz(@PathParam("testId") int testId, String params) throws Exception {
		return doCheckQuiz(testId, params);
	}

	private String doCheckQuiz(int testId, String json) throws Exception {

		boolean isAutoTestMode = false;

		try {
			JSONObject dataO = null;
			if (json != null && json.length() > 0) {
				JSONObject jo = new JSONObject(json);
				isAutoTestMode = jo.has("testMode") ? jo.getBoolean("testMode") : false;

				dataO = jo.getJSONObject("data");
				 JSONArray sel = dataO.getJSONArray("selections");
				 
				 List<QuizSelection> selections = new ArrayList<QuizSelection>();
				 for(int i=0;i<sel.length();i++) {
					 JSONObject o = sel.getJSONObject(i);
					 QuizSelection selection = new QuizSelection(o.getString("pid"), o.getInt("choice"), o.has("isCorrect")?o.getBoolean("isCorrect"):false);
					 
					 Cm2ActionManager.setQuizAnswer(testId, selection.getPid(), selection.getChoice(), selection.isCorrect());
				 }
			}

		} catch (Exception e) {
			e.printStackTrace();
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
	@Path("/assignments/{uid}")
	public String getAssignmentsListing(@PathParam("uid") int uid) throws Exception {
		return Cm2ActionManager.getAssignmentsListing(uid);
	}

	@POST
	@Path("/assignments/{uid}/{assignKey}")
	public String getAssignment(@PathParam("uid") int uid, @PathParam("assignKey") int assignKey) throws Exception {
		return Cm2ActionManager.getCm2Assignment(uid, assignKey);
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
	public String getUserSyncEvents(@PathParam("uid") int uid) throws Exception {
		return Cm2ActionManager.getUserSyncEvents(uid);
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
		return Cm2ActionManager.getTopicReviewText(file, language.equals("spanish"));
	}

	@POST
	@Path("user/{uid}/reset")
	public String doRetailReset(@PathParam("uid") int uid, String data) throws Exception {
		return processDoRetailReset(uid, data);
	}

	private String processDoRetailReset(int uid, String data) throws Exception {
		JSONObject jo = new JSONObject(data);
		int testId = jo.getInt("tid");
		int runId = jo.getInt("rid");
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
				CmStudentDao.getInstance().assignProgramToStudent(conn, uid, CmProgram.AUTO_ENROLL,null);
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
	@Path("quiz/{rid}/results")
	public String getQuizResults(@PathParam("rid") int rid) throws Exception {
		return JsonWriter.objectToJson(Cm2ActionManager.getQuizResults(rid));
	}
}
