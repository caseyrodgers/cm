package hotmath.testset.ha.report;

import hotmath.SolutionManager;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.InmhItemData;
import hotmath.assessment.Range;
import hotmath.assessment.RppWidget;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.awt.BorderLayout;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/**
 * Checks all CM Programs for anomalies.
 *
 * Writes to table HA_PRESCRIPTION_LOG
 *
 * Also populates the HA_PROGRAM_LESSON table which contains all the ACTIVE
 * lessons currently in play.
 *
 *
 * @author casey
 *
 */
public class PrescriptionReport {

    static Logger __logger = Logger.getLogger(PrescriptionReport.class);
    FileWriter _fileOut;
    ReportGui _rgui = null;
    Integer _uid;
    Connection _conn;

    public PrescriptionReport(String logFile, CmProgram programToTest) throws Exception {

        /**
         * first try to init gui, if fails move on...
         *
         */
        try {
            String os = (String) System.getProperties().get("os.name");
            if (os.toLowerCase().contains("windows"))
                _rgui = new ReportGui();
        } catch (Error th) {
            th.printStackTrace();
        }

        if (logFile != null) {
            logFile = PrescriptionReport.class.getName() + ".log";
            _fileOut = new FileWriter(logFile);
        }

        _uid = CmTestUtils.setupDemoAccount(CmProgram.PREALG_PROF);

        try {
            _conn = HMConnectionPool.getConnection();

            setupDatabaseForTest();

            if (programToTest != null) {
                if(programToTest.getProgramType().equals("Chap")) {
                    testProgramChapterTests(programToTest);
                }
                else {
                    testProgramProfTests(programToTest);
                }
            } else {

                /**
                 * assign every program to user and check for anomalies
                 *
                 * Do not process Auto Enroll tests
                 *
                 */
                for (CmProgram progDef : CmProgram.values()) {
                    if (!progDef.isActive() && !progDef.getProgramType().equals("Other"))
                        continue;
                    try {
                        if (progDef.getProgramType().equals("Chap")) {
                            testProgramChapterTests(progDef);
                        } else if (!progDef.getProgramType().equals("Auto Enroll")) {
                            testProgramProfTests(progDef);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logMessage(-1, "Error testing: " + progDef + ", " + e.getMessage());
                    }
                }
            }
            logMessage(-1, "Prescription Check Complete");
        } finally {
            SqlUtilities.releaseResources(null, null, _conn);
            if (_fileOut != null)
                _fileOut.close();
        }
    }

    private void testProgramProfTests(CmProgram progDef) throws Exception {
        setupNewUserAndProgram(progDef, null);
        testCurrentlyAssignedProgram(_conn);
    }

    /**
     * run test on each chapter in this program
     *
     * @param conn
     * @param progDef
     * @throws Exception
     */
    private void testProgramChapterTests(CmProgram progDef) throws Exception {
        HaTestDefDao dao = HaTestDefDao.getInstance();
        List<String> chapters = dao.getProgramChapters(_conn, dao.getTestDef(progDef.getDefId()));

        // chapters = Arrays.asList(chapters.get(6));

        for (String chapter : chapters) {
            setupNewUserAndProgram(progDef, chapter);
            testCurrentlyAssignedProgram(_conn);
        }
    }

    /**
     * test the currently assigned program by creating a prescription for every
     * assigned solution and looking for anomalies.
     *
     *
     * @param conn
     * @throws Exception
     */
    private void testCurrentlyAssignedProgram(final Connection conn) throws Exception {

        StudentUserProgramModel userProgram = CmUserProgramDao.getInstance().loadProgramInfoCurrent(_uid);

        logMessage(-1, "Testing program: " + userProgram);

        HaTestDef testDef = userProgram.getTestDef();

        CmStudentDao sda = CmStudentDao.getInstance();
        StudentModelI sm = sda.getStudentModel(userProgram.getUserId());
        int altTests = testDef.getNumAlternateTests();
        if (altTests == 0)
            altTests = 1;

        for (int altTest = 0; altTest < altTests; altTest++) {
            logMessage(-1, "Testing alternate test: " + testDef + " " + altTest);
            StudentActiveInfo activeInfo = sda.loadActiveInfo(userProgram.getUserId());
            activeInfo.setActiveSegmentSlot(altTest);
            sda.setActiveInfo(conn, userProgram.getUserId(), activeInfo);

            /**
             * for each quiz in this program
             */
            for (int segment = 1; segment - 1 < testDef.getTotalSegmentCount(); segment++) {

                logMessage(-1, "Testing segment: " + segment);

                HaTest test = null;
                try {
                    test = HaTestDao.getInstance().createTest(_uid, testDef, segment);
                } catch (Exception e) {
                    logMessage(-1, "Error creating test: " + userProgram, e);
                    continue;
                }

                /**
                 * for every distinct question in quiz
                 *
                 */
                List<String> pids = test.getPids();

                for (String pid : pids) {
                    
                    /**
                     * Quick way to test a single pid if(!pid.equals(
                     * "nationalhm2_CourseTest_1_PracticeTest_63_1")) continue;
                     */

                    logMessage(-1, "Testing pid: " + pid);
                    /**
                     * create a prescription marking this question as incorrect
                     * in order to create a related prescription.
                     *
                     */

                    /**
                     * mark all questions as correct
                     *
                     */
                    for (String p : pids) {
                        HaTestDao.saveTestQuestionChange(_conn, test.getTestId(), p, 0, true);
                    }

                    /**
                     * mark the one pid as Incorrect
                     *
                     */
                    HaTestDao.saveTestQuestionChange(_conn, test.getTestId(), pid, 0, false);

                    /**
                     * create the test run based on ONE pid
                     *
                     */
                    HaTestRun testRun = HaTestDao.getInstance().createTestRun(_conn, _uid, test.getTestId(), 0, 1, 0);

                    /**
                     * then the prescription by missing the ONE pid
                     *
                     */
                    AssessmentPrescription prescription = AssessmentPrescriptionManager.getInstance().getPrescription(_conn, testRun.getRunId());
                    
                    List<InmhItemData> itemsData = prescription.getAssessment().getInmhItemUnion("review");
                    for (InmhItemData itemData : itemsData) {
                        List<RppWidget> poolItems = itemData.getWidgetPool(conn,"prescription_report");
                        if(itemData.getInmhItem().getFile().contains("index_hotmath_review_full.html")) {
                            System.out.print("index review full");
                        }
                        else if(poolItems.size() == 0) {
                            logMessage(testRun.getRunId(),"WARNING: No RPP pool found for '" + itemData);
                        }
                        else if(prescription.filterRppsByGradeLevel(prescription.getGradeLevel(), poolItems, itemData, true).size() == 0) {
                            logMessage(testRun.getRunId(),"WARNING: No RPP items (level=" + prescription.getGradeLevel() + ") found for '" + itemData.getInmhItem().getFile());    
                        }
                    }

                    checkPrescription(_conn, prescription, pid);

                    cleanUpTest(conn, test.getTestId());
                }
            }
        }
    }

    /**
     * Make sure there are no TEST_RUNS associated with this test.
     *
     * @param conn
     * @param testId
     * @throws Exception
     */
    private void cleanUpTest(final Connection conn, int testId) throws Exception {
        conn.createStatement().executeUpdate("delete from HA_TEST_RUN where test_id = " + testId);
    }

    /**
     * Perform checks for prescription to make sure it is valid
     *
     * Test:
     *
     * 1. 3 RPP per session 2. each RPP exists 3. 3 EPP per session 4. each EPP
     * exists
     *
     * @param prescription
     * @throws Exception
     */
    private Boolean checkPrescription(final Connection conn, AssessmentPrescription prescription, String quizQuestionPid)
            throws Exception {

        boolean isError = false;
        /**
         * make sure there is at least one session created
         *
         */
        if (prescription.getSessions().size() == 0) {
            String pidList = prescription.getTestRun().getPidList();
            logMessage(prescription.getTestRun().getRunId(), "WARNING: Program prescription has zero lessons ("
                    + pidList + ")");
            isError = true;
        } else {

            /**
             * store the name of this lesson as being 'active', meaning it is
             * referenced by at least one RPP.
             */
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("insert into HA_PROGRAM_LESSONS(lesson,subject,file,pid)values(?,?,?,?)");

                /**
                 * there are sessions, make search: --
                 *
                 * if RPP there are three --
                 *
                 * if RPA there is at least one.
                 *    .. and there are RPPs too.
                 *
                 *
                 *
                 */
                for (int i = 0, t = prescription.getSessions().size(); i < t; i++) {


                    AssessmentPrescriptionSession session = prescription.getSessions().get(i);
                    List<SessionData> rpps = session.getSessionItems();


                    boolean flashRequired=false;
                    for (SessionData p : rpps) {
                        if (p.getRpp().isFlashRequired()) {
                            flashRequired=true;
                            /**
                             * is a flash widget
                             *
                             * TODO: validate JSON and activity.
                             * */

                            String widgetJson = p.getRpp().getWidgetJsonArgs();
                            if(widgetJson == null || widgetJson.length() == 0 || widgetJson.charAt(0) != '{') {
                                logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i + ": RPA has invalid widget JSON '" + p.getRpp().getWidgetJsonArgs() + "'");
                            }

                        } else {
                            String pid = p.getRpp().getFile(); 
                            if (!SolutionManager.getInstance().doesSolutionExist(conn, SbUtilities.getToken(pid,1, "$"))) {
                                logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i
                                        + ": RPP does not exist '" + p.getRpp().getFile() + "'");
                            }
                        }
                    }


                    if(flashRequired) {
                        /**
                         * make sure there is a non flash prescription as well
                         */

                        AssessmentPrescription noFlashPres = new AssessmentPrescription(conn,prescription.getTestRun(), new ClientEnvironment(false));
                        if(noFlashPres.getSessions().size() == 0 || noFlashPres.getSessions().get(0).getSessionItems().size() == 0) {
                            String lessonFile = session.getInmhItemsFor(session.getTopic()).get(0).getFile();
                            String sessionInfo = String.format("%s (%d) [question pid: %s] (%s)",  prescription.getTest().getTitle(), prescription.getTest().getSegment(), quizQuestionPid, lessonFile);
                            logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i + ": no non-flash content found for '" + sessionInfo + "'");
                        }
                    }



                    Collection<INeedMoreHelpResourceType> epp = session.getPrescriptionInmhTypes(_conn, "cmextra");
                    /**
                     * if (epp.size() > 0 && epp.size() != 3) {
                     * logMessage(prescription.getTestRun().getRunId(),
                     * "WARNING: Session " + i + ": incorrect number of EPP (" +
                     * epp.size() + ")"); isError = true; }
                     */
                    for (INeedMoreHelpResourceType p : epp) {
                        for (INeedMoreHelpItem pid : p.getResources()) {
                            
                            if (!SolutionManager.getInstance().doesSolutionExist(conn, new Range(pid.getFile()).getRange())) {
                                logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i
                                        + ": RPP does not exist '" + pid.getFile() + "'");
                            }
                        }
                    }

                    /**
                     * save the name of this lesson as being active
                     */
                    String title = session.getTopic();
                    ps.setString(1, title);
                    ps.setString(2, prescription.getTest().getTestDef().getSubjectId());
                    ps.setString(3, session.getInmhItemsFor(title).get(0).getFile());
                    ps.setString(4, quizQuestionPid);
                    if (ps.executeUpdate() != 1)
                        throw new Exception("Could not save new active lesson name: " + prescription);
                }
            } finally {
                SqlUtilities.releaseResources(null, ps, null);
            }
        }

        return isError;
    }

    /**
     * Create a new user and assigned named program to it.
     *
     * Having a user for each test allows for easier debugging when problems
     * arise.
     *
     * @param conn
     * @param progDef
     * @param chapter
     * @throws Exception
     */
    private void setupNewUserAndProgram(CmProgram progDef, String chapter) throws Exception {
        CmStudentDao.getInstance().assignProgramToStudent(_conn, _uid, progDef, chapter);
    }

    private void logMessage(int runId, String msg) throws Exception {

        /**
         * log to output file
         *
         */
        if (_fileOut != null)
            _fileOut.write(msg + "\n");

        /**
         * log to gui if available
         *
         */
        if (_rgui != null)
            _rgui.logMessage(msg);

        /**
         * log to database
         *
         */
        PreparedStatement ps = null;
        try {
            ps = _conn
                    .prepareStatement("insert into HA_PRESCRIPTION_LOG(run_id,message,message_time)values(?,?,now())");
            ps.setInt(1, runId);
            ps.setString(2, msg);
            ps.executeUpdate();
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }

        System.out.println("PrescriptionReport: " + runId + ", " + msg);
    }

    private void logMessage(int runId, String msg, Throwable t) throws Exception {
        t.printStackTrace();
        logMessage(runId, msg + ", " + t.getMessage());
    }

    /**
     * create/recreate the HA_PRESCRIPTION_LOG table and the HA_PROGRAM_LESSON
     * table
     *
     * @throws Exception
     */
    private void setupDatabaseForTest() throws Exception {
        Statement ps = null;
        try {
            ps = _conn.createStatement();

            try {
                ps.executeUpdate("drop table HA_PRESCRIPTION_LOG");
            } catch (Exception e) {
                /** silent */
            }
            try {
                ps.executeUpdate("drop table HA_PROGRAM_LESSONS");

            } catch (Exception e) {
                /** silent */
            }

            String sql = "create table HA_PRESCRIPTION_LOG " + "(id integer auto_increment not null primary key,"
                    + " run_id integer, message text,message_time datetime)";
            ps.executeUpdate(sql);

            sql = "create table HA_PROGRAM_LESSONS( " + "id integer auto_increment not null primary key, "
                    + "lesson varchar(100) not null, " + " file varchar(100) not null, "
                    + " pid varchar(100) not null, " + " subject varchar(100) not null)";
            ps.executeUpdate(sql);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    public static void main(String[] args) {
        try {
            String logFile = null;
            String programName = null;
            CmProgram program = null;

            for (String s : args) {
                if (s.startsWith("-log="))
                    logFile = s.split("=")[1];
                else if (s.startsWith("-program=")) {
                    programName = s.split("=")[1];
                }
            }

            if (programName != null) {

                if(!programName.equalsIgnoreCase("ALL")) {
                    for (CmProgram p : CmProgram.values()) {
                        if (p.name().equalsIgnoreCase(programName)) {
                            __logger.info("Only checking program: " + p);
                            program = p;
                            break;
                        }
                    }

                    if(program == null) {
                        throw new Exception("-program specified unknown program: '" + programName + "'");
                    }
                }
            }
            __logger.info("PrescriptionReport: Starting");
            new PrescriptionReport(logFile, program);
            __logger.info("PrescriptionReport complete successfully!");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ReportGui extends JFrame {
    JTextArea _textArea = new JTextArea();
    DateFormat _dateFormat = SimpleDateFormat.getTimeInstance();

    public ReportGui() {
        setSize(640, 480);
        setTitle("Cm Prescription Debug Report");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add("Center", new JScrollPane(_textArea));
        setVisible(true);
    }

    public void logMessage(String msg) {
        _textArea.append(_dateFormat.format(new Date()) + ": " + msg + "\n");
        _textArea.setCaretPosition(_textArea.getDocument().getLength());
    }
}
