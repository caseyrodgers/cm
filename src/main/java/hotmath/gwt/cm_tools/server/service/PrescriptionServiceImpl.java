package hotmath.gwt.cm_tools.server.service;

import hotmath.HotMathProperties;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.data.PrescriptionData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.service.PrescriptionService;
import hotmath.gwt.cm_tools.client.ui.NextAction;
import hotmath.gwt.cm_tools.client.ui.NextAction.NextActionName;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.solution.Solution;
import hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity;
import hotmath.solution.writer.TutorProperties;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sb.util.SbFile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PrescriptionServiceImpl extends RemoteServiceServlet implements PrescriptionService {

    public PrescriptionServiceImpl() {
        System.out.println("Created");
    }

    /**
     * Transfer data from local data structures into JSON
     * 
     * 
     * Show all INMH types, and provide an empty one where none exist
     * 
     * Lesson, Video, Activities, Required Problems, Extra Problems
     * 
     * 
     * marks the user object with the current active session
     * 
     * @param runId The run id
     * @param sessionNumber  The session number to load
     * @param updateActiveInfo   Should the user's active info be updated. 
     *                           If false, then no user state is changed.
     * 
     */
    public RpcData getPrescriptionSessionJson(int runId, int sessionNumber, boolean updateActiveInfo) throws CmRpcException {
        try {
            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(runId);

            int totalSessions = pres.getSessions().size();
            if(totalSessions == 0) {
                // no prescription created (no missed answers?)
                RpcData rdata = new RpcData();
                rdata.putData("correct_percent", 100);
                return rdata;
            }
            // which session
            if(sessionNumber > (totalSessions-1)) {
                System.out.println("WARNING: session request for " + runId + " is outside bounds of prescription: " + sessionNumber + ", " + totalSessions);
                sessionNumber = 0;
            }
            AssessmentPrescriptionSession sess = pres.getSessions().get(sessionNumber);

            PrescriptionData presData = new PrescriptionData();

            List<AssessmentPrescription.SessionData> practiceProblems = sess.getSessionDataFor(sess.getTopic());
            PrescriptionSessionDataResource problemsResource = new PrescriptionSessionDataResource();
            problemsResource.setType("practice");
            problemsResource.setLabel("Required Practice Problems");
            int cnt = 1;
            for (AssessmentPrescription.SessionData sdata : practiceProblems) {
                InmhItemData id = new InmhItemData();
                id.setTitle("Problem " + cnt++);
                id.setFile(sdata.getPid());
                id.setType("practice");
                
                problemsResource.getItems().add(id);
            }

            PrescriptionSessionDataResource lessonResource = new PrescriptionSessionDataResource();
            lessonResource.setType("review");
            lessonResource.setLabel("Review Lesson");
            InmhItemData lessonId = new InmhItemData();

            // Get the lesson this session is based on
            // each session is a single topic
            INeedMoreHelpItem item = sess.getSessionCategories().get(0);
            lessonId.setTitle(item.getTitle());
            lessonId.setFile(item.getFile());
            lessonId.setType(item.getType());
            lessonResource.getItems().add(lessonId);

            // always send complete list of all topics
            // @TODO: should we have an initialize phase and return this info
            // This would impose two request/response
            PrescriptionSessionData sessionData = new PrescriptionSessionData();
            for (AssessmentPrescriptionSession s : pres.getSessions()) {
                presData.getSessionTopics().add(s.getTopic());
            }
            presData.setCurrSession(sessionData);

            sessionData.setTopic(sess.getTopic());
            sessionData.setSessionNumber(sessionNumber);
            sessionData.setName(sess.getName());
            for (INeedMoreHelpResourceType t : sess.getPrescriptionInmhTypesDistinct()) {

                // skip the workbooks for now.
                if (t.getTypeDef().getType().equals("workbook"))
                    continue;

                PrescriptionSessionDataResource resource = new PrescriptionSessionDataResource();
                resource.setType(t.getTypeDef().getType());
                resource.setLabel(t.getTypeDef().getLabel());
                for (INeedMoreHelpItem i : t.getResources()) {
                    InmhItemData id = new InmhItemData();
                    id.setFile(i.getFile());
                    id.setTitle(i.getTitle());
                    id.setType(i.getType());

                    resource.getItems().add(id);
                }
                sessionData.getInmhResources().add(resource);
            }

            
            
            // add a results resource type to allow user to view current results
            PrescriptionSessionDataResource resultsResource = new PrescriptionSessionDataResource();
            resultsResource.setType("results");
            resultsResource.setLabel("Quiz Results");
            InmhItemData id = new InmhItemData();
            id.setTitle("Your quiz results");
            id.setFile("");
            id.setType("results");
            resultsResource.getItems().add(id);
            
            
            
            sessionData.getInmhResources().add(lessonResource);
            sessionData.getInmhResources().add(problemsResource);
            sessionData.getInmhResources().add(resultsResource);
            
            // mark all items as viewed/not
            // .. get list of viewed items so far
            List<RpcData> rdata = getViewedInmhItems(runId);
            List<PrescriptionSessionDataResource> resources = fixupInmhResources(sessionData.getInmhResources());
            for (PrescriptionSessionDataResource r : resources) {
                for (InmhItemData itemData : r.getItems()) {
                    // is this item viewed?
                    for (RpcData rd : rdata) {
                        if (rd.getDataAsString("file").equals(itemData.getFile())) {
                            itemData.setViewed(true);
                            break;
                        }
                    }
                }
            }
            sessionData.setInmhResources(resources);
            
            
            // update this user's active run session
            if(updateActiveInfo) {
                pres.getTest().getUser().setActiveTestRunSession(sessionNumber);
                pres.getTest().getUser().update();
            }
            
            RpcData rdata2 = new RpcData();
            rdata2.putData("json",Jsonizer.toJson(presData));
            rdata2.putData("correct_percent",getTestPassPercent(pres.getTest().getTestQuestionCount(), pres.getTestRun().getAnsweredCorrect()));
            rdata2.putData("program_title", pres.getTest().getTestDef().getTitle());
            return rdata2;
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /** Return the percent of correct from total
     * 
     * @param total
     * @param correct
     * @return
     */
    private int getTestPassPercent(int total, int correct) {

        double percent =  ((double)correct / (double)total) * 100.0d;

        int ipercent = (int)Math.floor(percent);
        return ipercent;
    }
    /**
     * Make sure that ALL resources are in list, and provide a dummy if none do.
     * 
     * @param inmhTypes
     */
    private List<PrescriptionSessionDataResource> fixupInmhResources(List<PrescriptionSessionDataResource> inmhTypes) {

        List<PrescriptionSessionDataResource> newTypes = new ArrayList<PrescriptionSessionDataResource>();
        String types[][] = { 
                { "Required Practice Problems", "practice","Practice problems you must complete before advancing" }, 
                { "Video", "video","Math videos related to the current topic"},
                { "Activities", "activity","Math activities and games related to the current topic" }, 
                { "Extra Practice Problems", "cmextra","Additional workbook problems" },
                { "Lesson", "review","Review lesson on the current topic" },
                { "Quiz Results", "results","The current quiz's results"}};

        for (int i = 0; i < types.length; i++) {
            String type[] = types[i];

            // find this type, if not exist .. create it
            boolean found = false;
            for (PrescriptionSessionDataResource r : inmhTypes) {
                if (r.getType().equals(type[1])) {
                    // exists, so add it
                    r.setLabel(type[0]);
                    r.setDescription(type[2]);
                    newTypes.add(r);
                    found = true;
                    break;
                }
            }
            if (!found) {
                PrescriptionSessionDataResource nr = new PrescriptionSessionDataResource();
                nr.setLabel(type[0]);
                nr.setType(type[1]);
                nr.setDescription(type[2]);
                InmhItemData iid = new InmhItemData();
                iid.setTitle("No " + type[0] + " Available");
                iid.setType(type[1]);
                iid.setFile("");
                // nr.getItems().add(iid);

                newTypes.add(nr);
            }
        }
        return newTypes;
    }

    /**
     * Return the raw HTML that makes up the solution
     * 
     */
    public String getSolutionHtml(String pid) {
        try {
            TutorProperties tutorProps = new TutorProperties();
            SolutionHTMLCreatorIimplVelocity creator = new SolutionHTMLCreatorIimplVelocity(tutorProps.getTemplate(),
                    tutorProps.getTutor());
            String solutionHtml = creator.getSolutionHTML(tutorProps, pid).getMainHtml();

            ProblemID ppid = new ProblemID(pid);
            String path = ppid.getSolutionPath_DirOnly("solutions");
            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);

            Map<String, String> map = new HashMap<String, String>();
            map.put("solution_html", solutionHtml);

            InputStream is = getClass().getResourceAsStream("tutor_wrapper.vm");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tutorWrapper = null;
            StringBuffer sb = new StringBuffer();
            while ((tutorWrapper = br.readLine()) != null) {
                sb.append(tutorWrapper);
            }
            tutorWrapper = sb.toString();

            solutionHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(tutorWrapper, map);

            // solutionHtml = "<b><img src='images/logo_1.gif'/>TEST 1</b>";
            return solutionHtml;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
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
        try {
            String quizHtmlTemplate = readQuizHtml();
            Map<String, Object> map = new HashMap<String, Object>();

            HaUser user = HaUser.lookUser(uid, null);
            String testName = user.getAssignedTestName();

            if (testSegment == 0)
                testSegment = 1;

            boolean isActiveTest = user.getActiveTest() > 0;
            HaTest haTest = null;
            if(isActiveTest && testSegment == user.getActiveTestSegment()) {
                // reuse the existing test
                haTest = HaTest.loadTest(user.getActiveTest());
            }
            else {
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
            rpcData.putData("quiz_segment_count",haTest.getTotalSegments());
            rpcData.putData("title",testTitle);
            
            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
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
            rpcData.putData("title",testTitle);
            
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
        StringBuffer sb = new StringBuffer();
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
     * @param baseDirectory The directory relative images should point
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
        // TODO Auto-generated method stub
        try {
            int testId = HaUser.lookUser(userId, null).getActiveTest();
            List<HaTestRunResult> testResults = HaTest.loadTest(testId).getTestCurrentResponses();
            ArrayList<RpcData> rpcData = new ArrayList<RpcData>();
            for (HaTestRunResult tr : testResults) {
                if(tr.isAnswered()) {
                    RpcData rd = new RpcData(Arrays.asList("pid=" + tr.getPid(), "answer=" + tr.getResponseIndex()));
                    rpcData.add(rd);
                }
            }
            return rpcData;
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public void saveWhiteboardData(int uid, int runId, String pid, String command, String commandData) throws CmRpcException {
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

    public void setInmhItemAsViewed(int runId, String type, String file) throws CmRpcException {
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "insert into HA_TEST_RUN_INMH_USE(run_id, item_type, item_file, view_time, session_number)values(?,?,?,?,?)";
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);
            pstat.setString(2, type);
            pstat.setString(3, file);
            pstat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstat.setInt(5, HaTestRun.lookupTestRun(runId).getHaTest().getUser().getActiveTestRunSession());
            

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new Exception("Error adding test run item view");

        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error adding test run item view: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    public ArrayList<RpcData> getViewedInmhItems(int runId) throws CmRpcException {
        ArrayList<RpcData> data = new ArrayList<RpcData>();
        try {
            HaTestRun run = HaTestRun.lookupTestRun(runId);
            AssessmentPrescription assessment = AssessmentPrescriptionManager.getInstance().getPrescription(
                    run.getRunId());
            String sessionStatusJson = assessment.getSessionStatusJson();
            Connection conn = null;
            PreparedStatement pstat = null;
            try {
                String sql = "select * from HA_TEST_RUN_INMH_USE where run_id = ?";
                conn = HMConnectionPool.getConnection();
                pstat = conn.prepareStatement(sql);

                pstat.setInt(1, runId);

                ResultSet rs = pstat.executeQuery();
                while (rs.next()) {
                    String type = rs.getString("item_type");
                    String file = rs.getString("item_file");

                    RpcData rpcData = new RpcData();
                    rpcData.putData("type", type);
                    rpcData.putData("file", file);
                    data.add(rpcData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new CmRpcException("Error adding test run item view: " + e.getMessage());
            } finally {
                SqlUtilities.releaseResources(null, pstat, conn);
            }

        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        return data;
    }

    private int getTotalInmHViewCount(int uid) throws Exception {
        
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "select count(*) from v_HA_USER_INMH_VIEWS_TOTAL where uid = ?";
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, uid);
            ResultSet rs = pstat.executeQuery();
            if (!rs.first())
                throw new Exception("Could not get count of viewed items");
            return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error adding test run item view: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }

    public RpcData getUserInfo(int uid) throws CmRpcException {
        try {
            HaUser user = HaUser.lookUser(uid, null);

            RpcData rpcData = new RpcData();
            rpcData.putData("uid", user.getUid());
            rpcData.putData("test_id", user.getActiveTest());
            rpcData.putData("run_id", user.getActiveTestRunId());
            rpcData.putData("test_segment", user.getActiveTestSegment());
            rpcData.putData("user_name", user.getUserName());
            rpcData.putData("session_number", user.getActiveTestRunSession());
            rpcData.putData("gui_background_style", user.getBackgroundStyle());
            rpcData.putData("test_name", user.getAssignedTestName());
            
            int totalViewCount = getTotalInmHViewCount(uid);
            
            rpcData.putData("view_count", totalViewCount);

            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
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
            String sql = "select pid, is_correct from v_HA_TEST_CURRENT_STATUS where test_id = ?";
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, testId);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                String pid = rs.getString("pid");
                Integer corr = rs.getInt("is_correct");
                if (rs.wasNull())
                    corr = null;

                if (corr != null) {
                    if(corr == 1)
                        answeredCorrect++;

                    totalAnswered++;
                }
                else
                    notAnswered++;

                if (corr == null || corr == 0) {
                    incorrectPids.add(pid);
                }
            }

            HaTestRun run = test.createTestRun(incorrectPids.toArray(new String[incorrectPids.size()]), answeredCorrect, incorrectPids.size(), notAnswered);

            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(run.getRunId());
            
            // Let the prescription instruct the next action depending on 
            // type of test,status,etc.
            NextAction nextAction = pres.getNextAction();
            RpcData rdata = new RpcData();            
            rdata.putData("correct_answers",answeredCorrect);
            rdata.putData("total_questions",(notAnswered + totalAnswered));

            if(!nextAction.getNextAction().equals(NextActionName.PRESCRIPTION)) {
                // need to inform caller it needs to show the quiz ...
                // Caught in QuizContent

                if(nextAction.getNextAction().equals(NextActionName.AUTO_ASSSIGNED)) {
                    rdata.putData("redirect_action", "AUTO_ASSIGNED");
                    rdata.putData("assigned_test", nextAction.getAssignedTest());
                }
                else {
                    rdata.putData("redirect_action", "QUIZ");
                    rdata.putData("segment", test.getUser().getActiveTestSegment());
                }
                
                return rdata;
            }
            
            rdata.putData("run_id", run.getRunId());
            rdata.putData("correct_percent",getTestPassPercent(run.getHaTest().getNumTestQuestions(), run.getAnsweredCorrect()));
            return rdata;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error creating new test run: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }
    
    public void resetUser(int userId) throws CmRpcException {
        try {
            HaUser user = HaUser.lookUser(userId,null);
            user.setActiveTest(0);
            user.setActiveTestRunId(0);
            user.setActiveTestSegment(0);
            user.setActiveTestRunSession(0);
            
            user.update();
        }
        catch(Exception e) {
            throw new CmRpcException(e);
        }
    }

    /** Return the QuizHtml with the results stored
     *  as a list of pids that are correct.
     *  
     *  rpcData:  (all from QuizHtml) and quiz_result_json,quiz_correct_count,quiz_question_count
     */
    //@Override
    public RpcData getQuizResultsHtml(int runId) throws CmRpcException {
        try {
           HaTestRun testRun = HaTestRun.lookupTestRun(runId);
           List<HaTestRunResult> results = testRun.getTestRunResults();
           String resultJson = "";
           for(HaTestRunResult r: results) {
               if(resultJson.length() > 0)
                   resultJson += ",";
               resultJson += Jsonizer.toJson(r);
           }
           resultJson = "[" + resultJson + "]";
           
           RpcData quizRpc = getQuizHtml(testRun.getHaTest().getTestId());
           
           quizRpc.putData("quiz_result_json", resultJson);
           quizRpc.putData("quiz_question_count", testRun.getHaTest().getTestQuestionCount());
           quizRpc.putData("quiz_correct_count", testRun.getAnsweredCorrect());
           
           
           return quizRpc;
        }
        catch(Exception e) {
            throw new CmRpcException(e);
        }
    }
    
    public void setUserBackground(int userId, String backgroundStyle) throws CmRpcException {
        try {
            HaUser user = HaUser.lookUser(userId,null);
            user.setBackgroundStyle(backgroundStyle);
            user.update();
        }
        catch(Exception e) {
            throw new CmRpcException(e);
        }
    }
    
    public void saveFeedback(String comments, String commentsUrl, String stateInfo) throws CmRpcException {

        Connection conn = null;
        PreparedStatement pstat = null;

        try {

            String xml = null;

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
    
    
}






