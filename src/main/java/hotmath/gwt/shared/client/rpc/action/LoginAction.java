package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;

/**
 *  Login with username and password, or...
 *  
 *  
 *  TODO: why is this duplicated in LogUserInAction?
 *  
 *  
 *  
 * @author bob
 *
 */
public class LoginAction implements Action<HaUserLoginInfo> {

	private static final long serialVersionUID = 8648836581209771809L;

	String password;
    String userName;
    
    String  type;
    String  key;
    boolean isDebug;
    int     uid;
    
    boolean realLogin;
    
    String browserInfo;
    
    public LoginAction() {}
    
    public LoginAction(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public boolean isRealLogin() {
        return realLogin;
    }

    public void setRealLogin(boolean realLogin) {
        this.realLogin = realLogin;
    }

    public String getPassword() {
        return password;
    }

    public String getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

    @Override
    public String toString() {
        return "LoginAction [password=" + password + ", userName=" + userName + ", type=" + type + ", key=" + key
                + ", isDebug=" + isDebug + ", uid=" + uid + ", realLogin=" + realLogin + ", browserInfo=" + browserInfo
                + "]";
    }
}
