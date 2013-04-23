package hotmath.gwt.cm_mobile_assignments.client.rpc;

import hotmath.gwt.cm_mobile_assignments.client.user.CmMobileAssignmentUser;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

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
