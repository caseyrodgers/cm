package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;

public class GetActiveGroupsAction implements Action<CmList<GroupInfoModel>> {
    
    Integer uid;
    
    public GetActiveGroupsAction() {}
    
    public GetActiveGroupsAction(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}
