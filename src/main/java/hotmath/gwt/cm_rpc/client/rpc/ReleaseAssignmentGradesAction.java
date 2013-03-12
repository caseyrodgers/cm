package hotmath.gwt.cm_rpc.client.rpc;

public class ReleaseAssignmentGradesAction implements Action<RpcData>{
    
    private int assignKey;

    public ReleaseAssignmentGradesAction(){}
    
    public ReleaseAssignmentGradesAction(int assignKey) {
        this.setAssignKey(assignKey);
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

}
