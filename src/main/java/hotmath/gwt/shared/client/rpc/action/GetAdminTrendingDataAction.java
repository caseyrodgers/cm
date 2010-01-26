package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.rpc.Action;

public class GetAdminTrendingDataAction implements Action<CmAdminTrendingDataI>{
    
    Integer adminId;
    
    public GetAdminTrendingDataAction() {}
    
    public GetAdminTrendingDataAction(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}
