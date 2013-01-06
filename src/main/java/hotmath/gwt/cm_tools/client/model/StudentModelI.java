package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;


public interface StudentModelI extends Response {

    public static final String GROUP_KEY = "group";
    public static final String GROUP_ID_KEY = "groupId";
    public static final String SECTION_COUNT_KEY = "sectionCount";
    public static final String SECTION_NUM_KEY = "sectionNum";
    public static final String USER_PROGRAM_KEY = "userProgramId";
    public static final String CHAPTER_KEY = "chapter";
    public static final String LAST_QUIZ_KEY = "last-quiz";
    public static final String HAS_LAST_QUIZ_KEY = "has-last-quiz";
    public static final String LAST_LOGIN_KEY = "last-login";
    public static final String HAS_LAST_LOGIN_KEY = "has-last-login";
    public static final String JSON_KEY = "json";
    public static final String STATUS_KEY = "status";
    public static final String UID_KEY = "uid";
    public static final String EMAIL_KEY = "email";
    public static final String ADMIN_UID_KEY = "admin_uid";
    public static final String SHOW_WORK_STATE_KEY = "show-work-state";
    public static final String TOTAL_USAGE_KEY = "total-usage";
    public static final String TUTORING_STATE_KEY = "tutoring-state";
    public static final String TUTORING_USE_KEY = "tutoring-use";
    public static final String HAS_TUTORING_USE_KEY = "has-tutoring-use";
    public static final String NAME_KEY = "name";
    public static final String PASSCODE_KEY = "passcode";
    public static final String BACKGROUND_STYLE = "background_style";
    public static final String DEMO_USER_KEY = "demo_user";
    public static final String PROGRAM_DESCR_KEY = "program";
    public static final String PROGRAM_TYPE_KEY  = "program-type";
    public static final String PASSING_COUNT_KEY = "passing-count";
    public static final String HAS_PASSING_COUNT_KEY = "has-passing-count";
    public static final String NOT_PASSING_COUNT_KEY = "not-passing-count";

    public static final String PROGRAM_KEY = "studyProgram";
    public static final String SETTINGS_KEY = "optionSettings";
    public static final String PASS_PERCENT_KEY = "pass-percent";
    public static final String TUTORING_AVAIL_KEY = "tutoring-avail";
    public static final String LIMIT_GAMES_KEY = "limit-games";
    public static final String STOP_AT_PROGRAM_END_KEY = "stop-at-program-end";
    public static final String SECTION_IS_SETTABLE = "section-is-settable";
    
    
    public String getName();

	public void setName(String name);

	public String getPasscode();

	public void setPasscode(String passcode);

	public String getGroup();

	public void setGroup(String group);
	
	public Integer getGroupId();
	public void setGroupId(Integer groupId);

	public void setSectionCount(Integer sectionCount);
	
	public Integer getSectionCount();

	public void setSectionNum(Integer sectionNum);
	
	public Integer getSectionNum();

	public String getLastLogin();
	
	public void setLastLogin(String lastLogin);

	public void setStatus(String status);

	public String getStatus();

	public void setTotalUsage(Integer totalUsage);

	public Integer getTotalUsage();
	
	public void setPassingCount(Integer count);
	
	public Integer getPassingCount();

	public void setNotPassingCount(Integer count);
	
	public Integer getNotPassingCount();

	public String getPassPercent();

	public void setPassPercent(String passPercent);

	public void setUid(Integer uid);
	
	public Integer getUid();
	
	public void setEmail(String emailAddr);
	
	public String getEmail();

	public void setAdminUid(Integer adminUid);
	
	public Integer getAdminUid();
	
	public void setProgramChanged(Boolean changed);

	public Boolean getProgramChanged();

	public String getChapter();
	
	public void setChapter(String chapter);

	public String getLastQuiz();
	
	public void setLastQuiz(String lastQuiz);

	public String getJson();
	
	public void setJson(String json);

	public void setTutoringUse(Integer x);
	
	public Integer getTutoringUse();

	public String getBackgroundStyle();
	
	public void setBackgroundStyle(String style);
	
	public Boolean getIsDemoUser();
	
	public void setIsDemoUser(Boolean isDemo);
	
	public StudentProgramModel getProgram();
	
	public void setProgram(StudentProgramModel studyProgram);
	
	public StudentSettingsModel getSettings();
	
	public void setSettings(StudentSettingsModel optionSettings);
	
	
	public boolean getHasExtendedData();
	public void setHasExtendedData(boolean extended);
	
    public void setShowWorkState(String swState);
    public String getShowWorkState();
}
