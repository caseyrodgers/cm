package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetWebLinksForAdminAction implements Action<CmList<WebLinkModel>>{

    public enum TypeOfGet {PRIVATE, PUBLIC};
    
    private int adminId;
    private TypeOfGet typeOfGet;

    public GetWebLinksForAdminAction() {}
    
    public GetWebLinksForAdminAction(TypeOfGet typeOfGet, int adminId) {
        this.typeOfGet = typeOfGet;
        this.adminId = adminId;
    }

    
    public TypeOfGet getTypeOfGet() {
        return typeOfGet;
    }

    public void setTypeOfGet(TypeOfGet typeOfGet) {
        this.typeOfGet = typeOfGet;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "GetWebLinksForAdminAction [adminId=" + adminId + ", typeOfGet=" + typeOfGet + "]";
    }
}
