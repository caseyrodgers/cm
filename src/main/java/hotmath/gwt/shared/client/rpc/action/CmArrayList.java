package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Response;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/** Simple wrapper around ArrayList to allow returning as command response
 * 
 * @author casey
 *
 * @param <x>
 */
public class CmArrayList<x> extends ArrayList<x> implements Response, IsSerializable {

}
