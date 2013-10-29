package hotmath.gwt.cm_rpc_core.client.rpc;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CmServiceAsync {
    <T extends Response> void execute(Action<T> action, AsyncCallback<T> callback);
}
