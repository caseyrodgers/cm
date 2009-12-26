package hotmath.testset.ha.report;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;
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

    HaUser _user;
    List<StudentUserProgramModel> _allPossiblePrograms;
    FileWriter _fileOut;
    ReportGui _rgui=null;
    public CmDebugReport(String logFile) throws Exception {
        
        /** first try to init gui, if fails move on...
         * 
         */
        try {
            //_rgui = new ReportGui();
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
            int uid = CmTestUtils.setupDemoAccount();

            _user = HaUser.lookUser(conn, uid, null);

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
        updateProgram(conn, progDef,null);
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
            updateProgram(conn, progDef, chapter);
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
        
    
        StudentUserProgramModel userProgram = new CmUserProgramDao().loadProgramInfoCurrent(conn, _user.getUid());
        
        logMessage("Testing program: " + userProgram);
        
        HaTestDef testDef = userProgram.getTestDef();

        /**
         * for each quiz in this program
         */
        for (int segment = 1; segment - 1 < testDef.getTotalSegmentCount(); segment++) {

            logMessage("Testing segment: " + segment);

            HaTest test = null;
            try {
                test = HaTestDao.createTest(conn, _user.getUid(), testDef, segment);
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
                HaTestRun testRun = HaTestDao.createTestRun(conn, _user.getUid(), test.getTestId(), wrongPids, 0,
                        1, 0);

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
    }
    
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
        }
        else {
            /** there are sessions, make search the RPP for each equals three
             * 
             */
            for(int i=0,t=prescription.getSessions().size();i<t;i++) {
                AssessmentPrescriptionSession session = prescription.getSessions().get(i);
                List<SessionData> rpp = session.getSessionItems();
                if(rpp.size() != 3) {
                    logMessage("Session " + i + ": incorrect number of RPP (" + rpp.size() + ")" );
                }
            }
        }
    }

    /** Assign this program to user
     * 
     * @param conn
     * @param progDef
     * @param chapter
     * @throws Exception
     */
    private void updateProgram(final Connection conn,CmProgram progDef, String chapter) throws Exception {
        new CmStudentDao().assignProgramToStudent(conn, _user.getUid(), progDef, chapter);
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
