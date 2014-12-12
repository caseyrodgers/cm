package hotmath.gwt.cm_core.client;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

public class CmCore implements EntryPoint {
	
    private static boolean _isDebug;
    private static Map<String, String> _queryParameters;

    public void onModuleLoad() {
        _queryParameters = readQueryString();
        _isDebug = (getQueryParameterValue("debug") != null);
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
        String v = _queryParameters.get(name);
        return (v != null) ? v : "";
    }

    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        Window.Location.getHostName();
        return m;
    }


}