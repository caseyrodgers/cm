package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class DoWebLinksCrudOperationAction implements Action<RpcData> {
    
    public enum CrudOperation {
        ADD,DELETE,UPDATE, IMPORT_FROM_PUBLIC
    }
    
    CrudOperation operation;
    private int adminId;
    private WebLinkModel webLink;
    
    public DoWebLinksCrudOperationAction(){}
    
    public DoWebLinksCrudOperationAction(int adminId,CrudOperation operation, WebLinkModel webLink) {
        this.adminId = adminId;
        this.operation = operation;
        this.webLink = webLink;
    }

    public CrudOperation getOperation() {
        return operation;
    }

    public void setOperation(CrudOperation operation) {
        this.operation = operation;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public WebLinkModel getWebLink() {
        return webLink;
    }

    public void setWebLink(WebLinkModel webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
        return "DoWebLinksCrudOperationAction [operation=" + operation + ", adminId=" + adminId + ", webLink=" + webLink + "]";
    }

}
