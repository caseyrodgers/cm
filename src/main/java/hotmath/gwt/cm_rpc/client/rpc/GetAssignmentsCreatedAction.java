package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.Assignment;

public class GetAssignmentsCreatedAction implements Action<CmList<Assignment>>{
    
    private int aid;
    private int groupId;
    
    public GetAssignmentsCreatedAction() {}

    public GetAssignmentsCreatedAction(int aid, int groupId) {
        this.aid = aid;
        this.groupId = groupId;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    

    @Override
    public String toString() {
        return "GetAssignmentsCreatedAction [aid=" + aid + ", groupId=" + groupId + "]";
    }
    
}
