package hotmath.gwt.cm_rpc.client.rpc;

public class ActivateAssignmentAction implements Action<RpcData> {
    
    private int assignmentKey;

    public ActivateAssignmentAction(){}
    
    public ActivateAssignmentAction(int assignmentKey) {
        this.assignmentKey = assignmentKey;
    }

    public int getAssignmentKey() {
        return assignmentKey;
    }

    public void setAssignmentKey(int assignmentKey) {
        this.assignmentKey = assignmentKey;
    }

    @Override
    public String toString() {
        return "ActivateAssignmentAction [assignmentKey=" + assignmentKey + "]";
    }

}
