package hotmath.gwt.cm_rpc_core.client.rpc;


import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CmServiceAsync {
    <T extends Response> void execute(Action<T> action, AsyncCallback<T> callback);
}
