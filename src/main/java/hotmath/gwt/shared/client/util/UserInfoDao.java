package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

public class UserInfoDao {
    
    /** Lookup this user and callback when complete.
     * 
     *  The UserInfo has been set before callback.
     * 
     * @param uid
     * @param callback
     * @return
     */
    static public void loadUser(final int uid, final CmAsyncRequest callback) {
        new RetryAction<UserLoginResponse>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetUserInfoAction action = new GetUserInfoAction(uid,"");
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(UserLoginResponse loginResponse) {

                UserInfo user = loginResponse.getUserInfo();
                UserInfo.setInstance(user);
                
                // if run_id passed in, then allow user to view_only
                if(CmCore.getQueryParameter("run_id") != null) {
                    int runId = Integer.parseInt(CmCore.getQueryParameter("run_id"));
                    // setup user to masquerade as real user
                    user.setRunId(runId);
                    user.setActiveUser(false);
                }
                else {
                    user.setActiveUser(true);
                }
                
                CmLogger.debug("UserInfo object set to: " + user);
                
                CmBusyManager.setBusy(false);     
                callback.requestComplete(null);

                // fire an event on the event bus, passing new userinfo
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED,user));
            }
        }.register();
    }    



    /** Create a UserInfo object from a JSON string.
     * 
     * Throw exception on any parsing type of error.  We
     * must not continue on any error due to the need for 
     * a valid UserInfo object to have been set.
     * 
     * 
     * @param json
     * @throws Exception
     */
    static public CmDestination loadUserAndReturnFirstAction(String json) throws Exception {
    	try {
    	    UserInfo ui = CmGwtUtils.extractUserFromJsonString(json);
            UserInfo.setInstance(ui);
             
             // fire an event on the event bus, passing new userinfo
             EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED,ui));
             return ui.getFirstDestination();
    	}
    	catch(Exception e) {
    		throw new Exception("Could not create UserInfo from JSON", e);
    	}
    }


}
