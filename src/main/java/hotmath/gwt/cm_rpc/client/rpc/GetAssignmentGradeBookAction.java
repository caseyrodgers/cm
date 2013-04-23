package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetAssignmentGradeBookAction implements Action<CmList<StudentAssignment>> {
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
