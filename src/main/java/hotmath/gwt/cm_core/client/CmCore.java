package hotmath.gwt.cm_core.client;


import hotmath.gwt.cm_rpc.client.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

public class CmCore implements EntryPoint {
	
    private static boolean _isDebug;
    private static Map<String, String> _queryParameters;

    public void onModuleLoad() {
    }

    public static boolean isDebug() {
    	return _isDebug;
    }

    /**
     * Return parameter for value or empty string if not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameterValue(String name) {
        String v = getQueryParameter(name);
        return (v != null) ? v : "";
    }
    
    static public String getQueryParameter(String name) {
        if(_queryParameters == null) {
            _queryParameters = readQueryString();
        }
        return _queryParameters.get(name);
    }
    

    /**
     * Reload the current user's page allowing any changed program configuration
     * to take effect.
     * 
     * Make sure there is a uid parameter to deal with possible GETs
     * 
     */
    static public void reloadUser() {
        String queryString = "";
        boolean hasUid = false;
        for (String k : _queryParameters.keySet()) {
            if (queryString.length() > 0) {
                queryString += "&";
            }
            if (k.equals("uid")) {
                hasUid = true;
            }
            queryString += k + "=" + _queryParameters.get(k);
        }
        if (!hasUid) {
            if (queryString.length() > 0) {
                queryString += "&";
            }
            queryString += "uid=" + UserInfo.getInstance().getUid();
        }
        Window.Location.replace("/loginService?" + queryString);
    }

    /**
     * Convert string+list to string+string of all URL parameters
     * 
     * set global debug flag
     * 
     */
    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        Window.Location.getHostName();
        
        _isDebug = (m.get("debug") != null);
        return m;
    }

    public static void setQueryParameter(String name, String value) {
        _queryParameters.put(name,  value);
    }

    public static void removeQueryParameter(String name) {
        _queryParameters.remove(name);
    }
}