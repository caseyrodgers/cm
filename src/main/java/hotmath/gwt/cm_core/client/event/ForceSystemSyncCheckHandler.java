package hotmath.gwt.cm_core.client.event;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.event.shared.EventHandler;

public interface ForceSystemSyncCheckHandler extends EventHandler {
    void forceSyncCheck(boolean doFullCheck, CallbackOnComplete callback);
}
