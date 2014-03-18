package hotmath.assessment;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.testset.ha.HaTestDefPlacement;
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

        
        CmProgramFlowAction nextAction = new CmProgramFlowAction();
        
        CmProgram program=((HaTestDefPlacement)getTest().getTestDef()).getNextProblem(thisTest, total, correct);
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

                // handle Parallel Program transitions
				ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
				boolean resetMainProgram = (ppDao.isStudentInParallelProgram(sm.getUid()) == false);
				boolean continueParallelProgram = ! resetMainProgram;

                dao.updateStudent(conn, sm, true, false, true, false, false, resetMainProgram, continueParallelProgram);
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