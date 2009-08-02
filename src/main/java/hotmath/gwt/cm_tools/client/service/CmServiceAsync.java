package hotmath.gwt.cm_tools.client.service;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CmServiceAsync {
    <T extends Response> void execute(Action<T> action, AsyncCallback<T> callback);
}
