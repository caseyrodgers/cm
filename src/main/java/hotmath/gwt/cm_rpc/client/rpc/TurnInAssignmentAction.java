package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class TurnInAssignmentAction implements Action<RpcData>{
    
    private int assignKey;
    private int uid;

    public TurnInAssignmentAction(){}
    
    public TurnInAssignmentAction(int uid, int assignKey) {
        this.uid = uid;
        this.assignKey = assignKey;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    @Override
    public String toString() {
        return "TurnInAssignmentAction [assignKey=" + assignKey + ", uid=" + uid + "]";
    }
}
