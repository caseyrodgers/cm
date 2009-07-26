package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.UserInfo;

public class GetUserInfoAction implements Action<UserInfo> {
    
    int userId;
    public GetUserInfoAction(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
