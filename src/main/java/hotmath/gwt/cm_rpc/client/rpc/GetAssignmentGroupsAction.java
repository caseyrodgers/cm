package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.GroupDto;

public class GetAssignmentGroupsAction implements Action<CmList<GroupDto>>{
    
    private int aid;

    public GetAssignmentGroupsAction(){}
    
    public GetAssignmentGroupsAction(int aid) {
        this.aid = aid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    @Override
    public String toString() {
        return "GetAssignmentGroupsAction [aid=" + aid + "]";
    }

}
