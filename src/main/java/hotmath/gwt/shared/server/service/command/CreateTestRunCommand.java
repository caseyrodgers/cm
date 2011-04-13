package hotmath.gwt.shared.server.service.command;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefPlacement;
import hotmath.testset.ha.HaTestRun;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Create the Test run for this test by reading the current question
 * selections.
 * 
 * RpcData fields: redirect_action,correct_percent, correct_answers,total_questions,run_id,assignedTest
 * 
 * @param testId
 * @param callBack
 * @return
 */
public class CreateTestRunCommand implements ActionHandler<CreateTestRunAction, CreateTestRunResponse> {

	private static Logger logger = Logger.getLogger(CreateTestRunCommand.class);

    @Override
    public CreateTestRunResponse execute(Connection conn, CreateTestRunAction action) throws Exception {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        long startTime;
        long beginTime;
        
        CreateTestRunResponse testRunInfo = new CreateTestRunResponse();

        CmStudentDao sdao = new CmStudentDao();
        startTime = beginTime = System.currentTimeMillis();
        sdao.verifyActiveProgram(conn, action.getTestId(), action.getUserId());

        CmProgramFlow cmProgram = new CmProgramFlow(conn,action.getUserId());

        logger.debug(String.format("+++ execute(): verifyActiveProgram(): took: %d msec",System.currentTimeMillis() - startTime));

        try {
            // get list of all correct answers
            List<String> incorrectPids = new ArrayList<String>();
            int totalAnswered = 0;
            int notAnswered = 0;
            int answeredCorrect = 0;
            int answeredIncorrect = 0;

            startTime = System.currentTimeMillis();
            HaTest test = HaTestDao.loadTest(conn, action.getTestId());
            logger.debug(String.format("+++ execute(): loadTest(): took: %d msec",System.currentTimeMillis() - startTime));
            
            startTime = System.currentTimeMillis();
            String sql = "select cs.pid, cs.is_correct, t.total_segments from v_HA_TEST_CURRENT_STATUS cs, HA_TEST t where cs.test_id = ? and t.test_id = cs.test_id";
            pstat = conn.prepareStatement(sql);

            
            /** get test run choices from DB table
             * 
             *  TODO: this is source of bug with users
             *        reporting incorrect (empty) quiz 
             *        results.  Maybe from multiple
             *        test run creations.
             *        
             *        might happen after current questions
             *        are read and then the quiz is shown 
             *        again .. which would try to read the 
             *        'current' selections, which would have
             *        been cleared after test run creation.
             *        (see HaTestRunDao.removeAllQuizResponses)
             */
            pstat.setInt(1, action.getTestId());
            rs = pstat.executeQuery();
            while (rs.next()) {
                String pid = rs.getString("pid");
                Integer corr = rs.getInt("is_correct");
                if (rs.wasNull())
                    corr = null;

                if (corr != null) {
                    if (corr == 1)
                        answeredCorrect++;
                    else
                        answeredIncorrect++;

                    totalAnswered++;
                } else
                    notAnswered++;

                if (corr == null || corr == 0) {
                    incorrectPids.add(pid);
                }
            }
           
            logger.debug(String.format("+++ execute(): getting current test results took: %d msec",System.currentTimeMillis() - startTime));
            startTime = System.currentTimeMillis();
            HaTestRun run = HaTestDao.createTestRun(conn, test.getUser().getUid(), test.getTestId(), answeredCorrect, answeredIncorrect, notAnswered);
          	logger.debug(String.format("+++ execute(): createTestRun(): took: %d msec",System.currentTimeMillis() - startTime));
            
          	
          	AssessmentPrescription pres=null;
          	if(cmProgram.getUserProgram().getCustomQuizId() > 0) {
                /** if a Custom Quiz, then no need to create a prescription
                 * 
                 */
          	    
          	    cmProgram.markProgramAsCompleted(conn, true);
          	}
          	else {
          	    startTime = System.currentTimeMillis();
          	    pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, run.getRunId());
          	    logger.debug(String.format("+++ execute(): getPrescription(): took: %d msec",System.currentTimeMillis() - startTime));
          	}
            
            testRunInfo.setRunId(run.getRunId());
            testRunInfo.setTestCorrectPercent(GetPrescriptionCommand.getTestPassPercent(run.getAnsweredCorrect() + run.getAnsweredIncorrect() + run.getNotAnswered(), run.getAnsweredCorrect()));
            testRunInfo.setSessionCount(pres != null?pres.getSessions().size():0);
            if(testRunInfo.getSessionCount() > 0)
                testRunInfo.setSessionName(pres.getSessions().get(0).getTopic());
            else {
                /** there is not a prescription assigned
                 * 
                 */
                testRunInfo.setSessionName("No Lesson");
            }

            
            /** 
             * if user DID NOT pass this quiz, or is a custom program
             * we increment the quiz slot to show
             * a new alternate quiz on next quiz creation.
             */
            if(!run.isPassing()) {
                startTime = System.currentTimeMillis();
                new CmStudentDao().moveToNextQuizSegmentSlot(conn,test.getUser().getUid(), test.getTestDef().getNumAlternateTests());
                	logger.debug(String.format("+++ execute(): moveToNextQuizSegmentSlot(): took: %d msec",System.currentTimeMillis() - startTime));
            }
            
            /** 
             * Let the prescription direct the next action depending on
             * type of test, status, etc.
             */
            startTime = System.currentTimeMillis();
           
            logger.debug(String.format("+++ execute: Prescription Class: %s, getNextAction(): took: %d msec",pres!=null?pres.getClass().getName():"No Prescription", System.currentTimeMillis() - startTime));
            
            testRunInfo.setCorrect(answeredCorrect);
            testRunInfo.setTotal(test.getTestQuestionCount());
            testRunInfo.setPassed(run.isPassing());
            
            /** Manage the program flow, if required
             * 
             */
            CmProgramFlowAction nextAction=null;
            if(pres == null || pres.getSessions().size() == 0) {
                if(run.getHaTest().getTestDef() instanceof HaTestDefPlacement && pres != null && pres.getNextAction().getPlace() == CmPlace.AUTO_PLACEMENT) {
                    /** special case for placement tests
                     * 
                     */
                    nextAction = pres.getNextAction();
                }
                else {
                    cmProgram.moveToNextSegmentIfAvailable(conn);
                }
            }
            else {
                cmProgram.getActiveInfo().setActiveRunId(run.getRunId());
                cmProgram.getActiveInfo().setActiveRunSession(0);
            }
            
            if(nextAction == null) {
               nextAction = cmProgram.getActiveFlowAction(conn);
            }
            testRunInfo.setNextAction(nextAction);
            if (nextAction.getPlace() != CmPlace.PRESCRIPTION) {
                /** 
                 * need to inform caller it needs to show the quiz ...
                 * Caught in QuizContent
                 */
                testRunInfo.setTestId(nextAction.getAssignedTestId());
                if (nextAction.getPlace() == CmPlace.AUTO_PLACEMENT) {
                    testRunInfo.setAssignedTest(nextAction.getAssignedTest());
                } else {
                    testRunInfo.setTestSegment(cmProgram.getActiveInfo().getActiveSegment());
                }
                return testRunInfo;
            }

            return testRunInfo;

        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException("Error creating new test run: " + e.getMessage());
        } finally {
          	logger.info(String.format("+++ execute(): CreateTestRun overall time: %d msec", System.currentTimeMillis() - beginTime));
            SqlUtilities.releaseResources(rs, pstat,null);
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateTestRunAction.class;
    }
}
