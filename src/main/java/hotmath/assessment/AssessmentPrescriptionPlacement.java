package hotmath.assessment;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

/**
 * Class to represent an assessment prescription for a given set of INMH items.
 * 
 * With a set of comma separated pids, find the union of all related INMH items.
 * 
 * @author Casey
 * 
 */
public class AssessmentPrescriptionPlacement extends AssessmentPrescription {

    public AssessmentPrescriptionPlacement(final Connection conn, HaTestRun testRun) throws Exception {
        super(conn, testRun);
        setTestRun(testRun);
    }

    @Override
    public CmProgramFlowAction getNextAction() throws Exception  {
        int correct = getTestRun().getAnsweredCorrect();
        int total = getTest().getNumTestQuestions();

        String thisTest = getTestRun().getHaTest().getSubTitle(getTest().getSegment()).toLowerCase();

        CmProgram program=null;
        
        CmProgramFlowAction nextAction = new CmProgramFlowAction();
        // some trigger, in this case > 1 wrong answers.
        if ((total - correct) > 1) {
            /** Sign user up for the current subject program.
                map to real Program name.
            */
            if(thisTest.indexOf("essentials") > -1) {
                if(correct < 9) {
                    program = CmProgram.ESSENTIALS;
                }
            }
            else if (thisTest.indexOf("pre-algebra") > -1) {
                if(correct < 9) {
                    program = CmProgram.PREALG_PROF;    
                }
            } else if (thisTest.indexOf("algebra 1") > -1) {
                if(correct < 9) {
                    program = CmProgram.ALG1_PROF;
                }
            } else if (thisTest.indexOf("geometry") > -1) {
                if(correct < 9) {
                    program = CmProgram.GEOM_PROF;
                }
            } else if (thisTest.indexOf("algebra 2") > -1) {
                program = CmProgram.ALG2_PROF;
            }
        } else if (thisTest.indexOf("algebra 2") > -1) {
            /**
             * this means user passed the last test
             * assign to National as default
             * 
             */
            program = CmProgram.NATIONAL;
        }

        HaUser user = getTestRun().getHaTest().getUser();        

        if (program != null) {

            // next action is another quiz
            /** @TODO: Need to set test config json when assigning test
             * 
             */
            nextAction.setNextAction(CmPlace.AUTO_PLACEMENT);
            CmStudentDao dao = CmStudentDao.getInstance();
            
            // now update the ActiveInfo to empty
            StudentActiveInfo active = new StudentActiveInfo();
            
            // TODO: eliminate local Connection 
            Connection conn = super.conn;
            boolean useLocalConnection = (conn == null);
            
            long studentModelTime = -1;
            long updateStudentTime = -1;
            long activeInfoTime = -1;

            try {
                if (useLocalConnection) {
                	conn = HMConnectionPool.getConnection();
                }

                long startTime = System.currentTimeMillis();
                StudentModelI sm = dao.getStudentModelBase(conn, user.getUid(), false);
                studentModelTime = System.currentTimeMillis() - startTime;
                
                sm.getProgram().setProgramType(program.getProgramType());
                sm.getProgram().setSubjectId(program.getSubject());
                sm.setProgramChanged(true);
                sm.setSectionNum(0);
                
                startTime = System.currentTimeMillis();
                dao.updateStudent(conn, sm, true, false, true, false, false);
                updateStudentTime = System.currentTimeMillis() - startTime;
                
                startTime = System.currentTimeMillis();
                dao.setActiveInfo(conn, user.getUid(), active);  // clear out
                activeInfoTime = System.currentTimeMillis() - startTime;
            }
            finally {
            	logger.info(String.format("+++ getNextAction(): getStudentModel took: %d msec, updateStudent() took: %d msec, setActiveInfo() took: %d msec",
            			studentModelTime, updateStudentTime, activeInfoTime));
            	if (useLocalConnection) {
            		// TODO: eliminate local Connection
            		logger.info("=== getNextAction(): closing a local Connection", new Exception());
                    SqlUtilities.releaseResources(null,null,conn);
            	}
            }
            nextAction.setAssignedTest(program.getTitle());
            nextAction.setAssignedTestId(active.getActiveTestId());
        }
        else {
            nextAction.setNextAction(CmPlace.QUIZ);
        }
        return nextAction;
    }
}