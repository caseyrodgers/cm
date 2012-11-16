package hotmath.cm.program;

import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.server.service.command.CreateTestRunCommand;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetQuizHtmlCommand;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * abstraction of a Catchup Math program 'flow'.
 * 
 * It provides methods that determine if the current program is valid, if the
 * current program is complete.
 * 
 * And most importantly, how to advance the program.
 * 
 * 
 * Each program is made up of one or more segments. Each segment is one quiz
 * followed by zero or more lessons depending on how many correct questions are
 * entered. 
 * 
 * Either the quiz or the lessons can be missing (but not both).
 * 
 * @author casey
 * 
 */
public class CmProgramFlow {

    final static Logger __logger = Logger.getLogger(CmProgramFlow.class);

    StudentUserProgramModel userProgram;
    CmStudentDao sdao = CmStudentDao.getInstance();
    CmUserProgramDao updao = CmUserProgramDao.getInstance();
    StudentActiveInfo activeInfo;
    StudentModelI student;

    public CmProgramFlow(final Connection conn, int userId) throws Exception {
    	
        this.activeInfo = sdao.loadActiveInfo(userId);

        this.userProgram = updao.loadProgramInfoCurrent(userId);

        this.student = sdao.getStudentModel(conn, userId, true);

        /**
         * make the program segment 1 based
         */
        if (this.activeInfo.getActiveSegment() < 1) {
            this.activeInfo.setActiveSegment(1);
            sdao.setActiveInfo(conn, userId, activeInfo);
        }

        /**
         * make sure the current state is valid, if not try to synchronize.
         * 
         */
        isValid();
    }

    private void isValid() {
        try {
            sdao.verifyActiveProgram(getActiveInfo().getActiveTestId());
        } catch (Exception e) {
            __logger.error("Error validating test id: " + getActiveInfo().getActiveTestId());
        }
    }

    /** Does the current prescription (all lessons) require flash */
    public boolean dependsOnFlash(final Connection conn, int runId) throws Exception {
        String sql = "select count(*) " +
                     "from HA_TEST_RUN_LESSON l JOIN HA_TEST_RUN_LESSON_PID p  " +
                     "  on p.lid = l.id " +
                     "where l.run_id = ? " +
                     "and p.pid like '%.swf' ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ResultSet rs = ps.executeQuery();
            rs.first();
            return (rs.getInt(1) > 0); 
        }
        finally {
            SqlUtilities.releaseResources(null,  ps, null);
        }
    }

    /**
     * Return the active 'item' for this program.
     * 
     * Responsible for determining what 'program item', either a QUIZ,
     * PRESCRIPTION or END_OF_PROGRAM. Plus the specific data associated with
     * the program item type. For example, if it is a QUIZ, then the specific
     * data would be the quiz html, etc. If PRESCRIPTION, then data would be ALL
     * data required to show a prescription lesson.
     * 
     * @param conn
     * @return
     */
    public CmProgramFlowAction getActiveFlowAction(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();
    	boolean newQuiz = false;

    	try {
    		if (userProgram.isComplete()) {
    			if (student.getSettings().getStopAtProgramEnd() || userProgram.isCustom()) {
    				return new CmProgramFlowAction(CmPlace.END_OF_PROGRAM);
    			} else
    				return new CmProgramFlowAction(CmPlace.AUTO_ADVANCED_PROGRAM);
    		}

    		if (activeInfo.getActiveRunId() > 0) {
    			// is in a prescription
    			return new CmProgramFlowAction(new GetPrescriptionCommand().execute(conn, new GetPrescriptionAction(
    					activeInfo.getActiveRunId(), activeInfo.getActiveRunSession(), true)));
    		} else {
    			/**
    			 * If no quiz for current segment, then create a new one
    			 */
    			if (activeInfo.getActiveTestId() == 0) {
    				activeInfo.setActiveTestId(createNewProgramSegment().getTestId());
    				newQuiz = true;
    			}
    			return new CmProgramFlowAction(new GetQuizHtmlCommand().execute(conn,
    					new GetQuizHtmlAction(activeInfo.getActiveTestId())));
    		}
    	}
    	finally {
    		__logger.info(String.format("+++ getActiveFlowAction() runId: %d, testId: %d, newQuiz: %s, took: %d msec",
    				activeInfo.getActiveRunId(), activeInfo.getActiveTestId(), newQuiz, System.currentTimeMillis()-start));
    	}
    }

    /**
     * Move the program to the next Program Item
     * 
     * The logic is: if there is an active prescription if there are uncompleted
     * lessons move to the next lesson return PRESCRIPTION
     * 
     * else if user passed current QUIZ mark the segment as complete
     * 
     * 
     * if there are additional segments
     * 
     * move to next segment return QUIZ
     * 
     * 
     * if the AUTO_PROGRAM_ADVANCE is set for the user
     * 
     * advance to next program return first ProgramFlowItem
     * 
     * 
     * return END_OF_PROGRAM
     * 
     * @param conn
     * @throws Exception
     */
    public CmProgramFlowAction moveToNextFlowItem(final Connection conn) throws Exception {

        // verifyProgramFlowIsStillActive();

    	long start = System.currentTimeMillis();

        CmProgramFlowAction action = null;
        /**
         * if active testRun and more lessons
         * 
         */
        if (activeInfo.getActiveRunId() > 0) {

            /**
             * mark segment as completed?
             * 
             */

            /**
             * prescription is complete
             * 
             */
            if (areMoreSegments()) {
                moveToNextProgramSegment(conn);

                action = getActiveFlowAction(conn);
                assert (action.getPlace() == CmPlace.QUIZ);
            } else {
                /**
                 * End of program has been reached
                 * 
                 */
                markProgramAsCompleted(conn, true);

                if (student.getSettings().getStopAtProgramEnd()) {
                    action = getActiveFlowAction(conn);
                    assert (action.getPlace() == CmPlace.END_OF_PROGRAM);
                } else {
                    action = getActiveFlowAction(conn);
                    assert (action.getPlace() == CmPlace.AUTO_ADVANCED_PROGRAM);
                }
            }
        } else if (activeInfo.getActiveTestId() > 0) {

            /**
             * we should create the prescription
             * 
             * NOTE: this changes the active info
             * 
             * */
            CreateTestRunAction testRunAction = new CreateTestRunAction(activeInfo.getActiveTestId());
            CreateTestRunResponse testRunResponse = new CreateTestRunCommand().execute(conn, testRunAction);
            action = testRunResponse.getNextAction();
            if (testRunResponse.getSessionCount() > 0) {
                assert (action.getPlace() == CmPlace.PRESCRIPTION);
            } else {
                assert (action.getPlace() == CmPlace.PRESCRIPTION || action.getPlace() == CmPlace.QUIZ || action
                        .getPlace() == CmPlace.AUTO_ADVANCED_PROGRAM);
            }
        } else {
            /**
             * brand new program, create the first segment
             * 
             */
            createNewProgramSegment();
            action = getActiveFlowAction(conn);
            assert (action.getPlace() == CmPlace.PRESCRIPTION || action.getPlace() == CmPlace.QUIZ);
        }

		__logger.info(String.format("+++ moveToNextFlowItem() took: %d msec", System.currentTimeMillis()-start));
        assert (action != null);
        return action;
    }

    /**
     * Setup so the active item is to retake the current segment
     */
    public CmProgramFlowAction retakeActiveProgramSegment(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();

        activeInfo.setActiveRunId(0);
        activeInfo.setActiveTestId(0);
        sdao.setActiveInfo(conn, userProgram.getUserId(), activeInfo);

        CmProgramFlowAction flowAction = getActiveFlowAction(conn);
		__logger.info(String.format("+++ retakeActiveProgramSegment() took: %d msec", System.currentTimeMillis()-start));
        return flowAction;
    }

    /**
     * create a brand new test for the current segment
     * 
     * @param conn
     */
    private HaTest createNewProgramSegment() throws Exception {

    	long start = System.currentTimeMillis();

    	verifyProgramFlowIsStillActive();

        /**
         * create and register a new test
         */
        HaTestDef testDef = userProgram.getTestDef();
        HaTest test = HaTestDao.getInstance().createTest(userProgram.getUserId(), testDef, activeInfo.getActiveSegment());
		__logger.info(String.format("+++ createNewProgramSegment() took: %d msec", System.currentTimeMillis()-start));
		return test;
    }

    /**
     * Return true if the current test segment has been passed.
     * 
     * @param conn
     * @return
     * @throws Exception
     */
    public boolean hasPassedCurrentSegment(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();

    	try {
    		if (activeInfo.getActiveRunId() > 0) {
    			HaTestRun testRun = HaTestRunDao.getInstance().lookupTestRun(activeInfo.getActiveRunId());
    			return testRun.isPassing();
    		} else {
    			return false;
    		}
    	}
    	finally{
    		__logger.info(String.format("+++ hasPassedCurrentSegment() took: %d msec", System.currentTimeMillis()-start));
    	}
    }

    /**
     * Attempt to move this program to the next FlowItem.
     * 
     * If movement to next item advanced passed end of program, then mark
     * program as complete.
     * 
     * Return true if successfully advanced program.
     * 
     * 
     * @param conn
     * @return
     * @throws Exception
     */
    public void moveToNextProgramSegment(final Connection conn) throws CmProgramFlowException {

    	long start = System.currentTimeMillis();
        __logger.debug("Moving to next segment: " + activeInfo);
        try {
            if (!areMoreSegments()) {
                markProgramAsCompleted(conn, true);
            } else {
                activeInfo.setActiveSegment(activeInfo.getActiveSegment() + 1);
                activeInfo.setActiveTestId(0);
                activeInfo.setActiveRunSession(0);
                activeInfo.setActiveRunId(0);
                sdao.setActiveInfo(conn, userProgram.getUserId(), activeInfo);
            }
        } catch (Exception e) {
            throw new CmProgramFlowException("Error advancing program", e);
        }
        __logger.info(String.format("+++ moveToNextProgramSegment() took: %d msec", System.currentTimeMillis()-start));
    }

    public void markSessionAsActive(final Connection conn, int session) throws CmProgramFlowException {
    	long start = System.currentTimeMillis();
        try {
            if (session < 0 || session > getNumberOfSessionsInPrescription(conn, activeInfo.getActiveRunId())) {
                throw new CmProgramFlowException("session number not valid: " + session + ", " + activeInfo);
            }
            activeInfo.setActiveRunSession(session);
            sdao.setActiveInfo(conn, userProgram.getUserId(), activeInfo);

            /**
             * Mark this lesson as being viewed
             * 
             */
            HaTestRunDao.getInstance().setLessonViewed(conn, activeInfo.getActiveRunId(), session);

        } catch (Exception e) {
            throw new CmProgramFlowException("Error advancing program", e);
        }
        __logger.info(String.format("+++ markSessionAsActive() took: %d msec", System.currentTimeMillis()-start));
    }

    /**
     * Move to next segment only if available.
     * 
     * 
     * @param conn
     * @throws Exception
     */
    public void moveToNextSegmentIfAvailable(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();
        if (areMoreSegments()) {
            moveToNextProgramSegment(conn);
        } else {
            if (!userProgram.isComplete()) {
                markProgramAsCompleted(conn, true);
            }
        }
        __logger.info(String.format("+++ moveToNextSegmentIfAvailable() took: %d msec", System.currentTimeMillis()-start));
    }

    /*
     * Mark this program as being complete. All further flow movements will
     * throw an exception
     */
    public void markProgramAsCompleted(final Connection conn, boolean trueOrFalse) throws Exception {
    	long start = System.currentTimeMillis();
        if (updao.setProgramAsComplete(conn, userProgram.getId(), trueOrFalse)) {
            /** update our instance */
            userProgram.setCompleteDate((trueOrFalse==true)?new Date():null);
        }
        __logger.info(String.format("+++ markProgramAsCompleted() took: %d msec", System.currentTimeMillis()-start));
    }

    /**
     * Advance this session in current segment to the next session.
     * 
     * This also will mark that session (lesson) as being viewed.
     * 
     * @param conn
     * @return
     * @throws CmProgramFlowException
     */
    public boolean moveToNextSessionInSegment(final Connection conn) throws CmProgramFlowException {

    	long start = System.currentTimeMillis();

    	verifyProgramFlowIsStillActive();

        __logger.debug("Moving to next session in segment: " + activeInfo);
        try {
            if (areMoreSessionsInSegment(conn)) {
                int uid = userProgram.getUserId();
                activeInfo.setActiveRunSession(activeInfo.getActiveRunSession() + 1);
                sdao.setActiveInfo(conn, uid, activeInfo);

                return true;
            }
        } catch (Exception e) {
            throw new CmProgramFlowException("Could not advance to next lesson", e);
        }
        __logger.info(String.format("+++ moveToNextSessionInSegment() took: %d msec", System.currentTimeMillis()-start));

        return false;
    }

    private void verifyProgramFlowIsStillActive() throws CmProgramFlowException {
        if (userProgram.isComplete()) {
            throw new CmProgramFlowException("Program is already complete: " + userProgram);
        }
    }

    /**
     * Marks the current session/lesson in the flow as being viewed
     * 
     * @param conn
     * @param lessonNumber
     * @throws Exception
     */
    public void markCurrentSessionAsViewed(final Connection conn) throws Exception {
        /**
         * Mark this lesson as being viewed
         * 
         */
    	long start = System.currentTimeMillis();

        HaTestRunDao.getInstance().setLessonViewed(conn, activeInfo.getActiveRunId(), activeInfo.getActiveRunSession());

        __logger.info(String.format("+++ markCurrentSessionAsViewed() took: %d msec", System.currentTimeMillis()-start));
    }

    /**
     * Reset the current program back to start
     * 
     * @param conn
     * @throws Exception
     */
    public void reset(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();
        activeInfo.setActiveSegment(1);
        activeInfo.setActiveRunId(0);
        activeInfo.setActiveRunSession(0);
        activeInfo.setActiveSegmentSlot(0);
        activeInfo.setActiveTestId(0);

        saveActiveInfo(conn);
        markProgramAsCompleted(conn, false);
        __logger.info(String.format("+++ reset() took: %d msec", System.currentTimeMillis()-start));
    }

    /**
     * the current test segment defined for this program
     * 
     * @return
     */
    public int getActiveTestSegment() {
        return activeInfo.getActiveSegment();
    }

    /**
     * the total number of segments in this program.
     * 
     * @return
     */
    public int getTotalProgramSegments() {
        return userProgram.getConfig().getSegmentCount();
    }

    /**
     * return true if there are additional segments in this program.
     * 
     * @return
     */
    public boolean areMoreSegments() {
        return activeInfo.getActiveSegment() < userProgram.getConfig().getSegmentCount();
    }

    /**
     * Return true if there are additional sessions in the current active
     * test_run.
     * 
     * @param conn
     * @return
     * @throws Exception
     */
    public boolean areMoreSessionsInSegment(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();
        boolean more = activeInfo.getActiveRunSession() + 1 < getNumberOfSessionsInPrescription(conn,
                activeInfo.getActiveRunId());
        __logger.info(String.format("+++ areMoreSessionsInSegment() took: %d msec", System.currentTimeMillis()-start));
        return more;
    }

    /**
     * Return the total number of sessions in test run's prescription
     * 
     * @param conn
     * @param runId
     * @return
     * @throws Exception
     */
    public int getNumberOfSessionsInPrescription(final Connection conn, int runId) throws Exception {
    	long start = System.currentTimeMillis();
        int sessions = AssessmentPrescriptionManager.getInstance().getPrescription(conn, runId).getSessions().size();
        __logger.info(String.format("+++ getNumberOfSessionsInPrescription() took: %d msec", System.currentTimeMillis()-start));
        return sessions;
    }

    /**
     * return true if this program is complete
     * 
     * @param conn
     * @return
     * @throws Exception
     */
    public boolean isComplete(final Connection conn) throws Exception {
        return false;
    }

    public StudentUserProgramModel getUserProgram() {
        return userProgram;
    }

    public void setUserProgram(StudentUserProgramModel userProgram) {
        this.userProgram = userProgram;
    }

    public StudentActiveInfo getActiveInfo() {
        return activeInfo;
    }

    public void setActiveInfo(StudentActiveInfo activeInfo) {
        this.activeInfo = activeInfo;
    }

    public void saveActiveInfo(final Connection conn) throws Exception {
    	long start = System.currentTimeMillis();
        sdao.setActiveInfo(conn, userProgram.getUserId(), activeInfo);
        __logger.info(String.format("+++ saveActiveInfo() took: %d msec", System.currentTimeMillis()-start));
    }

    @Override
    public String toString() {
        return "CmProgramFlow [userProgram=" + userProgram + ", activeInfo=" + activeInfo + "]";
    }
}

/**
 * if is a custom program and the test segment/total is screwed up move to end
 * of program.
 * 
 * This is a patch to catch a corrupted program.
 */
// if(userProgram.getCustomProgramId() > 0 &&
// (cmProgram.getActiveTestSegment() >= cmProgram.getTotalTestSegments())) {
//
// /** this is a corrupted custom program
// *
// */
// nextAction = new NextAction(CmPlace.END_OF_PROGRAM);
// }
// else {
// nextAction = pres.getNextAction();
// }

