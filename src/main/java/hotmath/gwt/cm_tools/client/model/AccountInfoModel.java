package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AccountInfoModel extends BaseModelData  {

	private static final long serialVersionUID = 5273566482319153369L;

	public static final String SCHOOL_NAME_KEY = "school-name";
	public static final String SCHOOL_USER_NAME_KEY = "school-name";
	public static final String EXPIRATION_DATE_KEY = "expiration-date";
	public static final String HAS_TUTORING_KEY = "has-tutoring";
	
	public String getSchoolName() {
		return get(SCHOOL_NAME_KEY);
	}

	public void setSchoolName(String schoolName) {
		set(SCHOOL_NAME_KEY, schoolName);
	}

	public String getSchoolUserName() {
		return get(SCHOOL_USER_NAME_KEY);
	}
	
	public void setSchoolUserName(String schoolUserName) {
		set(SCHOOL_USER_NAME_KEY, schoolUserName);
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

	public String getExpirationDate() {
		return get(EXPIRATION_DATE_KEY);
	}
	
	public void setExpirationDate(String expirationDate) {
		set (EXPIRATION_DATE_KEY, expirationDate);
	}
	
	public String getStatus() {
		return get("status");
	}

	public void setStatus(String status) {
		set("status", status);
	}
	
	public Boolean getHasTutoring() {
		return get(HAS_TUTORING_KEY);
	}

	public void setHasTutoring(Boolean hasTutoring) {
		set (HAS_TUTORING_KEY, hasTutoring);
	}
	
	public AccountInfoModel() {
	}
}
