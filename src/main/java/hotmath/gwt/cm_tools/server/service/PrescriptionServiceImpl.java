package hotmath.gwt.cm_tools.server.service;

import hotmath.HotMathProperties;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionService;
import hotmath.gwt.cm_tools.client.ui.NextAction;
import hotmath.gwt.cm_tools.client.ui.NextAction.NextActionName;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.client.rpc.action.SetInmhItemAsViewedAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.solution.Solution;
import hotmath.testset.TestSet;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefFactory;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunResult;
import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.HmContentExtractor;
import hotmath.util.Jsonizer;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sb.util.SbFile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PrescriptionServiceImpl extends RemoteServiceServlet implements PrescriptionService {
    
    private static final long serialVersionUID = 1034624620689798799L;
    
    Logger logger = Logger.getLogger(PrescriptionServiceImpl.class);

    public PrescriptionServiceImpl() {
        logger.info("PrescriptionServiceImpl Created");
    }

    public RpcData getPrescriptionSessionJson(int runId, int sessionNumber, boolean updateActiveInfo) throws CmRpcException {
        GetPrescriptionAction getPresAction = new GetPrescriptionAction(runId,sessionNumber, updateActiveInfo);
        return ActionDispatcher.getInstance().execute(getPresAction);
    }

    public ArrayList<RpcData> getViewedInmhItems(int runId) throws CmRpcException {
        GetViewedInmhItemsAction getViewedAction = new GetViewedInmhItemsAction(runId);
        List<RpcData> rdata = ActionDispatcher.getInstance().execute(getViewedAction).getRpcData();
        
        return (ArrayList<RpcData>)rdata;
    }
    
    
    public RpcData getSolutionHtml(int userId, String pid) throws CmRpcException {
        GetSolutionAction getViewedAction = new GetSolutionAction(userId, pid);
        return ActionDispatcher.getInstance().execute(getViewedAction);
    }
    
    

    public void setInmhItemAsViewed(int runId, String type, String file) throws CmRpcException {
        SetInmhItemAsViewedAction getViewedAction = new SetInmhItemAsViewedAction(runId,type,file);
        ActionDispatcher.getInstance().execute(getViewedAction);
    }


    public RpcData getUserInfo(int uid) throws CmRpcException {
        GetUserInfoAction action = new GetUserInfoAction(uid);
        return ActionDispatcher.getInstance().execute(action);
    }


    public String getSolutionProblemStatementHtml(String pid) {
        try {
            Solution sol = SolutionManager.getSolution(pid);
            String probStatementHtml = sol.getStatement(false);

            ProblemID ppid = new ProblemID(pid);
            String path = ppid.getSolutionPath_DirOnly("solutions");
            probStatementHtml = HotMathUtilities.makeAbsolutePaths(path, probStatementHtml);

            return probStatementHtml;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Get the testset quiz Html to be inserted into HTML
     * 
     * Returns valid HTML segment or null on error.
     */
    public RpcData getQuizHtml(int uid, int testSegment) {
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            String quizHtmlTemplate = readQuizHtml();
            Map<String, Object> map = new HashMap<String, Object>();

            HaUser user = HaUser.lookUser(conn, uid,null);
            String testName = user.getAssignedTestName();

            if (testSegment == 0)
                testSegment = 1;

            boolean isActiveTest = user.getActiveTest() > 0;
            HaTest haTest = null;
            if (isActiveTest && testSegment == user.getActiveTestSegment()) {
                // reuse the existing test
                haTest = HaTest.loadTest(user.getActiveTest());
            } else {
                // register a new test
                HaTestDef testDef = HaTestDefFactory.createTestDef(testName);
                haTest = HaTest.createTest(uid, testDef, testSegment);
            }

            String testTitle = haTest.getTitle();

            TestSet _testSet = new TestSet(haTest.getPids());

            map.put("haTest", haTest);
            map.put("testTitle", testTitle);
            map.put("testSet", _testSet);
            map.put("subTitle", haTest.getSubTitle(testSegment));

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("quiz_html", quizHtml);
            rpcData.putData("test_id", haTest.getTestId());
            rpcData.putData("quiz_segment", testSegment);
            rpcData.putData("quiz_segment_count", haTest.getTotalSegments());
            rpcData.putData("title", testTitle);

            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }

        return null;
    }

    /**
     * Returns the quiz data for this test
     * 
     * Returns valid HTML segment or null on error.
     * 
     * rpcData: quiz_html, test_id, quiz_segment, title
     */
    public RpcData getQuizHtml(int testId) throws CmRpcException {
        try {

            String quizHtmlTemplate = readQuizHtml();
            Map<String, Object> map = new HashMap<String, Object>();

            HaTest haTest = HaTest.loadTest(testId);
            String testTitle = haTest.getTitle();

            TestSet _testSet = new TestSet(haTest.getPids());

            int testSegment = haTest.getSegment();
            map.put("haTest", haTest);
            map.put("testTitle", testTitle);
            map.put("testSet", _testSet);
            map.put("subTitle", testSegment);

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("quiz_html", quizHtml);
            rpcData.putData("test_id", haTest.getTestId());
            rpcData.putData("quiz_segment", testSegment);
            rpcData.putData("title", testTitle);

            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException(e.getMessage());
        }
    }

    private String readQuizHtml() throws IOException {
        InputStream is = getClass().getResourceAsStream("quiz_template.vm");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Return the HTML content in the hm_content of the named file in the
     * hotmath space.
     * 
     * @param file
     * @param baseDirectory
     *            The directory relative images should point
     * 
     * @return
     */
    public String getHmContent(String file, String baseDirectory) {
        try {
            String filePath = HotMathProperties.getInstance().getHotMathWebBase();
            filePath += "/" + file;

            String html = new SbFile(filePath).getFileContents().toString("\n");

            HmContentExtractor ext = new HmContentExtractor();
            String hmContent = ext.extractContent(html, baseDirectory);

            return hmContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Method used to get a list of RcpData objects defining test results for
     * the current test.
     * 
     * @return ArrayList of RpcData objects
     * 
     *         __gwt.typeArgs <hotmath.gwt.cm.client.util.RpcData>
     */
    public ArrayList<RpcData> getQuizCurrentResults(int userId) throws CmRpcException {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            int testId = HaUser.lookUser(conn, userId,null).getActiveTest();
            List<HaTestRunResult> testResults = HaTest.loadTest(testId).getTestCurrentResponses(conn);
            ArrayList<RpcData> rpcData = new ArrayList<RpcData>();
            for (HaTestRunResult tr : testResults) {
                if (tr.isAnswered()) {
                    RpcData rd = new RpcData(Arrays.asList("pid=" + tr.getPid(), "answer=" + tr.getResponseIndex()));
                    rpcData.add(rd);
                }
            }
            return rpcData;
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }

    public void saveWhiteboardData(int uid, int runId, String pid, String command, String commandData)
            throws CmRpcException {
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "insert into HA_TEST_RUN_WHITEBOARD(user_id, pid, command, command_data, insert_time_mills, run_id) "
                    + " values(?,?,?,?,?,?) ";
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, uid);
            pstat.setString(2, pid);
            pstat.setString(3, command);
            pstat.setString(4, commandData);
            pstat.setLong(5, System.currentTimeMillis());
            pstat.setInt(6, runId);

            if (pstat.executeUpdate() != 1)
                throw new Exception("Could not save whiteboard data (why?)");
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    public ArrayList<RpcData> getWhiteboardData(int uid, String pid) throws CmRpcException {

        ArrayList<RpcData> data = new ArrayList<RpcData>();
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "select * from HA_TEST_RUN_WHITEBOARD "
                    + " where user_id = ? and pid = ? order by insert_time_mills";

            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, uid);
            pstat.setString(2, pid);

            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                List<String> ln = new ArrayList<String>();
                RpcData rd = new RpcData(ln);
                rd.putData("command", rs.getString("command"));
                rd.putData("command_data", rs.getString("command_data"));

                data.add(rd);
            }

            return data;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    public void saveQuizCurrentResult(int testId, boolean correct, int answerIndex, String pid) throws CmRpcException {
        try {
            HaTest test = HaTest.loadTest(testId);
            test.saveTestQuestionChange(pid, answerIndex, correct);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }

    }


    /**
     * Create the Test run for this test by reading the current question
     * selections
     * 
     * @param testId
     * @param callBack
     * @return
     */
    public RpcData createTestRun(int testId) throws CmRpcException {

        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            HaTest test = HaTest.loadTest(testId);

            // get list of all correct answers
            List<String> incorrectPids = new ArrayList<String>();
            int totalAnswered = 0;
            int notAnswered = 0;
            int answeredCorrect = 0;
            int answeredIncorrect = 0;
            int totalSessions = 0;

            String sql = "select cs.pid, cs.is_correct, t.total_segments from v_HA_TEST_CURRENT_STATUS cs, HA_TEST t where cs.test_id = ? and t.test_id = cs.test_id";
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, testId);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                if (totalSessions < 1) {
                    totalSessions = rs.getInt("total_segments");
                }
                String pid = rs.getString("pid");
                Integer corr = rs.getInt("is_correct");
                if (rs.wasNull())
                    corr = null;

                if (corr != null) {
                    if (corr == 1)
                        answeredCorrect++;
                    else
                        answeredIncorrect++;

                    totalAnswered++;
                } else
                    notAnswered++;

                if (corr == null || corr == 0) {
                    incorrectPids.add(pid);
                }
            }

            HaTestRun run = test.createTestRun(incorrectPids.toArray(new String[incorrectPids.size()]),
                    answeredCorrect, answeredIncorrect, notAnswered, totalSessions);

            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(run.getRunId());

            // Let the prescription instruct the next action depending on
            // type of test, status, etc.
            NextAction nextAction = pres.getNextAction();
            RpcData rdata = new RpcData();
            rdata.putData("correct_answers", answeredCorrect);
            rdata.putData("total_questions", (notAnswered + totalAnswered));

            if (!nextAction.getNextAction().equals(NextActionName.PRESCRIPTION)) {
                // need to inform caller it needs to show the quiz ...
                // Caught in QuizContent

                if (nextAction.getNextAction().equals(NextActionName.AUTO_ASSSIGNED)) {
                    rdata.putData("redirect_action", "AUTO_ASSIGNED");
                    rdata.putData("assigned_test", nextAction.getAssignedTest());
                } else {
                    rdata.putData("redirect_action", "QUIZ");
                    rdata.putData("segment", test.getUser().getActiveTestSegment());
                }

                return rdata;
            }

            rdata.putData("run_id", run.getRunId());
            rdata.putData("correct_percent", GetPrescriptionCommand.getTestPassPercent(run.getHaTest().getNumTestQuestions(), run
                    .getAnsweredCorrect()));
            return rdata;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error creating new test run: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    public void resetUser(int userId) throws CmRpcException {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            HaUser user = HaUser.lookUser(conn, userId,null);
            user.setActiveTest(0);
            user.setActiveTestRunId(0);
            user.setActiveTestSegment(0);
            user.setActiveTestRunSession(0);

            user.update(conn);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }

    /**
     * Return the QuizHtml with the results stored as a list of pids that are
     * correct.
     * 
     * rpcData: (all from QuizHtml) and
     * quiz_result_json,quiz_correct_count,quiz_question_count
     */
    // @Override
    public RpcData getQuizResultsHtml(int runId) throws CmRpcException {
        try {
            HaTestRun testRun = HaTestRun.lookupTestRun(runId);
            List<HaTestRunResult> results = testRun.getTestRunResults();
            String resultJson = "";
            for (HaTestRunResult r : results) {
                if (resultJson.length() > 0)
                    resultJson += ",";
                resultJson += Jsonizer.toJson(r);
            }
            resultJson = "[" + resultJson + "]";

            RpcData quizRpc = getQuizHtml(testRun.getHaTest().getTestId());

            quizRpc.putData("quiz_result_json", resultJson);
            quizRpc.putData("quiz_question_count", testRun.getHaTest().getTestQuestionCount());
            quizRpc.putData("quiz_correct_count", testRun.getAnsweredCorrect());

            return quizRpc;
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public void setUserBackground(int userId, String backgroundStyle) throws CmRpcException {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            HaUser user = HaUser.lookUser(conn, userId,null);
            user.setBackgroundStyle(backgroundStyle);
            user.update(conn);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }

    public void saveFeedback(String comments, String commentsUrl, String stateInfo) throws CmRpcException {

        Connection conn = null;
        PreparedStatement pstat = null;

        try {

            String sql = "insert into HA_FEEDBACK(entry_date, comment,comment_url,state_info)values(now(),?,?,?)";

            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, comments);
            pstat.setString(2, commentsUrl);
            pstat.setString(3, stateInfo);

            if (pstat.executeUpdate() != 1)
                throw new Exception("could not save feedback comments");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    public List<StudentShowWorkModel> getStudentShowWork(Integer uid, Integer runId) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getStudentShowWork(uid, runId);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<StudentActivityModel> getStudentActivity(StudentModel sm) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getStudentActivity(sm.getUid());
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public StudentModel getStudentModel(Integer uid) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getStudentModel(uid);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<StudyProgramModel> getProgramDefinitions() throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getProgramDefinitions();
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<SubjectModel> getSubjectDefinitions() throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getSubjectDefinitions();
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<GroupModel> getActiveGroups(Integer adminUid) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getActiveGroups(adminUid);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public StudentModel addUser(StudentModel sm) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.addStudent(sm);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
            Boolean passcodeChanged) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.updateStudent(sm, stuChanged, progChanged, progIsNew, passcodeChanged);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getChaptersForProgramSubject(progId, subjId);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }
    

    public GroupModel addGroup(Integer adminUid, GroupModel gm) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.addGroup(adminUid, gm);
        }
        catch(Exception e) {
            throw new CmRpcException(e);
        }
    }

    
    

    /**
     * Simple test to pound the getting of solution html to see if it is
     * responsible for the Too Many Files error.
     * 
     * I suspect it was the cause, or at least the many instances of
     * SolutionHTMLCreatorIimplVelocity was.
     * 
     * @param as
     */
    static public void main(String as[]) {
        try {
            PrescriptionServiceImpl pi = new PrescriptionServiceImpl();

            for (int i = 0; i < 50; i++) {
                Connection conn = null;
                PreparedStatement pstat = null;
                try {
                    String sql = "select problemindex from SOLUTIONS where booktitle = 'genericalg1'";

                    conn = HMConnectionPool.getConnection();
                    pstat = conn.prepareStatement(sql);

                    ResultSet rs = pstat.executeQuery();
                    while (rs.next()) {
                        String pid = rs.getString("problemindex");

                        pi.getSolutionHtml(700, pid);
                    }

                } finally {
                    SqlUtilities.releaseResources(null, pstat, conn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
