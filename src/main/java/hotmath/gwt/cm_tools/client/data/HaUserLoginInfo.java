package hotmath.gwt.cm_tools.client.data;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class HaUserLoginInfo implements Response {
	
	private HaBasicUser haUser;
	
	private HaLoginInfo haLoginInfo;
	
	public HaUserLoginInfo(HaBasicUser haUser, HaLoginInfo loginInfo) {
		this.haUser = haUser;
		this.haLoginInfo = loginInfo;
	}

	public HaBasicUser getHaUser() {
		return haUser;
	}

	public void setHaUser(HaBasicUser haUser) {
		this.haUser = haUser;
	}

	public HaLoginInfo getHaLoginInfo() {
		return haLoginInfo;
	}

	public void setHaLoginInfo(HaLoginInfo loginInfo) {
		this.haLoginInfo = loginInfo;
	}
}
