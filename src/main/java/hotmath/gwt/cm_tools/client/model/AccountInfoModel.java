package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AccountInfoModel extends BaseModelData  {

	private static final long serialVersionUID = 5273566482319153369L;

	public String getSchoolName() {
		return get("school-name");
	}

	public void setSchoolName(String schoolName) {
		set("school-name", schoolName);
	}

	public String getPasscode() {
		return get("passcode");
	}

	public void setPasscode(String passcode) {
		set("passcode", passcode);
	}

	public String getLastLogin() {
		return get("last-login");
	}

	public void setLastLogin(String lastLogin) {
		set("last-login", lastLogin);
	}

	public String getStatus() {
		return get("status");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public AccountInfoModel() {
	}
}
