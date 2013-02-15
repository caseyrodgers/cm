package hotmath.gwt.cm_rpc.client.rpc;

public class CopyAssignmentAction implements Action<RpcData>{
    
    int assKey;
    int aid;

    public CopyAssignmentAction() {}
    
    public CopyAssignmentAction(int aid,int assKey ) {
        this.aid = aid;
        this.assKey = assKey;
    }

    public int getAssKey() {
        return assKey;
    }

    public void setAssKey(int assKey) {
        this.assKey = assKey;
    }

    public int getAid() {
        return aid;
    }
}
