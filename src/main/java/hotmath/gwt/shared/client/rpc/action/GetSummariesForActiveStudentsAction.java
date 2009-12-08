package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;

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
