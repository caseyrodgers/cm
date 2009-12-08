package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.shared.client.rpc.Action;

public class GetActiveGroupsAction implements Action<CmList<GroupModel>> {
    
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
