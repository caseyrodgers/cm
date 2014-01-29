package hotmath.gwt.cm_core.client.event;



import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.event.shared.GwtEvent;

/** Forces a call to SystemSyncChecker.checkForUpdate
 *  
 *  This will causes a server action to happen and sync the user
 *  with the known state of the database.
 *  
 * @author casey
 *
 */
public class ForceSystemSyncCheckEvent extends GwtEvent<ForceSystemSyncCheckHandler> {

    public static Type<ForceSystemSyncCheckHandler> TYPE = new Type<ForceSystemSyncCheckHandler>();
    private CallbackOnComplete callback;
    private boolean doFullCheck;
    
    public ForceSystemSyncCheckEvent(boolean doFullCheck, CallbackOnComplete callbackOnComplete) {
        this.doFullCheck = doFullCheck;
        this.callback = callbackOnComplete;
    }

    public CallbackOnComplete getCallback() {
        return callback;
    }

    public void setCallback(CallbackOnComplete callback) {
        this.callback = callback;
    }

    @Override
    public Type<ForceSystemSyncCheckHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ForceSystemSyncCheckHandler handler) {
        handler.forceSyncCheck(doFullCheck, callback);
    }

    public boolean isDoFullCheck() {
        return doFullCheck;
    }

    public void setDoFullCheck(boolean doFullCheck) {
        this.doFullCheck = doFullCheck;
    }
}
