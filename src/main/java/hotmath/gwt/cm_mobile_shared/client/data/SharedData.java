package hotmath.gwt.cm_mobile_shared.client.data;


import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.background.BackgroundServerChecker;
import hotmath.gwt.cm_mobile_shared.client.event.UserLoginEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutHandler;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** centralized wrapper for shared global data structures
 * 
 * Contains composite objects for both Quiz and Prescription data.
 * 
 * @TODO: refactor these info a better data structure.  This is really 
 * a local datasource. Would be nice to be able to serialize this data
 * at some point into local storage as well.
 *
 *  
 * @author casey
 *
 */
public class SharedData {
    
    private static UserInfo userInfo;
    private static CmProgramFlowAction flowAction;
    private static CmMobileUser __mobileUser;
    
    static public CmProgramFlowAction getFlowAction() {
        if(flowAction == null) {
            flowAction = new CmProgramFlowAction();
        }
        return flowAction;
    }
    
    static public void setFlowAction(CmProgramFlowAction flowActionIn) {
        flowAction = flowActionIn;
    }
    
    static public UserInfo getUserInfo() {
        return userInfo;
    }
    
    static public void setUserInfo(UserInfo userInfoIn) {
        userInfo = userInfoIn;
    }

    static public int getCountLessonsRemaining() {
        int cnt=SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics().size();
        for(SessionTopic topic: SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics()) {
            if(topic.isComplete()) {
                cnt--;
            }
        }
        return cnt;
    }
    
    /** Search through prescription data and return the InmhItemData that is 
     * in the ordinal position of the named resource type.
     * 
     * @param type
     * @param ordinal
     */
    public static InmhItemData findInmhDataInPrescriptionByOrdinal(String type, int ordinal) throws Exception  {
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if(type.equals(dr.getType())) {
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    if(i == ordinal) {
                        return dr.getItems().get(i);
                    }
                }
            }
        }
        throw new Exception("No " + type + " resource at ordinal position '" + ordinal + "'");
    }
    
    
    public static String calculateCurrentLessonStatus(String topic) {
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if("practice".equals(dr.getType())) {
                int viewed=0;
                int total=0;
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    InmhItemData item = dr.getItems().get(i);
                    if(item.isViewed()) {
                        viewed++;
                    }
                    total++;
                }
                
                return viewed + " of " + total;
            }
        }
        return "unknown status '" + topic + "'";
    }
    
    
    /** Return the InmhItemData for the named resource file or null
     * 
     * @param type
     * @param file
     * @return
     * @throws Exception
     */
    public static InmhItemData findInmhDataInPrescriptionByFile(String type, String file) throws Exception {
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if(type.equals(dr.getType())) {
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    if(dr.getItems().get(i).getFile().equals(file)) {
                        return dr.getItems().get(i);
                    }
                }
            }
        }
        return null;
    }
    
    public static int findOrdinalPositionOfResource(InmhItemData itemIn) {
        String type = itemIn.getType();
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if(type.equals(dr.getType())) {
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    InmhItemData item = dr.getItems().get(i);
                    if(item.getTitle().equals(itemIn.getTitle())) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    
    static public CmMobileUser getMobileUser() {
        if(__mobileUser == null) {
            Log.debug("CmMobileUser is not set");
        }
        return __mobileUser;
    }
    
    public static void setData(CmMobileUser result) {
        if(result == null) {
            SharedData.setMobileUser(null);
            SharedData.setUserInfo(null);
            SharedData.setFlowAction(null);
            
            
            BackgroundServerChecker.stopInstance();
        }
        else {
            SharedData.setMobileUser(result);
            SharedData.setUserInfo(result.getBaseLoginResponse().getUserInfo());
            SharedData.setFlowAction(result.getFlowAction());
            
            saveUidToLocalStorage(result.getUserId());
            
            BackgroundServerChecker.getInstance(result.getUserId());
            
            CmRpcCore.EVENT_BUS.fireEvent(new UserLoginEvent(result));
        }
        
        
        CmRpcCore.EVENT_BUS.fireEvent(new AssignmentsUpdatedEvent(result.getAssignmentInfo()));
    }

    private static void setMobileUser(CmMobileUser result) {
        __mobileUser = result;
    }
    
    static public void makeSureUserHasBeenRead(final CallbackOnComplete callback) {
        if(__mobileUser == null) {
            final int uid = getUidFromLocalStorage();
            if(uid != 0) {
                GetCmMobileLoginAction action = new GetCmMobileLoginAction(uid);
                CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<CmMobileUser>() {
                    @Override
                    public void onSuccess(CmMobileUser result) {
                        SharedData.setData(result);
                        callback.isComplete();
                    }
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("Error logging in with saved uid: " + uid, caught);
                        MessageBox.showError("Could not login with saved uid: " + caught.getMessage());
                    }
                });
            }
            else {
                Window.alert("no user id saved");
            }
        }
        else {
            callback.isComplete();
        }
    }

    public static void saveUidToLocalStorage(int uid) {
        Storage store = Storage.getLocalStorage();
        if(store == null) {
            MessageBox.showError("Local Storage not supported");
        }
        
        try {
            store.setItem("uid", Integer.toString(uid));
        }
        catch(Exception e) {
            Log.error("Could not save uid to local storage", e);
        }
    }
    
    public static int getUidFromLocalStorage() {
        Storage store = Storage.getLocalStorage();
        if(store == null) {
            MessageBox.showError("Local Storage not supported");
            return 0;
        }
        
        int uid=0;
        try {
            String sUid=store.getItem("uid");
            if(sUid != null) {
                uid = Integer.parseInt(sUid);
            }
        }
        catch(Exception e) {
            Log.error("Could not get uid from local storage", e);
        }
        return uid;
    }
    
    static {
        CmRpcCore.EVENT_BUS.addHandler(UserLogoutEvent.TYPE,  new UserLogoutHandler() {
            @Override
            public void userLogout() {
                SharedData.setData(null);                
            }
        });
    }
}
