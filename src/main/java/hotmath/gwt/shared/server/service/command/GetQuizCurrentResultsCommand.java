package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRunResult;
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
            List<HaTestRunResult> testResults = HaTestDao.getTestCurrentResponses(conn, action.getTestId());

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
