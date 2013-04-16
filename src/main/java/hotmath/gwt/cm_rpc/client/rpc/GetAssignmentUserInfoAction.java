package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_mobile_assignments.client.user.CmMobileAssignmentUser;

public class GetAssignmentUserInfoAction implements Action<CmMobileAssignmentUser>{
    private int uid;

    public GetAssignmentUserInfoAction(){}
    
    public GetAssignmentUserInfoAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

}
