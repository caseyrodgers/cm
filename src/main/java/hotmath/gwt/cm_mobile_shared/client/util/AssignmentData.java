package hotmath.gwt.cm_mobile_shared.client.util;


import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.CmMobileAssignmentUser;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentUserInfoAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Centralized handling of user meta data
 * 
 * @author casey
 *
 */
public class AssignmentData {
    
    public interface CallbackWhenDataReady {
        void isReady();
    }
    
    /** Make sure all user data is ready for forms to use
     * Read from server is required
     * 
     * Callback async once data is ready
     * @param callBack
     * @return
     */
    static private AssignmentData __assData;
    static public AssignmentData readAssData(CallbackWhenDataReady callBack) {
        if(__assData == null) {
            __assData = new AssignmentData(callBack);
        }
        else {
            callBack.isReady();
        }
        return __assData;
    }
    
    int _uid;
    CallbackWhenDataReady callBack;
    static protected CmMobileAssignmentUser __userData;
    static protected StudentAssignment __currentAssignment;
    
    public static void clear() {
        __assData = null;
        __userData = null;
    }
    
    private AssignmentData(CallbackWhenDataReady callBack) {
        this.callBack = callBack;
        try {
            _uid = SharedData.getMobileUser().getUserId();   // Integer.parseInt(CmGwtUtils.getQueryParameter("uid"));
            Log.info("uid: " + _uid);
            readUserAssignmentData(_uid);
        }
        catch(Exception e) {
            Log.error("Error getting uid", e);
        }
    }
    
    static public CmMobileAssignmentUser getUserData() {
        return __userData;
    }
    
    private void readUserAssignmentData(int uid) {
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        Log.info("Reading user data: " + uid);
        GetAssignmentUserInfoAction action = new GetAssignmentUserInfoAction(uid);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmMobileAssignmentUser>() {
            @Override
            public void onSuccess(CmMobileAssignmentUser result) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));

                Log.info("Login successful: " + result);
                __userData = result;
                
                callBack.isReady();
            }

            @Override
            public void onFailure(Throwable caught) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Window.alert("Could not log you in: " + caught.getMessage());
            }
        });        
    }

    public static void refreshAssData(CallbackWhenDataReady callbackWhenDataReady) {
        __assData = null;
        readAssData(callbackWhenDataReady);
    }
}
