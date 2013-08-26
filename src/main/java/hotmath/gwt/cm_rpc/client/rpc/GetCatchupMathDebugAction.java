package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

/** A debugging message 
 * 
 * @author casey
 *
 */
public class GetCatchupMathDebugAction implements Action<RpcData>{
    
    public enum DebugAction{GET_NEXT}

    private DebugAction action;;
    
    public GetCatchupMathDebugAction() {}
    
    public GetCatchupMathDebugAction(DebugAction action) {
        this.action = action;
    }

    public DebugAction getAction() {
        return action;
    }

    public void setAction(DebugAction action) {
        this.action = action;
    }
}
