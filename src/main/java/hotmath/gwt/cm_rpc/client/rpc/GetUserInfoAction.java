package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.UserLoginResponse;

public class GetUserInfoAction implements Action<UserLoginResponse>{
    
    int userId;
    String loginName;

    public GetUserInfoAction() {}
    
    public GetUserInfoAction(int userId, String loginName) {
        this.userId = userId;
        this.loginName = loginName;
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
    
    
    @Override
    public String toString() {
        return "GetUserInfoAction [userId=" + userId + ", loginName=" + loginName +  "]";
    }

}
