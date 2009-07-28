package hotmath.gwt.shared.server.service;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import java.sql.Connection;

public interface ActionHandler<A extends Action<R>, R extends Response> {

    public Class<? extends Action<? extends Response>> getActionType();

    public R execute(Connection conn, A action) throws Exception;

}