package hotmath.assessment;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.ui.NextAction;
import hotmath.gwt.cm_tools.client.ui.NextAction.NextActionName;
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
    public NextAction getNextAction()  {
        int correct = getTestRun().getAnsweredCorrect();
        int total = getTest().getNumTestQuestions();

        String thisTest = getTestRun().getHaTest().getSubTitle(getTest().getSegment()).toLowerCase();
        String newTestName = null;

        NextAction nextAction = new NextAction();
        // some trigger, in this case > 1 wrong answers.
        if ((total - correct) > 1) {
            // some action

            // Sign user up for the current subject program.
            // map to real Program name
            // @TODO: we need single mapping api for test names.
            if (thisTest.indexOf("pre-algebra") > -1) {
                newTestName = "Pre-algebra Proficiency";
            } else if (thisTest.indexOf("algebra 1") > -1) {
                newTestName = "Beginning Algebra Proficiency";
            } else if (thisTest.indexOf("geometry") > -1) {
                newTestName = "Geometry Proficiency";
            } else if (thisTest.indexOf("algebra 2") > -1) {
                newTestName = "Intermediate Algebra Proficiency";
            }
        } else if (thisTest.indexOf("algebra 2") > -1) {
            // this means user passed the last test
            // assign to casshe
            newTestName = "California State Exit Exam";
        }

        HaUser user = getTestRun().getHaTest().getUser();        
        if (newTestName != null) {
            // next action is another quiz
            /** @TODO: Need to set test config json when assigning test
             * 
             */
            nextAction.setNextAction(NextActionName.AUTO_ASSSIGNED);
            
            try {
                CmStudentDao dao = new CmStudentDao();
                StudentModel sm = dao.getStudentModel(user.getUid());
                
//                sm.set
//                dao.updateStudentProgram(sm);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            nextAction.setNextAction(NextActionName.QUIZ);
        }
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            // update the state of this user
            user.update(conn);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
        
        return nextAction;
    }
}