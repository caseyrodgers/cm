package hotmath.testset.ha.report;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.server.service.CmTestUtils;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

public class CmDebugReport {

    static Logger __logger = Logger.getLogger(CmDebugReport.class);
    FileWriter _fileOut;
    ReportGui _rgui=null;
    Integer _uid;
    
    public CmDebugReport(String logFile) throws Exception {
        
        /** first try to init gui, if fails move on...
         * 
         */
        try {
            String os = (String)System.getProperties().get("os.name");
            if(os.toLowerCase().contains("windows"))
                _rgui = new ReportGui();
        }
        catch(Error th) {
           th.printStackTrace();   
        }

        
        if(logFile == null)
            logFile = CmDebugReport.class.getName() + ".log";
        
        Connection conn = null;
        _fileOut = new FileWriter(logFile);
        try {
            conn = HMConnectionPool.getConnection();
            /**
             * assign every program to user and check for anomalies
             * 
             */
            for (CmProgram progDef : CmProgram.values()) {
                try {
                    if (progDef.getProgramId().equals("Chap")) {
                        testProgramChapterTests(conn, progDef);
                    } else {
                        testProgramProfTests(conn, progDef);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    logMessage("Error testing: " + progDef + ", " + e.getMessage());
                }
            }
            logMessage("Prescription Check Complete");
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
            _fileOut.close();
        }
    }
    
    
    
    
    private void testProgramProfTests(final Connection conn, CmProgram progDef) throws Exception {
        setupNewUserAndProgram(conn, progDef,null);
        testCurrentlyAssignedProgram(conn);
    }
        

    /** run test on each chapter in this program
     * 
     * @param conn
     * @param progDef
     * @throws Exception
     */
    private void testProgramChapterTests(final Connection conn, CmProgram progDef) throws Exception {
        HaTestDefDao dao = new HaTestDefDao(); 
        List<String> chapters = dao.getProgramChapters(dao.getTestDef(conn, progDef.getDefId()));

        for(String chapter: chapters) {
            setupNewUserAndProgram(conn, progDef, chapter);
            testCurrentlyAssignedProgram(conn);
        }
    }

    
    /** test the currently assigned program by create a prescription for 
     * every solution assigned and looking for anomolies.
     * 
     * 
     * @param conn
     * @throws Exception
     */
    private void testCurrentlyAssignedProgram(final Connection conn) throws Exception {
        
        _errorCount=0;
    
        StudentUserProgramModel userProgram = new CmUserProgramDao().loadProgramInfoCurrent(conn, _uid);
        
        logMessage("Testing program: " + userProgram);
        
        HaTestDef testDef = userProgram.getTestDef();

        /**
         * for each quiz in this program
         */
        for (int segment = 1; segment - 1 < testDef.getTotalSegmentCount(); segment++) {

            logMessage("Testing segment: " + segment);

            HaTest test = null;
            try {
                test = HaTestDao.createTest(conn, _uid, testDef, segment);
            } catch (Exception e) {
                logMessage("Error creating test: " + userProgram, e);
                continue;
            }

            /**
             * for every distinct question in quiz
             * 
             */
            List<String> pids = test.getPids();

            for (String pid : pids) {

                logMessage("Testing pid: " + pid);
                /**
                 * create a prescription marking this question as incorrect
                 * in order to create a related prescription.
                 * 
                 */

                /**
                 * first create the test run based on ONE pid
                 * 
                 */
                String wrongPids[] = { pid };
                HaTestRun testRun = HaTestDao.createTestRun(conn, _uid, test.getTestId(), wrongPids, 0,1, 0);

                /**
                 * then the prescription by missing the ONE pid
                 * 
                 */
                AssessmentPrescription prescription = AssessmentPrescriptionManager.getInstance().getPrescription(
                        conn, testRun.getRunId());

                /**
                 * check for anomalies
                 * 
                 */
                checkPrescription(prescription);
            }
        }
        
        if(_errorCount == 0) {
            StudentModel sm = new StudentModel();
            sm.setUid(_uid);
            new CmStudentDao().removeUser(conn, sm);
        }
        else {
            logMessage("!ERRORS OCCURRED: check user: " + _uid);
        }
    }
    
    Integer _errorCount;
    /** Perform checks for prescription to make sure it is valid
     * 
     * @param prescription
     * @throws Exception
     */
    private void checkPrescription(AssessmentPrescription prescription) throws Exception {
        
        /** make sure their is at least one session created
         * 
         */
        if (prescription.getSessions().size() == 0) {
            logMessage("Program prescription has zero lessons: " + prescription);
            _errorCount++;
        }
        else {
            /** there are sessions, make search the RPP for each equals three
             * 
             */
            for(int i=0,t=prescription.getSessions().size();i<t;i++) {
                AssessmentPrescriptionSession session = prescription.getSessions().get(i);
                List<SessionData> rpp = session.getSessionItems();
                if(rpp.size() != 3) {
                    logMessage("ERROR: Session " + i + ": incorrect number of RPP (" + rpp.size() + ")" );
                    _errorCount++;
                }
            }
        }
    }

    /** Create a new user and assigned named program to it.
     * 
     *  Having a user for each test allows for easier debugging
     *  when problems arise.
     *  
     *  The user should be deleted if no anomalies are found.
     * 
     * @param conn
     * @param progDef
     * @param chapter
     * @throws Exception
     */
    private void setupNewUserAndProgram(final Connection conn,CmProgram progDef, String chapter) throws Exception {
        _uid = CmTestUtils.setupDemoAccount();
        new CmStudentDao().assignProgramToStudent(conn, _uid, progDef, chapter);
    }

    
    private void logMessage(String msg) throws Exception {
        _fileOut.write(msg + "\n");
        
        if(_rgui != null)
            _rgui.logMessage(msg);
    }

    private void logMessage(String msg, Throwable t) throws Exception {
        t.printStackTrace();
        logMessage(msg + ", " + t.getMessage());
    }

    public static void main(String[] args) {
        try {
            String logFile = null;
            if(args.length > 0)
                logFile = args[0];
            new CmDebugReport(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



class ReportGui extends JFrame {
    JTextArea _textArea = new JTextArea();
    DateFormat _dateFormat = SimpleDateFormat.getTimeInstance();
    public ReportGui() {
        setSize(640,480);
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
