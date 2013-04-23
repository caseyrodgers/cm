package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class DeleteAssignmentAction implements Action<RpcData>{
    
    private int assignKey;

    public DeleteAssignmentAction(){}
    
    public DeleteAssignmentAction(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    @Override
    public String toString() {
        return "DeleteAssignmentAction [assignKey=" + assignKey + "]";
    }
}
