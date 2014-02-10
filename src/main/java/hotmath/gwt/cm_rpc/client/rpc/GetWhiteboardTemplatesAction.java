package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.WhiteboardTemplatesResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetWhiteboardTemplatesAction implements Action<WhiteboardTemplatesResponse>{
    
    private int adminId;

    public GetWhiteboardTemplatesAction(){}
    
    public GetWhiteboardTemplatesAction(int adminId) {
        this.adminId = adminId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

}
