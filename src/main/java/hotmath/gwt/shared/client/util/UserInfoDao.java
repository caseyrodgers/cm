package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

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
                if(CmShared.getQueryParameter("run_id") != null) {
                    int runId = Integer.parseInt(CmShared.getQueryParameter("run_id"));
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
	    	 JSONValue loginInfo = JSONParser.parse(json);
	         JSONObject o = loginInfo.isObject().get("userInfo").isObject();
	         JSONObject nextAction = loginInfo.isObject().get("nextAction").isObject();
	         
	         UserInfo ui = new UserInfo();
	         ui.setUid(getJsonInt(o.get("uid")));
	         ui.setUserName(getJsonString(o.get("userName")));
	         ui.setAutoTestMode(o.get("autoTestMode").isBoolean().booleanValue());
	         ui.setBackgroundStyle(getJsonString(o.get("backgroundStyle")));
	         ui.setCorrectPercent(getJsonInt(o.get("correctPercent")));
	         ui.setCustomProgram(o.get("customProgram").isBoolean().booleanValue());
	         ui.setDemoUser(o.get("demoUser").isBoolean().booleanValue());
	         ui.setFirstView(o.get("firstView").isBoolean().booleanValue());
	         ui.setLimitGames(o.get("limitGames").isBoolean().booleanValue());
	         ui.setLoginName(getJsonString(o.get("loginName")));
	         ui.setPassPercentRequired(getJsonInt(o.get("passPercentRequired")));
	         ui.setPassword(getJsonString(o.get("password")));
	         ui.setRunId(getJsonInt(o.get("runId")));
	         ui.setSessionCount(getJsonInt(o.get("sessionCount")));
	         ui.setSessionNumber(getJsonInt(o.get("sessionNumber")));
	         ui.setShowWorkRequired(o.get("showWorkRequired").isBoolean().booleanValue());
	         ui.setSubTitle(getJsonString(o.get("subTitle")));
	         ui.setTestId(getJsonInt(o.get("testId")));
	         ui.setProgramName(getJsonString(o.get("testName")));
	         ui.setProgramSegment(getJsonInt(o.get("testSegment")));
	         ui.setProgramSegmentCount(getJsonInt(o.get("programSegmentCount")));
	         ui.setTutoringAvail(o.get("tutoringAvail").isBoolean().booleanValue());
	         ui.setViewCount(getJsonInt(o.get("viewCount")));
	         
	         
	         String accountType = getJsonString(o.get("userAccountType"));
	         if(accountType.equals("SCHOOL_USER")) {  
	        	 ui.setUserAccountType(AccountType.SCHOOL_TEACHER);
	         }
	         else {
	        	 ui.setUserAccountType(AccountType.PARENT_STUDENT);
	         }
	         
	         String onCompletion = getJsonString(o.get("onCompletion"));
	         if(onCompletion.equals("AUTO_ADVANCE")) {
	        	 ui.setOnCompletion(UserProgramCompletionAction.AUTO_ADVANCE);
	         }
	         else {
	        	 ui.setOnCompletion(UserProgramCompletionAction.STOP);
	         }
	         
	         
             // if run_id passed in, then allow user to view_only
             if(CmShared.getQueryParameter("run_id") != null) {
                 int runId = Integer.parseInt(CmShared.getQueryParameter("run_id"));
                 // setup user to masquerade as real user
                 ui.setRunId(runId);
                 ui.setActiveUser(false);
             }
             else {
                 ui.setActiveUser(true);
             }
             CmLogger.debug("UserInfo object set to: " + ui);
             

             UserInfo.setInstance(ui);
             
             // fire an event on the event bus, passing new userinfo
             EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED,ui));	 
             
             String placeVal = nextAction.get("place").isString().stringValue();
             String place = (placeVal != null && !placeVal.equals("null"))?placeVal:"PRESCRIPTION";
             return new CmDestination(place);
	         
    	}
    	catch(Exception e) {
    		throw new Exception("Could not create UserInfo from JSON", e);
    	}
    }

    
    static private String getJsonString(JSONValue o) {
    	return o.isString() != null ?o.isString().stringValue():null;
    }

    static private int getJsonInt(JSONValue o) {
    	return (int)(o.isNumber() != null?o.isNumber().doubleValue():0);
    }	
	

}
