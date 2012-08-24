package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;

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
