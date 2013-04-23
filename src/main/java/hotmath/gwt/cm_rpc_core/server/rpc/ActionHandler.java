package hotmath.gwt.cm_rpc_core.server.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.sql.Connection;

public interface ActionHandler<A extends Action<R>, R extends Response> {

    public Class<? extends Action<? extends Response>> getActionType();

    public R execute(Connection conn, A action) throws Exception;
}