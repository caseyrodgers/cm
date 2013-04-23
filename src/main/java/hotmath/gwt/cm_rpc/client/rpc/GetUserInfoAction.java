package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetUserInfoAction implements Action<UserLoginResponse>{
    
    int userId;
    String loginName;
    private boolean isDebug;

    public GetUserInfoAction() {}
    
    public GetUserInfoAction(int userId, String loginName) {
        this(userId, loginName, false);
    }
    public GetUserInfoAction(int userId, String loginName, boolean isDebug) {
        this.userId = userId;
        this.loginName = loginName;
        this.isDebug = isDebug;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
    
    
    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @Override
    public String toString() {
        return "GetUserInfoAction [userId=" + userId + ", loginName=" + loginName +  "]";
    }

}
