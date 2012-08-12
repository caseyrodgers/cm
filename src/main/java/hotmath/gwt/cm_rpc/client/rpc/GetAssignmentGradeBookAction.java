package hotmath.gwt.cm_rpc.client.rpc;

public class GetAssignmentGradeBookAction implements Action<RpcData> {
    private int assignKey;

    public GetAssignmentGradeBookAction(){}
    
    public GetAssignmentGradeBookAction(int assignKey) {
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
        return "GetAssignmentGradeBookAction [assignKey=" + assignKey + "]";
    }

}
