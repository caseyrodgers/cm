package hotmath.gwt.shared.client.rpc.action;

import com.google.gwt.user.client.rpc.IsSerializable;

import hotmath.gwt.shared.client.rpc.Response;

import java.util.List;

/** Simple wrapper around List to allow returning as command response
 * 
 * @author bob
 *
 * @param <x>
 */
public interface CmList<x> extends Response, IsSerializable, List<x> {

}
