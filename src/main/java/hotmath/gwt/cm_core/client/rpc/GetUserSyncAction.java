package hotmath.gwt.cm_core.client.rpc;

import hotmath.gwt.cm_core.client.model.UserSyncInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetUserSyncAction implements Action<UserSyncInfo> {

	private static final long serialVersionUID = 4531006320763180455L;

	int uid;
    private int userActiveMinutes;
    boolean fullSyncCheck;

    public GetUserSyncAction() {}
    
    public GetUserSyncAction(int uid) {
        this.uid = uid;
        this.fullSyncCheck = true;
    }

    
    public boolean isFullSyncCheck() {
        return fullSyncCheck;
    }

    public void setFullSyncCheck(boolean fullSyncCheck) {
        this.fullSyncCheck = fullSyncCheck;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUserActiveMinutes() {
        return userActiveMinutes;
    }

    public void setUserActiveMinutes(int userActiveMinutes) {
        this.userActiveMinutes = userActiveMinutes;
    }

    @Override
    public String toString() {
        return "GetUserSyncAction [uid=" + uid + ", userActiveMinutes=" + userActiveMinutes + ", fullSyncCheck=" + fullSyncCheck + "]";
    }
}
