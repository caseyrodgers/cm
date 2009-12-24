package hotmath.testset.ha.report;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefFactory;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.FileWriter;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

public class CmDebugReport {
    
    static Logger __logger = Logger.getLogger(CmDebugReport.class);
    
    HaUser _user;
    List<StudentUserProgramModel>  _allPossiblePrograms;
    FileWriter _fileOut;
    
    public CmDebugReport() throws Exception {
        Connection conn=null;
        _fileOut = new FileWriter(CmDebugReport.class.getName() + ".log");
        try {
            conn = HMConnectionPool.getConnection();
            int uid = CmTestUtils.setupDemoAccount();
            
            _user = HaUser.lookUser(conn, uid, null);

            
            /** assign every program to user and check for anomalies
             * 
             */
            for(CmProgram progDef: CmProgram.values()) {
                
                try {
                    new CmStudentDao().assignProgramToStudent(conn,_user.getUid(),progDef,null);
                }
                catch(Exception e) {
                   logMessage("error assigning program to user: " + progDef);
                    continue;
                }
            
                __logger.info("Testing program: " + progDef);
                
                _user = HaUser.lookUser(conn, uid, null);

                HaTestDef testDef = HaTestDefFactory.createTestDef(conn, progDef.getDefId());
                
                /** 
                 * for each quiz in this program
                 */
                for(int segment=1;segment+1< testDef.getTotalSegmentCount();segment++) {
                    
                    __logger.info("Testing segment: " + segment);
                    
                    HaTest test = null;
                    try {
                        test = HaTestDao.createTest(conn, _user.getUid(),testDef,segment);
                    }
                    catch(Exception e) {
                       logMessage("Error creating test: " + progDef,e);
                        continue;
                    }
                   
                    /** for every distinct question in quiz
                     * 
                     */
                    List<String> pids = test.getPids();
                    
                    for(String pid: pids) {
                        
                        logMessage("Testing pid: " + pid);
                        /** create a prescription marking this question as incorrect
                         *  in order to create a related prescription.
                         * 
                         */
                        
                        /** first create the test run
                         * 
                         */
                        String wrongPids[] = {pid};
                        HaTestRun testRun = HaTestDao.createTestRun(conn, _user.getUid(), test.getTestId(), wrongPids,0, 1, 0);
                        
                        
                        /** then the prescription
                         * 
                         */
                        AssessmentPrescription prescription = AssessmentPrescriptionManager.getInstance().getPrescription(conn, testRun.getRunId());
                        
                        
                        /** check for anomalies
                         * 
                         */
                        if(prescription.getSessions().size() == 0) {
                           logMessage("Program prescription has zero lessons: " + testRun);
                        }
                    }
                }
            }
            logMessage("Prescription Check Complete");
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
            _fileOut.close();
        }
    }

    
    private void logMessage(String msg) throws Exception  {
        _fileOut.write(msg);
    }
    
    private void logMessage(String msg, Throwable t) throws Exception  {
        t.printStackTrace();
        logMessage(msg + ", " + t.getMessage());
    }

    public static void main(String[] args) {
        try {
            new CmDebugReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}