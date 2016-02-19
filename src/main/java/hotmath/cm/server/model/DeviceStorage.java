package hotmath.cm.server.model;

import java.util.HashMap;
import java.util.Map;

public class DeviceStorage {
	
	Map<String, String> storage = new HashMap<String,String>();
	
	public DeviceStorage() {
	}
	
	public Map<String, String> getStorage() {
		return storage;
	}
}
