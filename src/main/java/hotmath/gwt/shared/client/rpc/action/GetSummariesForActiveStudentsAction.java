package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

public class GetSummariesForActiveStudentsAction implements Action<CmList<StudentModelI>>{
    
    int adminId;

    public GetSummariesForActiveStudentsAction() {}
    
    public GetSummariesForActiveStudentsAction(Integer adminId) {
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
        return "GetSummariesForActiveStudentsAction [adminId=" + adminId + "]";
    }

}
