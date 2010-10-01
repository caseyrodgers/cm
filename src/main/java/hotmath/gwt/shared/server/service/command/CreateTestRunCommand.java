package hotmath.gwt.shared.server.service.command;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.NextAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.NextAction.NextActionName;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.util.sql.SqlUtilities;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



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
        
        startTime = System.currentTimeMillis();
        new CmStudentDao().verifyActiveProgram(conn, action.getTestId(), action.getUserId());

        if (logger.isInfoEnabled()) {
        	logger.info(String.format("+++ execute(): verifyActiveProgram(): took: %d msec",
        		System.currentTimeMillis() - startTime));
        }

        try {
            // get list of all correct answers
            List<String> incorrectPids = new ArrayList<String>();
            int totalAnswered = 0;
            int notAnswered = 0;
            int answeredCorrect = 0;
            int answeredIncorrect = 0;

            startTime = System.currentTimeMillis();
            HaTest test = HaTestDao.loadTest(conn, action.getTestId());
            if (logger.isInfoEnabled()) {
            	logger.info(String.format("+++ execute(): loadTest(): took: %d msec",
            		System.currentTimeMillis() - startTime));
            }
            
            startTime = System.currentTimeMillis();
            String sql = "select cs.pid, cs.is_correct, t.total_segments from v_HA_TEST_CURRENT_STATUS cs, HA_TEST t where cs.test_id = ? and t.test_id = cs.test_id";
            pstat = conn.prepareStatement(sql);

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
            if (logger.isInfoEnabled()) {
            	logger.info(String.format("+++ execute(): getting current test results took: %d msec",
            		System.currentTimeMillis() - startTime));
            }

            startTime = System.currentTimeMillis();
            HaTestRun run = HaTestDao.createTestRun(conn, test.getUser().getUid(), test.getTestId(), answeredCorrect, answeredIncorrect, notAnswered);
            if (logger.isInfoEnabled()) {
            	logger.info(String.format("+++ execute(): createTestRun(): took: %d msec",
            		System.currentTimeMillis() - startTime));
            }
            
            /** 
             * if user DID NOT pass this quiz, 
             * we increment the quiz slot to show
             * a new quiz on next quiz creation.
             */
            if(!run.isPassing()) {
                startTime = System.currentTimeMillis();
                new CmStudentDao().moveToNextQuizSegmentSlot(conn,test.getUser().getUid(), test.getTestDef().getNumAlternateTests());
                if (logger.isInfoEnabled()) {
                	logger.info(String.format("+++ execute(): moveToNextQuizSegmentSlot(): took: %d msec",
                		System.currentTimeMillis() - startTime));
                }
            }

            startTime = System.currentTimeMillis();
            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, run.getRunId());
            if (logger.isInfoEnabled()) {
            	logger.info(String.format("+++ execute(): getPrescription(): took: %d msec",
            		System.currentTimeMillis() - startTime));
            }

            CreateTestRunResponse testRunInfo = new CreateTestRunResponse();
            
            /** 
             * Let the prescription direct the next action depending on
             * type of test, status, etc.
             */
            NextAction nextAction = pres.getNextAction();
            
            testRunInfo.setCorrect(answeredCorrect);
            testRunInfo.setTotal(pres.getTest().getTestQuestionCount());
            testRunInfo.setPassed(run.isPassing());

            testRunInfo.setAction(nextAction.getNextAction());
            if (nextAction.getNextAction() != NextActionName.PRESCRIPTION) {
                /** 
                 * need to inform caller it needs to show the quiz ...
                 * Caught in QuizContent
                 */
                testRunInfo.setTestId(nextAction.getAssignedTestId());
                if (nextAction.getNextAction().equals(NextActionName.AUTO_ASSSIGNED)) {
                    testRunInfo.setAssignedTest(nextAction.getAssignedTest());
                } else {
                    testRunInfo.setTestSegment(test.getUser().getActiveTestSegment());
                }
                
                return testRunInfo;
            }

            testRunInfo.setRunId(run.getRunId());
            testRunInfo.setTestCorrectPercent(GetPrescriptionCommand.getTestPassPercent(run.getAnsweredCorrect() + run.getAnsweredIncorrect() + run.getNotAnswered(), run.getAnsweredCorrect()));
            
            
            testRunInfo.setSessionCount(pres.getSessions().size());
            if(testRunInfo.getSessionCount() > 0)
                testRunInfo.setSessionName(pres.getSessions().get(0).getTopic());
            else
                testRunInfo.setSessionName("Unknown");
            
            return testRunInfo;

        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException("Error creating new test run: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat,null);
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateTestRunAction.class;
    }
}
