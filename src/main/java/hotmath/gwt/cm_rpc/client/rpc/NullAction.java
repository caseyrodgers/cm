package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.server.service.ActionHandlerManualConnectionManagement;


/** Null do nothing action for testing
 * 
 * @author casey
 *
 */
public class NullAction extends ActionBase implements Action<RpcData>, ActionHandlerManualConnectionManagement {

    public NullAction() {}
}
