package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.DeleteSolutionAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;

public class DeleteSolutionCommand implements ActionHandler<DeleteSolutionAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, DeleteSolutionAction action) throws Exception {
        SolutionDao.getInstance().deleteSolution(action.getPid());
        return new RpcData("status=OK");
    }


    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return DeleteSolutionAction.class;
    }

}
