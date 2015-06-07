package hotmath.cm.server.rest;

import hotmath.ProblemID;
import hotmath.gwt.cm_core.client.model.Cm2PrescriptionTopic;
import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SaveQuizCurrentResultAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc.client.rpc.cm2.CheckCm2QuizAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2Assignments;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2MobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2QuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2CheckedResult;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2HtmlResult;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.server.service.command.GetReviewHtmlCommand;
import hotmath.gwt.shared.server.service.command.cm2.GetCm2MobileLoginCommand;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaUserDao;
import hotmath.testset.ha.SolutionDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.cedarsoftware.util.io.JsonWriter;

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
    static int USER_ID = 678549;

    public static String checkQuiz(int testId) throws Exception {

        HaTestDao.resetTest(testId);

        CheckCm2QuizAction action = new CheckCm2QuizAction(testId);

        QuizCm2CheckedResult results = ActionDispatcher.getInstance().execute(action);
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            List<Cm2PrescriptionTopic> topics = GetCm2MobileLoginCommand.extractPrescriptionTopics(conn, results
                    .getTestRunResults().getNextAction());
            results.setPrescriptionTopics(topics);
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        String json = JsonWriter.objectToJson(results);
        return json;
    }

    public static String loginUser(int uid, String un, String pwd, String subject) throws Exception {
        // int randomUserId = HaUserDao.getRandomUserId();
        GetCm2MobileLoginAction action = uid != 0 ? new GetCm2MobileLoginAction(uid) : new GetCm2MobileLoginAction(un,pwd, subject);
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        return jsonResponse;
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
            GetSolutionAction action = new GetSolutionAction(pid);
            SolutionResponse solutionInfo2 = ActionDispatcher.getInstance().execute(action);

            Cm2SolutionInfo solutionInfo = new Cm2SolutionInfo(solutionSteps.getProblemStatement(), solutionInfo2);
            return JsonWriter.objectToJson(solutionInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSolution(int rid, String pid) throws Exception {
        try {
            LoadSolutionMetaAction action1 = new LoadSolutionMetaAction(new ProblemID(pid).getGUID());
            SolutionMeta solutionSteps = ActionDispatcher.getInstance().execute(action1);

            SolutionResponse solutionInfo2 = ActionDispatcher.getInstance().execute(new GetSolutionAction(pid));

            SolutionContext context = SolutionDao.getInstance().getSolutionContext(rid, pid);
            solutionInfo2.setSolutionVariableContext(context!=null?context.getContextJson():null);

            Cm2SolutionInfo solutionInfo = new Cm2SolutionInfo(solutionSteps.getProblemStatement(), solutionInfo2);
            return JsonWriter.objectToJson(solutionInfo);
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
            return JsonWriter.objectToJson(results);
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
            return JsonWriter.objectToJson(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String savePrescriptionSolutionWhiteboard(int uid, int tid, int rid, String pid, String cmdType, String jsonData) throws Exception {
        try {
            SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(uid, tid, rid, pid, CommandType.valueOf(cmdType.toUpperCase()), jsonData);
            RpcData result = ActionDispatcher.getInstance().execute(action);
            return JsonWriter.objectToJson(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getSearchResults(int uid, String searchFor) throws Exception {
        try {
            SearchTopicAction action = new SearchTopicAction(searchFor, SearchApp.CM_MOBILE2, uid);
            TopicSearchResults result = ActionDispatcher.getInstance().execute(action);
            return JsonWriter.objectToJson(result);
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
            
            return JsonWriter.objectToJson(topicObject);
        }
        catch(Exception e) {
            throw e;
        }
    }

    public static String advanceUserProgram(int uid, boolean advanceToNextSegment) throws Exception {
        
        FlowType flowType = advanceToNextSegment?FlowType.NEXT:FlowType.RETAKE_SEGMENT;
        
        GetCmProgramFlowAction action = new GetCmProgramFlowAction(uid, flowType);
        CmProgramFlowAction result = ActionDispatcher.getInstance().execute(action);
        
        GetCm2QuizHtmlAction actionQuiz = new GetCm2QuizHtmlAction(result.getQuizResult().getTestId());
        QuizCm2HtmlResult quizResponse = ActionDispatcher.getInstance().execute(actionQuiz);
        
        return JsonWriter.objectToJson(quizResponse);
    }

    public static String getAssignmentsListing(int uid) throws Exception  {
        CmList<StudentAssignmentInfo> results = ActionDispatcher.getInstance().execute(new GetAssignmentsForUserAction(uid));
        List<StudentAssignmentInfo> assignList = new ArrayList<StudentAssignmentInfo>();
        assignList.addAll(results);
        Cm2Assignments assignments = new Cm2Assignments(assignList);
        
        return JsonWriter.objectToJson(assignments);
    }

}
