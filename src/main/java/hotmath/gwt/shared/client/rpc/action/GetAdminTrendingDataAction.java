package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.rpc.Action;

public class GetAdminTrendingDataAction implements Action<CmAdminTrendingDataI>{
    
    Integer adminId;
    GetStudentGridPageAction dataAction;
    
    public GetAdminTrendingDataAction() {}
    
    public GetAdminTrendingDataAction(Integer adminId, GetStudentGridPageAction action) {
        this.adminId = adminId;
        this.dataAction = action;
    }

    public GetStudentGridPageAction getDataAction() {
        return dataAction;
    }

    public void setDataAction(GetStudentGridPageAction dataAction) {
        this.dataAction = dataAction;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}
