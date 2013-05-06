package hotmath.gwt.cm_core.client.rpc;

import hotmath.gwt.cm_core.client.model.UserSyncInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetUserSyncAction implements Action<UserSyncInfo> {
    int uid;
    public GetUserSyncAction() {}
    
    public GetUserSyncAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "GetUserSyncAction [uid=" + uid + "]";
    }

}
