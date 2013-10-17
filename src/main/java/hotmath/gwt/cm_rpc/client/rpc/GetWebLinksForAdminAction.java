package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetWebLinksForAdminAction implements Action<CmList<WebLinkModel>>{
    
    private int adminId;

    public GetWebLinksForAdminAction() {}
    
    public GetWebLinksForAdminAction(int adminId) {
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
        return "GetWebLinksForAdminAction [adminId=" + adminId + "]";
    }
}
