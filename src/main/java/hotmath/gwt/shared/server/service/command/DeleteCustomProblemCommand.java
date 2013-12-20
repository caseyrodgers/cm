package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.DeleteCustomProblemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class DeleteCustomProblemCommand implements ActionHandler<DeleteCustomProblemAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, DeleteCustomProblemAction action) throws Exception {
        CustomProblemDao.getInstance().deleteCustomProblem(action.getProblem());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return DeleteCustomProblemAction.class;
    }
}
