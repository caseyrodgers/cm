package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.Assignment;

public class GetAssignmentAction implements Action<Assignment>{
    
    private int assignKey;

    public GetAssignmentAction() {}
    
    public GetAssignmentAction(int assignKey) {
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
        return "GetAssignmentAction [assignKey=" + assignKey + "]";
    }
}
