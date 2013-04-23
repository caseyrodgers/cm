package hotmath.gwt.cm_rpc_core.client.rpc;


import java.util.ArrayList;

/** Simple wrapper around ArrayList to allow returning as command response
 * 
 * @author casey
 *
 * @param <x>
 */
public class CmArrayList<x> extends ArrayList<x> implements CmList<x> {
}
