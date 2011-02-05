package hotmath.testset.ha.report;

import hotmath.SolutionManager;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.ActionInfo;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.testset.ha.CmProgram;
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



/** Checks all CM Programs for anomalies.  
 * 
 * Writes to table HA_PRESCRIPTION_LOG
 * 
 * Also populates the HA_PROGRAM_LESSON table which 
 * contains all the ACTIVE lessons currently in play.
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

            
            if(programToTest != null) {
                testProgramProfTests(programToTest);    
            }
            else {
                
                /**
                 * assign every program to user and check for anomalies
                 * 
                 * Do not process Auto Enroll tests
                 * 
                 */
                for (CmProgram progDef : CmProgram.values()) {
                    try {
                        if(!progDef.isActive())
                            continue;
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
            if(_fileOut != null)
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
        HaTestDefDao dao = new HaTestDefDao();
        List<String> chapters = dao.getProgramChapters(_conn,dao.getTestDef(_conn, progDef.getDefId()));

        //chapters = Arrays.asList(chapters.get(6));
        
        for (String chapter : chapters) {
            setupNewUserAndProgram(progDef, chapter);
            testCurrentlyAssignedProgram(_conn);
        }
    }

    /**
     * test the currently assigned program by creating a prescription
     * for every assigned solution and looking for anomalies.
     * 
     * 
     * @param conn
     * @throws Exception
     */
    private void testCurrentlyAssignedProgram(final Connection conn) throws Exception {

        StudentUserProgramModel userProgram = new CmUserProgramDao().loadProgramInfoCurrent(_conn, _uid);

        logMessage(-1, "Testing program: " + userProgram);

        HaTestDef testDef = userProgram.getTestDef();

        
        CmStudentDao sda = new CmStudentDao();
        StudentModelI sm = sda.getStudentModel(userProgram.getUserId());
        int altTests = testDef.getNumAlternateTests();
        if(altTests == 0)
            altTests = 1;
        
        for(int altTest=0;altTest < altTests;altTest++) {
            logMessage(-1,"Testing alternate test: " + testDef + " " + altTest);
            StudentActiveInfo activeInfo = sda.loadActiveInfo(conn, userProgram.getUserId());
            activeInfo.setActiveSegmentSlot(altTest);
            sda.setActiveInfo(conn, userProgram.getUserId(), activeInfo);
            
            /**
             * for each quiz in this program
             */
            for (int segment = 1; segment - 1 < testDef.getTotalSegmentCount(); segment++) {
                
                logMessage(-1, "Testing segment: " + segment);
    
                HaTest test = null;
                try {
                    test = HaTestDao.createTest(_conn, _uid, testDef, segment);
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
    
                    if(!pid.equals("nationalhm2_CourseTest_1_PracticeTest_63_1"))
                        continue;
                    
                    System.out.println("HERE");
                    
                    logMessage(-1, "Testing pid: " + pid);
                    /**
                     * create a prescription marking this question as incorrect in
                     * order to create a related prescription.
                     * 
                     */
    
                    /**
                     * mark all others as correct
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
                     * first create the test run based on ONE pid
                     * 
                     */
                    HaTestRun testRun = HaTestDao.createTestRun(_conn, _uid, test.getTestId(), 0, 1, 0);
    
                    /**
                     * then the prescription by missing the ONE pid
                     * 
                     */
                    AssessmentPrescription prescription = AssessmentPrescriptionManager.getInstance().getPrescription(_conn,
                            testRun.getRunId());
                    
                    checkPrescription(_conn,prescription,pid);
                }
            }
        }
    }

    /**
     * Perform checks for prescription to make sure it is valid
     * 
     * Test:
     * 
     * 1. 3 RPP per session
     * 2. each RPP exists
     * 3. 3 EPP per session
     * 4. each EPP exists
     * 
     * @param prescription
     * @throws Exception
     */
    private Boolean checkPrescription(final Connection conn, AssessmentPrescription prescription,String quizQuestionPid) throws Exception {

        boolean isError = false;
        /**
         * make sure there is at least one session created
         * 
         */
        if (prescription.getSessions().size() == 0) {
            String pidList = prescription.getTestRun().getPidList();
            logMessage(prescription.getTestRun().getRunId(), "WARNING: Program prescription has zero lessons (" + pidList + ")");
            isError = true;
        } else {
            
            
            /** store the name of this lesson as being 'active', meaning it is referenced by
             *  at least one RPP.
             */
            PreparedStatement ps=null;
            try {
                ps = conn.prepareStatement("insert into HA_PROGRAM_LESSONS(lesson,subject,file,pid)values(?,?,?,?)");
                
                /**
                 * there are sessions, make search:
                 *  -- if RPP there are three
                 *  -- if RPA there is at least one.
                 * 
                 */
                for (int i = 0, t = prescription.getSessions().size(); i < t; i++) {
                    AssessmentPrescriptionSession session = prescription.getSessions().get(i);
                    List<SessionData> rpp = session.getSessionItems();
                    if(!session.isRpa() && rpp.size() != 3) {
                        logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i + ": incorrect number of RPP (" + rpp.size() + ")");
                        isError = true;
                    }
                    for(SessionData p: rpp) {
                    	if(p.getWidgetArgs() != null) {
                    		/** is a flash widget 
                    		 * 
                    		 * TODO: validate JSON and activity.
                    		 * */
                    	}
                    	else {
                    		if(!SolutionManager.getInstance().doesSolutionExist(conn, p.getPid())) {
                    			logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i + ": RPP does not exist '" + p.getPid() + "'");
                    		}
                    	}
                    }
                    
                    Collection<INeedMoreHelpResourceType> epp = session.getPrescriptionInmhTypes(_conn, "cmextra");
                    /**
                    if (epp.size() > 0 && epp.size() != 3) {
                        logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i + ": incorrect number of EPP (" + epp.size() + ")");
                        isError = true;
                    }
                    */
                    for(INeedMoreHelpResourceType p: epp) {
                        for(INeedMoreHelpItem pid: p.getResources()) {
                            if(!SolutionManager.getInstance().doesSolutionExist(conn, pid.getFile())) {
                                logMessage(prescription.getTestRun().getRunId(), "WARNING: Session " + i + ": RPP does not exist '" + pid.getFile() + "'");
                            }
                        }
                    }
                    
                    /** save the name of this lesson as being active
                     */
                    String title = session.getTopic();
                    ps.setString(1, title);
                    ps.setString(2, prescription.getTest().getTestDef().getSubjectId());
                    ps.setString(3,session.getInmhItemsFor(title).get(0).getFile());
                    ps.setString(4, quizQuestionPid);
                    if(ps.executeUpdate() != 1)
                        throw new Exception("Could not save new active lesson name: " + prescription);
                }
            }
            finally {
                SqlUtilities.releaseResources(null,ps,null);
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
        new CmStudentDao().assignProgramToStudent(_conn, _uid, progDef, chapter);
    }

    private void logMessage(int runId, String msg) throws Exception {
        
        /** log to output file
         * 
         */
        if(_fileOut != null)
            _fileOut.write(msg + "\n");
        
        
        /** log to gui if available
         * 
         */
        if (_rgui != null)
            _rgui.logMessage(msg);
        
        
        /** log to database
         * 
         */
        PreparedStatement ps = null;
        try {
            ps = _conn.prepareStatement("insert into HA_PRESCRIPTION_LOG(run_id,message,message_time)values(?,?,now())");
            ps.setInt(1,runId);
            ps.setString(2, msg);
            ps.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        
        System.out.println("PrescriptionReport: " + runId + ", " + msg);
    }

    private void logMessage(int runId, String msg, Throwable t) throws Exception {
        t.printStackTrace();
        logMessage(runId, msg + ", " + t.getMessage());
    }

    /** create/recreate the HA_PRESCRIPTION_LOG table
     *  and the HA_PROGRAM_LESSON table 
     * 
     * @throws Exception
     */
    private void setupDatabaseForTest() throws Exception {
        Statement ps=null;
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
            
            String sql = "create table HA_PRESCRIPTION_LOG " + 
                         "(id integer auto_increment not null primary key," + 
                         " run_id integer, message text,message_time datetime)";
            ps.executeUpdate(sql);
            
            sql = "create table HA_PROGRAM_LESSONS( " +
                  "id integer auto_increment not null primary key, " + 
                  "lesson varchar(100) not null, " +
                  " file varchar(100) not null, " +
                  " pid varchar(100) not null, " +
                  " subject varchar(100) not null)";
            ps.executeUpdate(sql);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }

    public static void main(String[] args) {
        try {
            String logFile = null;
            String programName=null;
            CmProgram program=null;





            for(String s: args) {
            	if(s.startsWith("-log="))
            		logFile = s.split("=")[1];
            	else if(s.startsWith("-program=")) {
            		programName = s.split("=")[1];
            	}
            }

            if(programName != null) {
            	for(CmProgram p: CmProgram.values()) {
            		if(p.name().equalsIgnoreCase(programName) ) {
            			__logger.info("Only checking program: " + p);
            			program = p;
            			break;
            		}
            	}
            }
            __logger.info("PrescriptionReport: Starting");
            new PrescriptionReport(logFile,program);
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
