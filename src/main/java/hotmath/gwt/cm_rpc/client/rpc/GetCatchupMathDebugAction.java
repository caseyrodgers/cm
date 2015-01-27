package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

/** A debugging message 
 * 
 * @author casey
 *
 */
public class GetCatchupMathDebugAction implements Action<RpcData>{
    
    public enum DebugAction{GET_NEXT, SETUP_TRANSISTION_TEST}
    int runId;

    private DebugAction action;;
    
    public GetCatchupMathDebugAction() {}
    
    public GetCatchupMathDebugAction(DebugAction action) {
        this.action = action;
    }

    
    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public DebugAction getAction() {
        return action;
    }

    public void setAction(DebugAction action) {
        this.action = action;
    }
}
