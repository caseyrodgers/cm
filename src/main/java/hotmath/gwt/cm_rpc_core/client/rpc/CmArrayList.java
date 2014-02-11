package hotmath.gwt.cm_rpc_core.client.rpc;


import java.util.ArrayList;
import java.util.List;

/** Simple wrapper around ArrayList to allow returning as command response
 * 
 * @author casey
 *
 * @param <x>
 */
public class CmArrayList<x> extends ArrayList<x> implements CmList<x> {

    public CmArrayList() {}
    
    public CmArrayList(List<x> all) {
        addAll(all);
    }
}
