package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StudentModel extends BaseModelData  {

	private static final long serialVersionUID = 2950521146425989628L;
	
	public static final String GROUP_KEY = "group";
	public static final String GROUP_ID_KEY = "groupId";
	public static final String SECTION_NUM_KEY = "sectionNum";
	public static final String USER_PROGRAM_KEY = "userProgramId";
	public static final String PROG_ID_KEY = "progId";
	public static final String SUBJ_ID_KEY = "subjId";
	public static final String CHAPTER_KEY = "chapter";
	public static final String LAST_QUIZ_KEY = "last-quiz";
	public static final String JSON_KEY = "json";
	public static final String STATUS_KEY = "status";	
	public static final String UID_KEY = "uid";
	public static final String EMAIL_KEY = "email";
	public static final String ADMIN_UID_KEY = "admin_uid";
	public static final String SHOW_WORK_KEY = "show-work";
	public static final String SHOW_WORK_STATE_KEY = "show-work-state";
	public static final String TUTORING_AVAIL_KEY = "tutoring-avail";
	public static final String TUTORING_STATE_KEY = "tutoring-state";

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

	public Integer getUserProgramId() {
		return get(USER_PROGRAM_KEY);
	}
	
	public void setUserProgramId(Integer userProgId) {
		set(USER_PROGRAM_KEY, userProgId);
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
		set(STATUS_KEY, status);
	}

	public String getStatus() {
		return get(STATUS_KEY);
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
		set(TUTORING_STATE_KEY, tutoringState);
	}
	
	public String getTutoringState() {
		return get(TUTORING_STATE_KEY);
	}

	public void setShowWorkState(String swState) {
		set(SHOW_WORK_STATE_KEY, swState);
	}
	
	public String getShowWorkState() {
		return get(SHOW_WORK_STATE_KEY);
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

	public String getProgId() {
		return get(PROG_ID_KEY);
	}
	
	public void setProgId(String progId) {
		set(PROG_ID_KEY, progId);
	}

	public String getSubjId() {
		return get(SUBJ_ID_KEY);
	}
	
	public void setSubjId(String progId) {
		set(SUBJ_ID_KEY, progId);
	}

	public String getChapter() {
		return get(CHAPTER_KEY);
	}
	
	public void setChapter(String chapter) {
		set(CHAPTER_KEY, chapter);
	}

	public String getLastQuiz() {
		return get(LAST_QUIZ_KEY);
	}
	
	public void setLastQuiz(String lastQuiz) {
		set(LAST_QUIZ_KEY, lastQuiz);
	}

	public String getJson() {
		return get(JSON_KEY);
	}
	
	public void setJson(String json) {
		set(JSON_KEY, json);
	}

	public Boolean getShowWorkRequired() {
		return get(SHOW_WORK_KEY);
	}
	
	public void setShowWorkRequired(Boolean val) {
		set(SHOW_WORK_KEY, val);
	}

	public Boolean getTutoringAvail() {
		return get(TUTORING_AVAIL_KEY);
	}
	
	public void setTutoringAvail(Boolean val) {
		set(TUTORING_AVAIL_KEY, val);
	}

	public StudentModel() {
	}
	
}
