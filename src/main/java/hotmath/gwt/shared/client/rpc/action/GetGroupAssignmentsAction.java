package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_core.client.model.GroupCopyModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetGroupAssignmentsAction implements Action<CmList<GroupCopyModel>> {
    
    private int adminId;

    public GetGroupAssignmentsAction(){}
    
    public GetGroupAssignmentsAction(int adminId) {
        this.adminId = adminId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "GetGroupAssignmentsAction [adminId=" + adminId + "]";
    }
}
