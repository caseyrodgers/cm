package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetAssignmentStudentsAction implements Action<CmList<StudentDto>>{
    
    private int assignKey;
    private TYPE type;

    public GetAssignmentStudentsAction(){}
    
    public enum TYPE {ALL_IN_GROUP, ASSIGNED}
    
    public GetAssignmentStudentsAction(int assignKey, TYPE type) {
        this.assignKey = assignKey;
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }


    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }


    @Override
    public String toString() {
        return "GetAssignmentStudentsAction [assignKey=" + assignKey + ", type=" + type + "]";
    }
}
