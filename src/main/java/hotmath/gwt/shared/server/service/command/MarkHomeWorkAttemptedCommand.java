package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.MarkHomeWorkAttemptedAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class MarkHomeWorkAttemptedCommand implements ActionHandler<MarkHomeWorkAttemptedAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return MarkHomeWorkAttemptedAction.class;
     }

    @Override
    public RpcData execute(Connection conn, MarkHomeWorkAttemptedAction action) throws Exception {
        return null;
    }

}
