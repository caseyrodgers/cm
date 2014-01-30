package hotmath.gwt.cm_mobile_shared.client.background;

/** Info: 
 * http://stackoverflow.com/questions/6543325/what-happens-to-javascript-execution-settimeout-etc-when-iphone-android-goes
 */
import hotmath.gwt.cm_core.client.event.CmLogoutEvent;
import hotmath.gwt.cm_core.client.event.CmLogoutHandler;
import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckHandler;
import hotmath.gwt.cm_core.client.model.CatchupMathVersion;
import hotmath.gwt.cm_core.client.model.UserSyncInfo;
import hotmath.gwt.cm_core.client.rpc.GetUserSyncAction;
import hotmath.gwt.cm_core.client.util.CmIdleTimeWatcher;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.UpdateAssignmentViewEvent;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Display a message from central server
 * 
 * Serves same purpose on mobile as SystemSyncChecker does on desktop.
 * 
 * 
 * @author casey
 *
 */
public class BackgroundServerChecker {

    static final int CHECK_EVERY = 1000 * 60 * 5;
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
        _lastCheckedData = SharedData.getMobileUser().getAssignmentInfo();
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
                checkForUpdate(true, null);
            }
        };
        _timer.scheduleRepeating(CHECK_EVERY);
    }
    
    
    /** Call server and look for new messages.
     * @param callback 
     * 
     */
    public void checkForUpdate(final boolean doFullCheck, final CallbackOnComplete callback) {
        
        //Window.alert("checkForUpdate called!");
        if(this.uid == 0) {
            return;
        }

        /** handle as separate request to keep errors 
         * silent in case of temp offline
         * 
         */
         GetUserSyncAction action = new GetUserSyncAction(uid);
         int minutes = CmIdleTimeWatcher.getInstance().getActiveMinutes(true);
         action.setUserActiveMinutes(minutes);
         
         Log.debug("BackgroundServerChecker", "UserSyncAction: " + action.toString());
         
         CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<UserSyncInfo>() {
             @Override
            public void onSuccess(UserSyncInfo info) {
                 try {
                     Log.debug("BackgroundServerChecker", "Check complete");
                     
                     if(!doFullCheck) {
                         return;
                     }
                     
                     
                     CatchupMathVersion version = info.getVersionInfo();
                     
                     if(info.getAssignmentInfo().isAdminUsingAssignments()) {
                         fireAppropriateEvent(info.getAssignmentInfo());
                     }
                 }
                 finally {
                     if(callback != null) {
                         callback.isComplete();
                     }
                 }
            }
             @Override
            public void onFailure(Throwable arg0) {
                 /** fail silent */
                 Log.error(arg0.getMessage(), arg0);
            }
        });
    }
    
    public static AssignmentUserInfo getLastAssignmentInfo() {
        return _lastCheckedData;
    }
    

    /** Fire update event if things are actually different
     * 
     * @param assignmentInfo
     */
    static AssignmentUserInfo _lastCheckedData;
    protected static void fireAppropriateEvent(AssignmentUserInfo assignmentInfo) {
        if(_lastCheckedData != null  &&(!_lastCheckedData.equals(assignmentInfo) || assignmentInfo.getUnreadMessageCount() > 0)) { //  !_lastCheckedData.equals(assignmentInfo)) {
            Log.debug("Firing AssignmentsUpdatedEvent: " + assignmentInfo);
            CmRpcCore.EVENT_BUS.fireEvent(new AssignmentsUpdatedEvent(assignmentInfo));
            CmRpcCore.EVENT_BUS.fireEvent(new UpdateAssignmentViewEvent());
        }
        else {
            _lastCheckedData = assignmentInfo;
        }
    }


    static {
        CmRpcCore.EVENT_BUS.addHandler(ForceSystemSyncCheckEvent.TYPE, new ForceSystemSyncCheckHandler() {
            public void forceSyncCheck(boolean doFullCheck, CallbackOnComplete callback) {
                __instance.checkForUpdate(doFullCheck, callback);
            }
        });
        
        CmRpcCore.EVENT_BUS.addHandler(CmLogoutEvent.TYPE, new CmLogoutHandler() {
            @Override
            public void userLogOut() {
                __instance.checkForUpdate(false, null);
            }
        });
        
        setupSleepChecker();
    }
    
    static private void gwt_jsWentToSleep(int secs) {
        Log.debug("JS WENT TO SLEEP: " + secs);
        if(__instance != null) {
            if(__instance._timer!=null) {
                Log.debug("Canceling timer event");
                stopInstance();
            }
            
            /** force any current changes to be flushed
             * 
             */
            __instance.checkForUpdate(false, null);
        }
    }
    
    static native private void setupSleepChecker() /*-{
        $wnd.sleepCheck = function() {
            $wnd.console.log('JS sleep check');
            var now = new Date().getTime();
            var diff = now - window.lastCheck;
            if (diff > 10000) {
                $wnd.console.log('JS Went To Sleep!: " + took ' + diff + 'ms');
                @hotmath.gwt.cm_mobile_shared.client.background.BackgroundServerChecker::gwt_jsWentToSleep(I)(diff);
            }
            window.lastCheck = now;
        }
        window.lastCheck = new Date().getTime();
        setInterval($wnd.sleepCheck, 1000);
    }-*/;


    public static void stopInstance() {
        if(__instance != null) {
            if(__instance._timer != null) {
                __instance._timer.cancel();
            }
            __instance._timer = null;
        }
    }
}
