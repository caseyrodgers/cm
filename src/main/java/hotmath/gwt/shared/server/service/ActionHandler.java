package hotmath.gwt.shared.server.service;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;

public interface ActionHandler<A extends Action<R>, R extends Response> {

    public Class<? extends Action<? extends Response>> getActionType();

    public R execute(A action) throws Exception;

}