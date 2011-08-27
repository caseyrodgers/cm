package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.UserLoginResponse;

public class GetUserInfoAction implements Action<UserLoginResponse>{
    
    int userId;
    String loginName;
    boolean flashSupported=true;

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
    
    public boolean isFlashSupported() {
        return flashSupported;
    }

    public void setFlashSupported(boolean flashSupported) {
        this.flashSupported = flashSupported;
    }

    @Override
    public String toString() {
        return "GetUserInfoAction [userId=" + userId + ", loginName=" + loginName + ", flashSupported="
                + flashSupported + "]";
    }

}
