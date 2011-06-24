package hotmath.gwt.cm_mobile_shared.client;

import java.util.ArrayList;
import java.util.List;

/** Parse a URL token resource into components
 * 
 * @author casey
 */


public class TokenParserGeneric {


	List<String> values = new ArrayList<String>();
	
    public TokenParserGeneric() {
    }
    
    
    public TokenParserGeneric(String token) {
        String p[] = token.split(":");
        for(String v:p) {
        	values.add(v);
        }
    }
    
    public String getToken(int which) {
    	return values.get(which);
    }
    
    public int getCount() {
    	return values.size();
    }
}
