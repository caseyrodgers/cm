package hotmath.assessment;

import hotmath.HotMathException;
import hotmath.gwt.cm.client.ui.NextAction;
import hotmath.gwt.cm.client.ui.NextAction.NextActionName;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;
import sb.logger.SbLogger;

/**
 * Class to represent an assessment prescription for a given set of INMH items.
 * 
 * With a set of comma separated pids, find the union of all related INMH items.
 * 
 * @author Casey
 * 
 */
public class AssessmentPrescriptionPlacement extends AssessmentPrescription {

    public AssessmentPrescriptionPlacement(HaTestRun testRun) throws Exception {
        super(testRun);
        setTestRun(testRun);
    }

    /**
     * Create placement prescription that simply moves user to next test in
     * sequence.
     * 
     * If user missed 2 or more in any segment, then automatically enroll them
     * into that Program ... otherwise, move to next segment.
     * 
     * @return relative URL to the proper prescription page.
     */
    public String getPrescriptionUrl() {

        int correct = getTestRun().getAnsweredCorrect();
        int total = getTest().getNumTestQuestions();

        String thisTest = getTestRun().getHaTest().getSubTitle(getTest().getSegment()).toLowerCase();
        String newTestName = null;
        // some trigger, in this case > 1 wrong answers.
        if ((total - correct) > 1) {
            // some action

            // Sign user up for the current subject program.
            // map to real Program name
            // @TODO: we need single mapping api for test names.
            if (thisTest.indexOf("pre-algebra") > -1) {
                newTestName = "Pre-algebra Proficiency";
            } else if (thisTest.indexOf("algebra 1") > -1) {
                newTestName = "Algebra 1 Proficiency";
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

        if (newTestName != null) {
            // assign test
            try {
                HaUser user = getTestRun().getHaTest().getUser();
                user.setAssignedTestName(newTestName);
                user.setActiveTestRunId(0);
                user.setActiveTest(0);
                getTestRun().getHaTest().getUser().update();
            } catch (HotMathException hme) {
                SbLogger.postMessage(hme);
                return "SOME_ERROR_PAGE_EXPLAINING_PROBLEM";
            }
            return "placement-assigned.jsp?uid=" + testRun.getHaTest().getUser().getUid();
        }

        int curSeg = getTestRun().getHaTest().getSegment();
        int newSeg = curSeg + 1;

        return "testset_assessment.jsp?uid=" + testRun.getHaTest().getUser().getUid() + "&segment=" + newSeg;
    }

    @Override
    public NextAction getNextAction() {
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
            nextAction.setNextAction(NextActionName.AUTO_ASSSIGNED);
            user.setAssignedTestName(newTestName);
            user.setActiveTestSegment(0);
            user.setActiveTestRunId(0);
            user.setActiveTest(0);
            
            nextAction.setAssignedTest(newTestName);
        }
        else {
            nextAction.setNextAction(NextActionName.QUIZ);
        }
        
        try {
            // update the state of this user
            user.update();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return nextAction;
    }
}