package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class CloseAssignmentAction implements Action<RpcData>{
    
    private int uid;
    private int assignKey;

    public CloseAssignmentAction() {}
    
    public CloseAssignmentAction(int uid, int assignKey) {
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
        return "CloseAssignmentAction [uid=" + uid + ", assignKey=" + assignKey + "]";
    }

}
