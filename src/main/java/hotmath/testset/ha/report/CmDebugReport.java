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

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

public class CmDebugReport {
    
    static Logger __logger = Logger.getLogger(CmDebugReport.class);
    
    HaUser _user;
    List<StudentUserProgramModel>  _allPossiblePrograms;
    
    public CmDebugReport() throws Exception {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            int uid = CmTestUtils.setupDemoAccount();
            
            _user = HaUser.lookUser(conn, uid, null);

            
            /** assign every program to user and check for anomalies
             * 
             */
            for(CmProgram progDef: CmProgram.values()) {
                new CmStudentDao().assignProgramToStudent(conn,_user.getUid(),progDef,null);
            
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
                        __logger.error("Error creating test: " + progDef,e);
                        continue;
                    }
                   
                    /** for every distinct question in quiz
                     * 
                     */
                    List<String> pids = test.getPids();
                    
                    for(String pid: pids) {
                        
                        __logger.info("Testing pid: " + pid);
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
                            __logger.error("Program prescription has zero lessons: " + testRun);
                        }
                    }
                }
            }
            
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }
    
    public static void main(String[] args) {
        try {
            new CmDebugReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}