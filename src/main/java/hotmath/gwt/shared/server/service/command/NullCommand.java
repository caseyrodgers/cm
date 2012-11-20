package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.NullAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc.server.service.ActionHandlerManualConnectionManagement;

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
