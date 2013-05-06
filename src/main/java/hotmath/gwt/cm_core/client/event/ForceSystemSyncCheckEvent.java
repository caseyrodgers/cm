package hotmath.gwt.cm_core.client.event;



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
    
    @Override
    public Type<ForceSystemSyncCheckHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ForceSystemSyncCheckHandler handler) {
        handler.forceSyncCheck();
    }
}
