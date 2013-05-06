package hotmath.gwt.cm_mobile_shared.client.background;

import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckHandler;
import hotmath.gwt.cm_core.client.model.CatchupMathVersion;
import hotmath.gwt.cm_core.client.model.UserSyncInfo;
import hotmath.gwt.cm_core.client.rpc.GetUserSyncAction;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Display a message from central server
 * 
 * @author casey
 *
 */
public class BackgroundServerChecker {

    static final int CHECK_EVERY = 1000 * 60 * 15;
    private int uid;
    
    static private BackgroundServerChecker __instance;
    static public BackgroundServerChecker getInstance(int uid) {
        if(__instance == null) {
            __instance = new BackgroundServerChecker();
        }
        __instance.setUid(uid);
        
        return __instance;
    }

    private BackgroundServerChecker() {
        monitorServer();
    }

    
    private void setUid(int uid) {
        this.uid = uid;
    }


    
    Timer _timer;
    private void monitorServer() {
        _timer = new Timer() {
            @Override
            public void run() {
                checkForUpdate();
            }
        };
        _timer.scheduleRepeating(CHECK_EVERY);
        
        /** do it first */
        checkForUpdate();
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    public void checkForUpdate() {
        Log.debug("Checking for server changes...");
        /** handle as separate request to keep errors 
         * silent in case of temp offline
         * 
         */
         GetUserSyncAction action = new GetUserSyncAction(uid);
         CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<UserSyncInfo>() {
             @Override
            public void onSuccess(UserSyncInfo info) {
                 Log.debug("Check complete");
                 CatchupMathVersion version = info.getVersionInfo();
                 
                 if(info.getAssignmentInfo().isAdminUsingAssignments()) {
                     fireAppropriateEvent(info.getAssignmentInfo());
                 }
            }
             @Override
            public void onFailure(Throwable arg0) {
                 /** fail silent */
                 Log.error(arg0.getMessage(), arg0);
            }
        });
    }
    

    /** Fire update event if things are actually different
     * 
     * @param assignmentInfo
     */
    static AssignmentUserInfo _lastCheckedData;
    protected static void fireAppropriateEvent(AssignmentUserInfo assignmentInfo) {
        if(_lastCheckedData == null || !_lastCheckedData.equals(assignmentInfo)) {
            _lastCheckedData = assignmentInfo;
            
            Log.debug("Firing AssignmentsUpdatedEvent: " + assignmentInfo);
            CmRpcCore.EVENT_BUS.fireEvent(new AssignmentsUpdatedEvent(assignmentInfo));
            CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
        }
    }


    static {
        CmRpcCore.EVENT_BUS.addHandler(ForceSystemSyncCheckEvent.TYPE, new ForceSystemSyncCheckHandler() {
            public void forceSyncCheck() {
                __instance.checkForUpdate();
            }
        });
    }


    public static void stopInstance() {
        if(__instance != null) {
            if(__instance._timer != null) {
                __instance._timer.cancel();
            }
            __instance._timer = null;
        }
    }
}
