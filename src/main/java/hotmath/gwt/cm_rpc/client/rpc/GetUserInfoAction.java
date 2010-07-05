package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.shared.client.util.UserInfo;

public class GetUserInfoAction implements Action<UserInfo>{
    
    int userId;
    public GetUserInfoAction() {}
    
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
