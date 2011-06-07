package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;

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
