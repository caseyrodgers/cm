package hotmath.gwt.cm_rpc_assignments.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetStudentAssignmentAction implements Action<StudentAssignment>{
    
    private int assignKey;
    int uid;

    public GetStudentAssignmentAction() {}
    
    public GetStudentAssignmentAction(int uid, int assignKey) {
        this.uid = uid;
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "GetStudentAssignmentAction [assignKey=" + assignKey + ", uid=" + uid + "]";
    }

}
