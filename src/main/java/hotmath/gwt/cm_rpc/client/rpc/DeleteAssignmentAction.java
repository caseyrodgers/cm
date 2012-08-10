package hotmath.gwt.cm_rpc.client.rpc;

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
