package hotmath.gwt.cm_rpc_core.client.rpc;


import java.util.List;

/** Simple wrapper around List to allow returning as command response
 * 
 * @author bob
 *
 * @param <T>
 */
public interface CmList<T> extends Response, List<T> {

}
