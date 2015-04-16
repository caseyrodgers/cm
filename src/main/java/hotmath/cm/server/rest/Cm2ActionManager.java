package hotmath.cm.server.rest;

import hotmath.ProblemID;
import hotmath.gwt.cm_core.client.model.Cm2PrescriptionTopic;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.client.rpc.cm2.CheckCm2QuizAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2MobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2CheckedResult;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.server.service.command.cm2.GetCm2MobileLoginCommand;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaUserDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
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
        Connection conn=null;
        try {
            conn=HMConnectionPool.getConnection();
            List<Cm2PrescriptionTopic> topics = GetCm2MobileLoginCommand.extractPrescriptionTopics(conn, results.getTestRunResults().getNextAction());
            results.setPrescriptionTopics(topics);
        }
        finally {
            SqlUtilities.releaseResources(null,null, conn);
        }
        String json = JsonWriter.objectToJson(results);
        return json;
    }

    public static String loginUser(int uid, String un, String pwd) throws Exception {
        // int randomUserId = HaUserDao.getRandomUserId();
        GetCm2MobileLoginAction action = uid != 0 ? new GetCm2MobileLoginAction(uid) : new GetCm2MobileLoginAction(un, pwd);
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
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String markRppAsViewed(int rid, int topicIndex, String pid) throws Exception {
        return new ActionDispacherWrapper().execute(new SetInmhItemAsViewedAction(rid, CmResourceType.PRACTICE, pid, topicIndex));
    }

}
