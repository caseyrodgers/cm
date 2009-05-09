package hotmath.gwt.shared.client;

import hotmath.gwt.cm.client.util.CmUserException;
import hotmath.gwt.cm.client.util.UserInfo;

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

    @Override
    public void onModuleLoad() {
        System.out.println("Catchup Math shared library loaded successfully");
    }
    
    

    static Map<String,String> _queryParameters = new HashMap<String,String>();

    /** Return the user id for this given user
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
        int runId=0;
        String key2="";
        try {
            if(_queryParameters.get("uid") != null)
                userId = Integer.parseInt(_queryParameters.get("uid"));

            // for testing, if uid is passed allow it override cookie
            if(userId == 0) {
                String cmKey = Cookies.getCookie("cm_key");
                if(cmKey == null) {
                    throw new Exception("No login cookie found");   
                }
                cmKey = cmKey.substring(1,cmKey.length()-1);
                
                JSONValue jsonValue = JSONParser.parse(cmKey);
                JSONObject o = jsonValue.isObject();
                String keyVal = o.get("key").isString().stringValue();
                if(keyVal == null) {
                    throw new Exception("Invalid security key found in cookie");
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
                userId = (int)o.get("uid").isNumber().doubleValue();
                if(userId == 0) {
                    throw new Exception("'uid' is not valid");
                }
            }
            
            
            // if run_id passed in, then allow user to view_only
            if(_queryParameters.get("run_id") != null) {
                runId = Integer.parseInt(_queryParameters.get("run_id"));
                // setup user to mascarade as real user
                UserInfo user = new UserInfo(0,0);
                user.setRunId(runId);
                user.setSessionNumber(0);
                user.setUid(userId);
                UserInfo.setInstance(user);
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
