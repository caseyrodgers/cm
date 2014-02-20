package hotmath.gwt.cm_rpc.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;

public class AddAdminTeacherAction implements Action<RpcData> {
    
    private int adminId;
    private String teacherName;

    public AddAdminTeacherAction(){}
    
    public AddAdminTeacherAction(int adminId, String teacherName) {
        this.adminId = adminId;
        this.teacherName = teacherName;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    

}
