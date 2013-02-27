package hotmath.gwt.cm_rpc.client.rpc;

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
