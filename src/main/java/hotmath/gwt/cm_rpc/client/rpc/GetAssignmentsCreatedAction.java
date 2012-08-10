package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.Assignment;

public class GetAssignmentsCreatedAction implements Action<CmList<Assignment>>{
    
    private int aid;

    public GetAssignmentsCreatedAction(){}
    
    public GetAssignmentsCreatedAction(int aid) {
        this.aid = aid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }
}
