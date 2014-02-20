package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetAdminTeachersAction implements Action<CmList<TeacherIdentity>>{
    
    private int adminId;

    public GetAdminTeachersAction(){}
    
    public GetAdminTeachersAction(int adminId) {
        this.adminId = adminId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
