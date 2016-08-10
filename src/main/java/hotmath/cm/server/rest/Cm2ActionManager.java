package hotmath.cm.server.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import hotmath.ProblemID;
import hotmath.cm.server.model.CmPaymentDao;
import hotmath.cm.server.model.StudentEventsDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_core.client.model.Cm2PrescriptionTopic;
import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_core.client.model.UserSyncInfo;
import hotmath.gwt.cm_core.client.rpc.GetUserSyncAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionForMobileAction;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.QuizResultsMetaInfo;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.client.rpc.TurnInAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc.client.rpc.cm2.CheckCm2QuizAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2Assignments;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2MobileUser;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2MobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2QuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2CheckedResult;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2HtmlResult;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.gwt.shared.server.service.command.GetReviewHtmlCommand;
import hotmath.gwt.shared.server.service.command.NewMobileUserCommand;
import hotmath.gwt.shared.server.service.command.cm2.GetCm2MobileLoginCommand;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.HaUserDao;
import hotmath.testset.ha.HaUserFactory;
import hotmath.testset.ha.SolutionDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

/**
 * Central place to request a CM2 request with any specialized formatting
 * required.
 *
 * serves as a wrapper around ActionDispatcher actions.
 *
 * @author casey
 *
 */
public class Cm2ActionManager {

    /**
     * Wrapper around GetUserInfoAction
     *
     * process login info and returns composite data with either relevant quiz
     * or prescription info.
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getUserCurrentProgram(int userId) throws Exception {

        // int randomTestId = HaTestDao.getRandomTestId();
        int randomUserId = HaUserDao.getRandomUserId();
        // int randomRunId = HaTestRunDao.getRandomTestRun();
        
        GetCmMobileLoginAction action = new GetCmMobileLoginAction(randomUserId);
        // GetUserInfoAction action = new GetUserInfoAction(randomUserId,
        // "Random Test");
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        // GetCm2QuizHtmlAction action = new GetCm2QuizHtmlAction(randomTestId);

        return jsonResponse;
    }

    // static int TEST_ID=2610241; // debug
    // static int USER_ID = 678549;

    public static String checkQuiz(int testId, boolean forceRandomPass) throws Exception {

        HaTestDao.resetTest(testId);

        if(forceRandomPass) {
            HaTestDao.getInstance().setAllToCorrectExcept(testId, 2);
        }
        
        CheckCm2QuizAction action = new CheckCm2QuizAction(testId);
        

        QuizCm2CheckedResult results = ActionDispatcher.getInstance().execute(action);
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            List<Cm2PrescriptionTopic> topics = GetCm2MobileLoginCommand.extractPrescriptionTopics(conn, results.getTestRunResults().getNextAction());
            results.setPrescriptionTopics(topics);
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        

		/** first program assigned in auto-enrollment is free
		 * 
		 */
        if(results.getTestRunResults().getRunId() > 0) {
            HaTest test = HaTestDao.getInstance().loadTest(action.getTestId());
            if(test.getTestDef().getTestDefId() == CmProgram.AUTO_ENROLL.getDefId()) {
	    		CmPaymentDao.getInstance().addPurchase(test.getUser().getUid(), results.getTestRunResults().getAssignedTest());
            }
        }
        
        String json = new Gson().toJson(results);
        return json;
    }

    public static String getPrescriptionTopic(int runId, int topicIndex) throws Exception {
        GetPrescriptionAction action = new GetPrescriptionAction(runId, topicIndex, true);
        return new ActionDispacherWrapper().execute(action);
    }

    public static String getPrescriptionResource(String pid) throws Exception {
        return "";
    }

    public static String getSolution(String pid) throws Exception {
        try {
            LoadSolutionMetaAction action1 = new LoadSolutionMetaAction(new ProblemID(pid).getGUID());
            SolutionMeta solutionSteps = ActionDispatcher.getInstance().execute(action1);
            GetSolutionForMobileAction action = new GetSolutionForMobileAction(pid);
            SolutionResponse solutionInfo2 = ActionDispatcher.getInstance().execute(action);

            Cm2SolutionInfo solutionInfo = new Cm2SolutionInfo(solutionSteps.getProblemStatement(), solutionInfo2);
            return new Gson().toJson(solutionInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSolution(int rid, String pid) throws Exception {
        try {
            LoadSolutionMetaAction action1 = new LoadSolutionMetaAction(new ProblemID(pid).getGUID());
            SolutionMeta solutionSteps = ActionDispatcher.getInstance().execute(action1);

            SolutionResponse solutionInfo2 = ActionDispatcher.getInstance().execute(new GetSolutionForMobileAction(pid));

            SolutionContext context = SolutionDao.getInstance().getSolutionContext(rid, pid);
            solutionInfo2.setSolutionVariableContext(context!=null?context.getContextJson():null);

            Cm2SolutionInfo solutionInfo = new Cm2SolutionInfo(solutionSteps.getProblemStatement(), solutionInfo2);
            
            
            if(rid > 0) {
                solutionInfo.setWidgetResult(HaTestRunDao.getInstance().getRunTutorWidgetValue(rid, pid));
            }
            return new Gson().toJson(solutionInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String markResourceAsViewed(int rid, int topicIndex, String type, String file) throws Exception {

        CmResourceType resourceType = CmResourceType.valueOf(type.toUpperCase());

        return new ActionDispacherWrapper().execute(new SetInmhItemAsViewedAction(rid, resourceType, file, topicIndex));
    }

    public static String setQuizAnswer(int tid, String pid, int choiceIndex, boolean isCorrect) throws Exception {
        return new ActionDispacherWrapper().execute(new SaveQuizCurrentResultAction(tid, isCorrect, choiceIndex, pid));
    }

    public static String setPrescriptionSolutionContext(int rid, String pid, String context) {
        try {
            RpcData results = ActionDispatcher.getInstance().execute(new SaveSolutionContextAction(0, rid, pid, 0, context));
            return new Gson().toJson(results);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPrescriptionSolutionWhiteboard(int uid, int rid, String pid) throws Exception {
        try {
            GetWhiteboardDataAction action = new GetWhiteboardDataAction(uid, pid, rid);
            CmList<WhiteboardCommand> result = ActionDispatcher.getInstance().execute(action);
            return new Gson().toJson(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String savePrescriptionSolutionWhiteboard(int uid, int tid, int rid, String pid, String cmdType, String jsonData) throws Exception {
        try {
            SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(uid, tid, rid, pid, CommandType.valueOf(cmdType.toUpperCase()), jsonData);
            RpcData result = ActionDispatcher.getInstance().execute(action);
            return new Gson().toJson(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getSearchResults(int uid, String searchFor) throws Exception {
        try {
            SearchTopicAction action = new SearchTopicAction(searchFor, SearchApp.CM_MOBILE2, uid);
            TopicSearchResults result = ActionDispatcher.getInstance().execute(action);
            return new Gson().toJson(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getSearchTopic(String topic) throws Exception {
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            GetTopicPrescriptionAction action = new GetTopicPrescriptionAction(topic);
            PrescriptionSessionResponse response = ActionDispatcher.getInstance().execute(action);
            
            
            // for each topic
            //
            int runId = 0; // where?
            //GetPrescriptionAction pa = new GetPrescriptionAction(runId, 0, false);
            //PrescriptionSessionResponse data = new GetPrescriptionCommand().execute(conn, pa);
            
            PrescriptionSessionData currSess = response.getPrescriptionData().getCurrSession();
            String topicHtml = new GetReviewHtmlCommand().execute(conn,new GetReviewHtmlAction(currSess.getFile())).getLesson();
            
            
            // point all solution images to image server
            topicHtml = GetCm2MobileLoginCommand.replaceImagesWithSolutionServer("/hotmath_help", topicHtml);
            
            Cm2PrescriptionTopic topicObject = new Cm2PrescriptionTopic(currSess.getTopic(), topicHtml, GetCm2MobileLoginCommand.getResources(response.getPrescriptionData()));
            
            return new Gson().toJson(topicObject);
        }
        catch(Exception e) {
            throw e;
        }
    }

    public static String advanceUserProgram(int uid, boolean advanceToNextSegment) throws Exception {
        
        
        FlowType flowType = FlowType.RETAKE_SEGMENT;
   
        HaUser user = HaUserDao.getInstance().lookUser(uid, false);
        HaTestRun run = HaTestRunDao.getInstance().lookupTestRun(user.getActiveTestRunId());
        if(run.isPassing()) {
            flowType = FlowType.NEXT;
        }
        
        GetCmProgramFlowAction action = new GetCmProgramFlowAction(uid, flowType);
        CmProgramFlowAction result = ActionDispatcher.getInstance().execute(action);
        
        GetCm2QuizHtmlAction actionQuiz = new GetCm2QuizHtmlAction(result.getQuizResult().getTestId());
        QuizCm2HtmlResult quizResponse = ActionDispatcher.getInstance().execute(actionQuiz);
        
        return new Gson().toJson(quizResponse);
    }

    public static String getAssignmentsListing(int uid) throws Exception  {
        CmList<StudentAssignmentInfo> results = ActionDispatcher.getInstance().execute(new GetAssignmentsForUserAction(uid));
        List<StudentAssignmentInfo> assignList = new ArrayList<StudentAssignmentInfo>();
        assignList.addAll(results);
        Cm2Assignments assignments = new Cm2Assignments(assignList);
        
        return new Gson().toJson(assignments);
    }

    public static String getCm2AssignmentProblem(int uid, int assignKey, String pid) throws Exception {
        AssignmentProblem result = ActionDispatcher.getInstance().execute(new GetAssignmentSolutionAction(uid, assignKey, pid));
        result.getInfo().setHtml(GetCm2MobileLoginCommand.replaceImagesWithSolutionServer("/help/solutions/", result.getInfo().getHtml()));
        return new Gson().toJson(result);
    }
    

    public static String getCm2Assignment(int uid, int assignKey) throws Exception {

        GetStudentAssignmentAction action = new GetStudentAssignmentAction(uid,  assignKey);
        StudentAssignment studentAssignment = ActionDispatcher.getInstance().execute(action);
 
    	
    	
       // Assignment assignment = AssignmentDao.getInstance().getAssignment(assignKey);
       // List<PrescriptionSessionDataResource> cm2Resources = AssignmentDao.getInstance().getAssigmentResources(assignment);
         
        //Cm2Assignment cm2Ass = new Cm2Assignment(assignment, cm2Resources); 
        return new Gson().toJson(studentAssignment);
    }

	public static String saveAssignmentProblemWidgetValue(int uid, int assKey, String pid, String value,
			boolean correct) throws Exception {
	    
	    
	    /** update the tutor widget value and the assignment problem status in one request 
	     * 
	     */
        MultiActionRequestAction multiRequest = new MultiActionRequestAction();
        multiRequest.getActions().add(new SaveAssignmentTutorInputWidgetAnswerAction(uid,assKey,pid,value,correct));
        multiRequest.getActions().add(new SaveAssignmentProblemStatusAction(uid,assKey,pid, correct?"Correct":"Incorrect"));
        
		CmList<Response> retVal = ActionDispatcher.getInstance().execute(multiRequest);
		return new Gson().toJson(new RpcData("status=OK"));
	}

	public static String getWhiteboardForAssignmentProblem(int uid, int assKey, String pid) throws Exception  {
		AssignmentWhiteboardData results = ActionDispatcher.getInstance().execute(new GetAssignmentWhiteboardDataAction(uid,pid, assKey));
		return new Gson().toJson(results);
	}

	public static String saveWhiteboardForAssignmentProblem(int uid, int assKey, String pid, String commandData) throws Exception {
		RpcData results = ActionDispatcher.getInstance().execute(new SaveAssignmentWhiteboardDataAction(uid, assKey, pid, CommandType.DRAW, commandData, false));
		return new Gson().toJson(results);
	}

    public static String updateAssignmentProblemStatus(int uid, int akey, String pid, String status) throws Exception {       
        RpcData res = ActionDispatcher.getInstance().execute(new SaveAssignmentProblemStatusAction(uid,akey, pid, status));
        return new Gson().toJson(res);
    }

    public static String getTopicReviewText(String file, boolean spanish) throws Exception  {
        LessonResult res = ActionDispatcher.getInstance().execute(new GetReviewHtmlAction(file,  spanish));
        res.setLesson(GetCm2MobileLoginCommand.replaceImagesWithSolutionServer("/hotmath_help", res.getLesson()));      
        
        res.setLesson(GetCm2MobileLoginCommand.replaceImagesWithSolutionServer("/images/gt", res.getLesson()));
        
        return new Gson().toJson(res);
    }

    public static String turnInAssignment(int uid, int assignKey) throws Exception {
        RpcData res = ActionDispatcher.getInstance().execute(new TurnInAssignmentAction(uid,  assignKey));
        return new Gson().toJson(res);
    }

    public static String saveTutorInputWidgetAnswer(int uid, int rid, String pid, String value, boolean isCorrect) throws Exception {
        UserTutorWidgetStats data = ActionDispatcher.getInstance().execute(new SaveTutorInputWidgetAnswerAction(uid, rid, pid, value, isCorrect));
        return new Gson().toJson(data);
    }

    public static String getUserInfo(int uid) throws Exception {
        UserLoginResponse data = ActionDispatcher.getInstance().execute(new GetUserInfoAction(uid, null));
        return new Gson().toJson(data);
    }

    public static String getUserSyncEvents(int uid) throws Exception {
        GetUserSyncAction action = new GetUserSyncAction(uid);
        UserSyncInfo data = ActionDispatcher.getInstance().execute(action);
        return new Gson().toJson(data);
    }

    public static String getUserMessages(int uid) throws Exception {
        return new Gson().toJson(StudentEventsDao.getInstance().getEventHistoryFor(uid));
    }
    
    public static String loginUser(int uid, String un, String pwd, String subject, String token) throws Exception {
        // int randomUserId = HaUserDao.getRandomUserId();
        GetCm2MobileLoginAction action = uid != 0 ? new GetCm2MobileLoginAction(uid) : new GetCm2MobileLoginAction(un,pwd, subject);
        action.setDeviceToken(token);
        
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        return jsonResponse;
    }

	public static String loginUserMobile(String deviceId) throws Exception {
        GetCm2MobileLoginAction action = new GetCm2MobileLoginAction();
        action.setName("retail");
        action.setPassword(deviceId);
        action.setDeviceToken(deviceId);

        Cm2MobileUser userInfo = ActionDispatcher.getInstance().execute(action);
        return new Gson().toJson(userInfo);
        // return new Gson().toJson(userInfo);
	}

	public static RpcData saveFeedback(int uid, String feedbackMessage) throws Exception {
		

		
		SaveFeedbackAction action = new SaveFeedbackAction();

		
		/** add user name to feedback message for cross referencing */

		HaUser user = HaUserDao.getInstance().lookUser(uid,  true);
		String userName = user.getUserName();
		feedbackMessage = "userName: " + userName + "\n" + feedbackMessage;
		action.setComments(feedbackMessage);
		action.setStateInfo("uid: " + uid);
		
		return ActionDispatcher.getInstance().execute(action);
	}

	public static QuizResultsMetaInfo getQuizResults(int runId) throws Exception {
		String token = "quiz_html";
		QuizResultsMetaInfo results = ActionDispatcher.getInstance().execute(new GetQuizResultsHtmlAction(runId));
		String html = GetCm2MobileLoginCommand.replaceImagesWithSolutionServer("/help/solutions/", results.getRpcData().getDataAsString(token));
		
		results.getRpcData().putData(token, html);
		return results;
	}

	public static String loadUserProgram(int userId, String subject) throws Exception {
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
            CmProgram program = null;
            if(subject.equals("Foundations")) {
            	program = CmProgram.FOUNDATIONS;
            }
            else if(subject.equals("Essentials")) {
            	program = CmProgram.ESSENTIALS;
            }            
            else if(subject.equals("Algebra 1")) {
            	program = CmProgram.ALG1_PROF;
            }
            else if(subject.equals("Algebra 2")) {
            	program = CmProgram.ALG2_PROF;
            }
            else if(subject.equals("Geometry")) {
            	program = CmProgram.GEOM_PROF;
            }            
            else if(subject.equals("Pre-Algebra")) {
            	program = CmProgram.PREALG_PROF;
            }    
            else if(subject.equals("College Basic Math")) {
            	program = CmProgram.BASICMATH;
            }
            else if(subject.equals("College Elementary Algebra")) {
            	program = CmProgram.ELEMALG;
            }

			CmStudentDao.getInstance().assignProgramToStudent(conn, userId,program,null);
			
			return new Gson().toJson(new RpcData("status=OK"));
		}
	    finally {
	    	SqlUtilities.releaseResources(null, null, conn);
	    }
	}

	
	public static void purchaseUserProgram(int userId, String subject) throws Exception {
		CmPaymentDao.getInstance().addPurchase(userId, subject);
	}

	public static void deleteUserByDeviceId(String deviceId) throws Exception {
		int existingUid = HaUserFactory.lookupUserId(deviceId, deviceId);
		if(existingUid > 0) {
			CmStudentDao.getInstance().removeUser(existingUid);
		}
	}

	public static void resetCurrentUser(int userId) throws Exception {
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
		    new NewMobileUserCommand().setupNewMobileUserProgram(conn, userId);
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
}
