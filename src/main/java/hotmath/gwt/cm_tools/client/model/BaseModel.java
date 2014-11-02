package hotmath.gwt.cm_tools.client.model;

import java.util.HashMap;
import java.util.Map;

/** temp replacement for GXT 2 BaseModelData
 * 
 * @author casey
 *
 */
public class BaseModel {

	private static final long serialVersionUID = -8370354125432687765L;
	
	Map<String,Object> _map = new HashMap<String, Object>();
	
	public <X> X get(String property) {
		return (X)_map.get(property);
	}
	
	
	 public <X> X set(String property, Object value) {
		 return (X)_map.put(property,  value);
	 }
}
