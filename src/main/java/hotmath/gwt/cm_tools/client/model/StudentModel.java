package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StudentModel extends BaseModelData  {

	private static final long serialVersionUID = 2950521146425989628L;
	
	public static final String GROUP_KEY = "group";
	public static final String GROUP_ID_KEY = "groupId";
	public static final String SECTION_NUM_KEY = "sectionNum";
	
	public static final String UID_KEY = "uid";
	public static final String EMAIL_KEY = "email";
	public static final String ADMIN_UID_KEY = "admin_uid";

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getPasscode() {
		return get("passcode");
	}

	public void setPasscode(String passcode) {
		set("passcode", passcode);
	}

	public String getProgramDescr() {
		return get("program");
	}

	public void setProgramDescr(String progDescr) {
		set("program", progDescr);
	}

	public String getGroup() {
		return get(GROUP_KEY);
	}

	public void setGroup(String group) {
		set(GROUP_KEY, group);
	}
	
	public String getGroupId() {
		return get(GROUP_ID_KEY);
	}
	
	public void setGroupId(String groupId) {
		set(GROUP_ID_KEY, groupId);
	}

	public void setSectionNum(Integer sectionNum) {
		set(SECTION_NUM_KEY, sectionNum);
	}
	
	public Integer getSectionNum() {
		return get(SECTION_NUM_KEY);
	}
	
	public void setLastLogin(String lastLogin) {
		set("last-login", lastLogin);
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public void setTotalUsage(String totalUsage) {
		set("total-usage", totalUsage);
	}

	public String getTotalUsage() {
		return get("total-usage");
	}

	public String getPassPercent() {
		return get("pass-percent");
	}

	public void setPassPercent(String passPercent) {
		set("pass-percent", passPercent);
	}

	public void setTutoringState(String tutoringState) {
		set("tutoring-state", tutoringState);
	}
	
	public String getTutoringState() {
		return get("tutoring-state");
	}

	public void setUid(Integer uid) {
		set(UID_KEY, uid);
	}
	
	public Integer getUid() {
		return get(UID_KEY);
	}

	public void setEmail(String emailAddr) {
		set(EMAIL_KEY, emailAddr);
	}
	
	public String getEmail() {
		return get(EMAIL_KEY);
	}

	public void setAdminUid(Integer adminUid) {
		set(ADMIN_UID_KEY, adminUid);
	}
	
	public Integer getAdminUid() {
		return get(ADMIN_UID_KEY);
	}
	
	public void setProgramChanged(Boolean changed) {
		set("programChanged", changed);
	}
	
	public Boolean getProgramChanged() {
		return get("programChanged");
	}

	public StudentModel() {
	}
	
}
