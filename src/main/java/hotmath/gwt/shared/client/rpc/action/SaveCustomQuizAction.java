package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.model.CustomQuizId;

import java.util.List;

public class SaveCustomQuizAction implements Action<RpcData>{
    
    int adminId;
    String cpName;
    List<CustomQuizId> ids;
    
    public SaveCustomQuizAction(){}
    
    public SaveCustomQuizAction(int adminId, String cpName, List<CustomQuizId> ids) {
        this.adminId = adminId;
        this.cpName = cpName;
        this.ids = ids;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public List<CustomQuizId> getIds() {
        return ids;
    }

    public void setIds(List<CustomQuizId> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "SaveCustomQuizAction [adminId=" + adminId + ", cpName=" + cpName + ", ids=" + ids + "]";
    }
}
