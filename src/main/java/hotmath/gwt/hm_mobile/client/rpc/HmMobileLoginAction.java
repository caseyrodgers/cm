package hotmath.gwt.hm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.HmMobileActionBase;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;

public class HmMobileLoginAction extends HmMobileActionBase implements Action<HmMobileLoginInfo> {

	private static final long serialVersionUID = 6669211783141017189L;

	String userName;
	String password;

	public HmMobileLoginAction(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
    public String toString() {
	    return "HmMobileLoginAction [userName=" + userName + ", password=" + password + "]";
    }

}
