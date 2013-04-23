package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class CopyAssignmentAction implements Action<RpcData>{
    
    int assKey;

    public CopyAssignmentAction() {}
    
    public CopyAssignmentAction(int assKey ) {
        this.assKey = assKey;
    }

    public int getAssKey() {
        return assKey;
    }

    public void setAssKey(int assKey) {
        this.assKey = assKey;
    }
}
