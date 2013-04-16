package hotmath.gwt.cm_mobile_assignments.client.util;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_mobile_assignments.client.user.CmMobileAssignmentUser;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentUserInfoAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Centralized handling of user meta data
 * 
 * @author casey
 *
 */
public class AssData {
    
    public interface CallbackWhenDataReady {
        void isReady();
    }
    private static AssData __assData; 
    
    /** Make sure all user data is ready for forms to use
     * 
     * Callback async once data is ready
     * @param callBack
     * @return
     */
    static public AssData readAssData(CallbackWhenDataReady callBack) {
        if(__assData == null) {
            __assData = new AssData(callBack);
        }
        return __assData;
    }
    
    int _uid;
    CallbackWhenDataReady callBack;
    static protected CmMobileAssignmentUser __userData;
    private AssData(CallbackWhenDataReady callBack) {
        this.callBack = callBack;
        try {
            _uid = Integer.parseInt(CmGwtUtils.getQueryParameter("uid"));
            if(_uid == 0) {
                AssAlertBox.showAlert("Uid must be specfied");
            }
            
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
        Log.info("Reading user data: " + uid);
        GetAssignmentUserInfoAction action = new GetAssignmentUserInfoAction(uid);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmMobileAssignmentUser>() {
            @Override
            public void onSuccess(CmMobileAssignmentUser result) {
                AssBusy.showBusy(true);
                Log.info("Login successful: " + result);
                __userData = result;
                
                callBack.isReady();
            }

            @Override
            public void onFailure(Throwable caught) {
                AssBusy.showBusy(false);
                AssAlertBox.showAlert("Could not log you in: " + caught.getMessage());
            }
        });        
    }

}
