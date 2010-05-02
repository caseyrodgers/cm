package hotmath.gwt.cm_rpc.client.rpc;


import java.util.List;

/** Simple wrapper around List to allow returning as command response
 * 
 * @author bob
 *
 * @param <x>
 */
public interface CmList<x> extends Response, List<x> {

}
