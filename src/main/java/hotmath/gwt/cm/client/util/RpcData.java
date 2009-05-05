package hotmath.gwt.cm.client.util;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Generic RPC data packaging
 * 
 * Manages a list of name value pairs.
 * 
 * Define the fields in constructor, then extract with get methods.
 * 
 * 
 * @author Casey
 * 
 */
public class RpcData implements IsSerializable {
    /**
     * HashMap that will always contain strings for both keys and values
     */
    private HashMap<String, String> rpcData;

    public RpcData() {
        rpcData = new HashMap<String, String>();
    }

    public RpcData(List<String> names) {
        this();
        for (String n : names) {
            int i = n.indexOf("=");
            String v = "";
            if (i > -1) {
                v = n.substring(i + 1);
                n = n.substring(0, i);
            }
            rpcData.put(n, v); // initialize with empty string
        }
    }

    public void putData(String name, String value) {
        rpcData.put(name, value);
    }
    
    public void putData(String name, int value) {
        rpcData.put(name, Integer.toString(value));
    }

    public String getDataAsString(String name) {
        return rpcData.get(name);
    }

    public int getDataAsInt(String name) {
        return Integer.parseInt(rpcData.get(name));
    }
}
