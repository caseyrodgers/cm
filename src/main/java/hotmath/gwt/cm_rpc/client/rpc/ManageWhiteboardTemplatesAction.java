package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class ManageWhiteboardTemplatesAction implements Action<RpcData> {
    
    public enum ManageType{DELETE}

    private ManageType type;
    private int adminId;
    private String templateName;
    
    public ManageWhiteboardTemplatesAction() {}
    
    public ManageWhiteboardTemplatesAction(int adminId, String templateName, ManageType type) {
        this.adminId = adminId;
        this.templateName = templateName;
        this.type = type;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public ManageType getType() {
        return type;
    }

    public void setType(ManageType type) {
        this.type = type;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    
}
