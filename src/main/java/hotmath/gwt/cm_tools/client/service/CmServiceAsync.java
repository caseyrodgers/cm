package hotmath.gwt.cm_tools.client.service;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CmServiceAsync {
    <T extends Response> void execute(Action<T> action, AsyncCallback<T> callback);
}
