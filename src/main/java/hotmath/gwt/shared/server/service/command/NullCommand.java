package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.NullAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;

import java.sql.Connection;

public class NullCommand implements ActionHandler<NullAction, RpcData>, ActionHandlerManualConnectionManagement {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return NullAction.class;
    }

    @Override
    public RpcData execute(Connection conn, NullAction action) throws Exception {
        return new RpcData("status=OK");
    }


}
