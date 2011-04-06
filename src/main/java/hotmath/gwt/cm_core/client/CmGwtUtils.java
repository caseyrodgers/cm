package hotmath.gwt.cm_core.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;

public class CmGwtUtils {
    
    
    static Map<String, String> _queryParameters = null;
    static {
        _queryParameters = CmGwtUtils.readQueryString();
    }
    
    /**
     * Return the parameter passed on query string
     * 
     * returns null if parameter not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameter(String name) {
        return _queryParameters.get(name);
    }
    
    /** Return parameter for value or empty string if not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameterValue(String name) {
        String v = _queryParameters.get(name);
        return (v != null)?v:"";
    }
    
    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static public Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        Window.Location.getHostName();
        return m;
    }
}
