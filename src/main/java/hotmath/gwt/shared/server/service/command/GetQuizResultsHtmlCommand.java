package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizResultsHtmlAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunResult;
import hotmath.util.HMConnectionPool;
import hotmath.util.Jsonizer;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.List;


/**
 * Return the QuizHtml with the results stored as a list of pids that are
 * correct.
 * 
 * rpcData: (all from QuizHtml) and
 * quiz_result_json,quiz_correct_count,quiz_question_count
 */
public class GetQuizResultsHtmlCommand implements ActionHandler<GetQuizResultsHtmlAction, RpcData> {

    @Override
    public RpcData execute(final Connection conn, GetQuizResultsHtmlAction action) throws CmRpcException {
        try {
            HaTestRun testRun = HaTestRun.lookupTestRun(conn, action.getRunId());
            
            List<HaTestRunResult> results = testRun.getTestRunResults();
            String resultJson = "";
            for (HaTestRunResult r : results) {
                if (resultJson.length() > 0)
                    resultJson += ",";
                resultJson += Jsonizer.toJson(r);
            }
            resultJson = "[" + resultJson + "]";

            GetQuizHtmlCheckedAction quizResultsAction = new GetQuizHtmlCheckedAction(testRun.getHaTest().getTestId());
            RpcData quizRpc = ActionDispatcher.getInstance().execute(quizResultsAction);

            quizRpc.putData("quiz_result_json", resultJson);
            quizRpc.putData("quiz_question_count", testRun.getHaTest().getTestQuestionCount());
            quizRpc.putData("quiz_correct_count", testRun.getAnsweredCorrect());

            return quizRpc;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }

    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetQuizResultsHtmlAction.class;
    }

}
