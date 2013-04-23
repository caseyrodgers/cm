package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.AssignmentStatus;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetAssignmentStatusAction implements Action<AssignmentStatus>{
    
    int assKey;
    int aid;

    public GetAssignmentStatusAction() {}
    
    public GetAssignmentStatusAction(int aid,int assKey ) {
        this.aid = aid;
        this.assKey = assKey;
    }

    public int getAssKey() {
        return assKey;
    }

    public void setAssKey(int assKey) {
        this.assKey = assKey;
    }

    public int getAid() {
        return aid;
    }
}
