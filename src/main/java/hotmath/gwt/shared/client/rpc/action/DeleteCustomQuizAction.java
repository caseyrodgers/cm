package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class DeleteCustomQuizAction implements Action<RpcData> {
    
    int adminId;
    String name;
    
    public DeleteCustomQuizAction(){}
    
    public DeleteCustomQuizAction(int adminId, String name) {
        this.adminId = adminId;
        this.name = name;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DeleteCustomQuizAction [adminId=" + adminId + ", name=" + name + "]";
    }
}