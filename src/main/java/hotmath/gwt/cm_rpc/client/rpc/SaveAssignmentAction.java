package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveAssignmentAction implements Action<RpcData>{
    
    Assignment assignment;
    int aid;

    public SaveAssignmentAction(){}
    
    public SaveAssignmentAction(int aid,Assignment assignment) {
        this.aid = aid;
        this.assignment = assignment;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    @Override
    public String toString() {
        return "SaveAssignmentAction [assignment=" + assignment + ", aid=" + aid + "]";
    }


}
