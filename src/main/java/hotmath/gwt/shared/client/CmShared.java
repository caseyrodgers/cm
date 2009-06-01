package hotmath.gwt.shared.client;

import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.util.CmUserException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public class CmShared implements EntryPoint {

    //@Override
    public void onModuleLoad() {
        System.out.println("Catchup Math shared library loaded successfully");
    }

    static Map<String,String> _queryParameters = new HashMap<String,String>();

    
    /** Return the parameter passed on query string 
     * 
     * @param name
     * @return
     */
    static public String getQueryParameter(String name) {
        return _queryParameters.get(name);
        
    }
    
    /** The URL to default to if cannot log in */
    final public static String CM_HOME_URL = "http://catchupmath.com";
    
    
    /** uid that corresponds to a demo account
     * 
     */
    final public static int DEMO_UID = 547;

    
    /** Check the query string and process log in.
     * 
     * Normal process is this is called with a single 'key' param that 
     * contains a 'login' key has been placed in the HA_USER_LOGIN table.
     * 
     * The login is either an 'ADMIN' or a 'STUDENT'.  In either case, we return
     * the user_id representing the validated user login.
     * 
     * If the record is located and validated (is_active == true), then this 
     * user is allowed to continue.   Otherwise, we display a standardized 
     * login failed display and return user to the CM home page.
     * 
     * 
     * 
     * @return
     * @throws CmUserException
     */
    static public int handleLoginProcess() throws CmUserException {
        
        _queryParameters = readQueryString();
        
        // first see if run_id is passed, if so
        // the user is in 'view' mode and we must
        // inform the server not to update the 
        // current state as the user moves around system.
        int userId=0;
        String key2="";
        try {
            
            if(_queryParameters.get("uid") != null) {
                // for debugging .. perhaps this should not be allowed 
                // during normal processing.
                
                // check for special case demo user
                if(_queryParameters.get("uid").equals("demo")) {
                    userId = DEMO_UID;
                }
                else {
                    userId = Integer.parseInt(_queryParameters.get("uid"));
                }
            }

            // for testing, if uid is passed allow it override cookie
            if(userId == 0) {
                String cmKey = Cookies.getCookie("cm_key");
                if(cmKey == null) {
                    throw new Exception("No login key found");   
                }
                cmKey = cmKey.substring(1,cmKey.length()-1);
                
                JSONValue jsonValue = JSONParser.parse(cmKey);
                JSONObject o = jsonValue.isObject();
                String keyVal = o.get("key").isString().stringValue();
                System.out.println("keyVal: " + keyVal);
                if(keyVal == null) {
                    throw new Exception("Invalid security key found");
                }
                if(_queryParameters.get("key") != null) {
                    key2 = _queryParameters.get("key");
                }
                
                if(key2 == null) {
                    throw new Exception("No key parameter found");
                }
                if(!key2.equals(keyVal)) {
                    throw new Exception("Security exception: key values do not match");
                }
                
                // we are valid ...
                //
                boolean isAdmin = false;
                try {
                    userId = (int)o.get("uid").isNumber().doubleValue();
                }
                catch (Exception e) {
                	System.out.println("Ignoring Exception: " + e.getLocalizedMessage());
                }

                if (userId == 0) {
                	try {
                    	userId = (int)o.get("aid").isNumber().doubleValue();
                	}
                	catch (Exception e) {
                		System.out.println("Ignoring Exception: " + e.getLocalizedMessage());
                	}
                    if (userId != 0) {
                    	isAdmin = true;
                    }
                }
                
                if(userId == 0) {
                    throw new Exception("'uid' is not valid");
                }
                UserInfoBase user = UserInfoBase.getInstance();
                user.setUid(userId);
                user.setIsAdmin(isAdmin);
            }
            return userId;
        }
        catch(Exception e) {
            throw new CmUserException(e.getMessage());
        }        
    }
    

    
    
    /** Convert string+list to string+string
     * 
     */
    static private Map<String,String> readQueryString() {
        Map<String,String> m = new HashMap<String,String>();
        Map<String,List<String>> query = Window.Location.getParameterMap();
        for(String s: query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        return m;
    }

}
