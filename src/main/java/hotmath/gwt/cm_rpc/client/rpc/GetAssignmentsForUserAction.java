package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetAssignmentsForUserAction implements Action<CmList<StudentAssignmentInfo>>{
    
    private int uid;

    public GetAssignmentsForUserAction(){}
    
    public GetAssignmentsForUserAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "GetAssignmentsForUserAction [uid=" + uid + "]";
    }
}
