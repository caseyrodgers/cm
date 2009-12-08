package hotmath.gwt.shared.client.util;

/** Throws as placeholder for GWT RPC classes being replaced
 * by command objects.
 * 
 * @author casey
 *
 */
public class CmRpcExceptionUseAction extends CmRpcException {
	public CmRpcExceptionUseAction() {
	    super("Use the associated hotmath.gwt.shared.client.rpc action");
	}
}
