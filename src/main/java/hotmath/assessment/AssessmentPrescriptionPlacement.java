package hotmath.assessment;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.NextAction;
import hotmath.gwt.cm_rpc.client.rpc.NextAction.NextActionName;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
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
    public NextAction getNextAction() throws Exception  {
        int correct = getTestRun().getAnsweredCorrect();
        int total = getTest().getNumTestQuestions();

        String thisTest = getTestRun().getHaTest().getSubTitle(getTest().getSegment()).toLowerCase();

        CmProgram program=null;
        
        NextAction nextAction = new NextAction();
        // some trigger, in this case > 1 wrong answers.
        if ((total - correct) > 1) {
            /** Sign user up for the current subject program.
                map to real Program name.
            */
            if (thisTest.indexOf("pre-algebra") > -1) {
                if(correct < 3) {
                    /** 
                     * 0-2 correct on first quiz --> Essentials Prof
                     */
                    program = CmProgram.ESSENTIALS;
                }
                else {
                    program = CmProgram.PREALG_PROF;    
                }
            } else if (thisTest.indexOf("algebra 1") > -1) {
                program = CmProgram.ALG1_PROF;
            } else if (thisTest.indexOf("geometry") > -1) {
                program = CmProgram.GEOM_PROF;
            } else if (thisTest.indexOf("algebra 2") > -1) {
                program = CmProgram.ALG2_PROF;
            }
        } else if (thisTest.indexOf("algebra 2") > -1) {
            /**
             * this means user passed the last test
             * assign to casshe as default
             * 
             */
            program = CmProgram.CAHSEEHM;
        }

        HaUser user = getTestRun().getHaTest().getUser();        
        if (program != null) {
            // next action is another quiz
            /** @TODO: Need to set test config json when assigning test
             * 
             */
            nextAction.setNextAction(NextActionName.AUTO_ASSSIGNED);
            CmStudentDao dao = new CmStudentDao();
            StudentModelI sm = dao.getStudentModel(user.getUid());
            
            sm.getProgram().setProgramType(program.getProgramType());
            sm.getProgram().setSubjectId(program.getSubject());
            sm.setProgramChanged(true);
            
            // now update the ActiveInfo to empty
            StudentActiveInfo active = new StudentActiveInfo();
            
            Connection conn=null;
            try {
                conn = HMConnectionPool.getConnection();
                
                dao.updateStudent(conn, sm,true,false, true, false, false);
                dao.setActiveInfo(conn, user.getUid(), active);
            }
            finally {
                SqlUtilities.releaseResources(null,null,conn);
            }
            nextAction.setAssignedTest(program.getTitle());
            nextAction.setAssignedTestId(active.getActiveTestId());
        }
        else {
            nextAction.setNextAction(NextActionName.QUIZ);
        }
        return nextAction;
    }
}