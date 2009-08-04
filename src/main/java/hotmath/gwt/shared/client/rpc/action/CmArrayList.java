package hotmath.gwt.shared.client.rpc.action;

import java.util.ArrayList;

/** Simple wrapper around ArrayList to allow returning as command response
 * 
 * @author casey
 *
 * @param <x>
 */
public class CmArrayList<x> extends ArrayList<x> implements CmList<x> {

	private static final long serialVersionUID = -5979089962747405346L;

}
