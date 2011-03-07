package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CustomQuizDef;

public class GetCustProgQuizDefsAction implements Action<CmList<CustomQuizDef>>{
    
    int adminId;
    public GetCustProgQuizDefsAction(){}
    
    public GetCustProgQuizDefsAction(int adminId) {
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
        return "GetCustProgQuizDefsAction [adminId=" + adminId + "]";
    }
}
