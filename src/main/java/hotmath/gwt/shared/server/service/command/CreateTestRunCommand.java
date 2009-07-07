package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm_tools.client.ui.NextAction;
import hotmath.gwt.cm_tools.client.ui.NextAction.NextActionName;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestRun;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;



/**
 * Create the Test run for this test by reading the current question
 * selections
 * 
 * @param testId
 * @param callBack
 * @return
 */
public class CreateTestRunCommand implements ActionHandler<CreateTestRunAction, RpcData> {

    @Override
    public RpcData execute(CreateTestRunAction action) throws Exception {

        Connection conn = null;
        PreparedStatement pstat = null;
        try {

            // get list of all correct answers
            List<String> incorrectPids = new ArrayList<String>();
            int totalAnswered = 0;
            int notAnswered = 0;
            int answeredCorrect = 0;
            int answeredIncorrect = 0;
            int totalSessions = 0;

            String sql = "select cs.pid, cs.is_correct, t.total_segments from v_HA_TEST_CURRENT_STATUS cs, HA_TEST t where cs.test_id = ? and t.test_id = cs.test_id";
            conn = HMConnectionPool.getConnection();
            
            HaTest test = HaTest.loadTest(conn, action.getTestId());
            
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, action.getTestId());
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                if (totalSessions < 1) {
                    totalSessions = rs.getInt("total_segments");
                }
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

            HaTestRun run = test.createTestRun(conn, incorrectPids.toArray(new String[incorrectPids.size()]),
                    answeredCorrect, answeredIncorrect, notAnswered, totalSessions);

            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, run.getRunId());

            // Let the prescription instruct the next action depending on
            // type of test, status, etc.
            NextAction nextAction = pres.getNextAction();
            RpcData rdata = new RpcData();
            rdata.putData("correct_answers", answeredCorrect);
            rdata.putData("total_questions", (notAnswered + totalAnswered));

            if (!nextAction.getNextAction().equals(NextActionName.PRESCRIPTION)) {
                // need to inform caller it needs to show the quiz ...
                // Caught in QuizContent

                if (nextAction.getNextAction().equals(NextActionName.AUTO_ASSSIGNED)) {
                    rdata.putData("redirect_action", "AUTO_ASSIGNED");
                    rdata.putData("assigned_test", nextAction.getAssignedTest());
                } else {
                    rdata.putData("redirect_action", "QUIZ");
                    rdata.putData("segment", test.getUser().getActiveTestSegment());
                }

                return rdata;
            }

            rdata.putData("run_id", run.getRunId());
            rdata.putData("correct_percent", GetPrescriptionCommand.getTestPassPercent(run.getHaTest().getNumTestQuestions(), run
                    .getAnsweredCorrect()));
            return rdata;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error creating new test run: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }
    
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return CreateTestRunAction.class;
    }

}
