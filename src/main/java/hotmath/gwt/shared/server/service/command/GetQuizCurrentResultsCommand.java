package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRunResult;
import hotmath.testset.ha.HaUser;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;


/** Return the current test results for a user
 *  
 * @author casey
 *
 */
public class GetQuizCurrentResultsCommand implements ActionHandler<GetQuizCurrentResultsAction,CmList<RpcData>>{

    @Override
    public CmList<RpcData> execute(final Connection conn, GetQuizCurrentResultsAction action) throws Exception {
        try {
            CmArrayList<RpcData> rpcData = new CmArrayList<RpcData>();
            int testId = HaUser.lookUser(conn, action.getUid(), null).getActiveTest();
            if (testId == 0)
                return rpcData;

            List<HaTestRunResult> testResults = HaTestDao.getTestCurrentResponses(conn, testId);

            for (HaTestRunResult tr : testResults) {
                if (tr.isAnswered()) {
                    RpcData rd = new RpcData(Arrays.asList("pid=" + tr.getPid(), "answer=" + tr.getResponseIndex()));
                    rpcData.add(rd);
                }
            }
            return rpcData;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetQuizCurrentResultsAction.class;
    }
}
