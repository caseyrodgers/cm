package hotmath.gwt.cm_tools.client.model;


import java.io.Serializable;

public class StudentModelExt extends BaseModel implements Serializable, StudentModelI {

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


    private boolean hasExtendedData = false;


    public StudentModelExt() {
    }

    public StudentModelExt(StudentModelI student) {
        setStudent(student);
    }



    public void setStudent(StudentModelI student) {
        set(GROUP_KEY, student.getGroup());
        set(GROUP_ID_KEY, student.getGroupId());
        set(SECTION_COUNT_KEY, student.getSectionCount());
        set(SECTION_NUM_KEY, student.getSectionNum());
        set(USER_PROGRAM_KEY, student.getProgram().getProgramId());
        set(CHAPTER_KEY, student.getChapter());

        setLastQuiz(student.getLastQuiz());
        setLastLogin(student.getLastLogin());
        setStatus(student.getStatus());
        setTutoringUse(student.getTutoringUse());
        setPassingCount(student.getPassingCount());
        setNotPassingCount(student.getNotPassingCount());
        setShowWorkState((student.getSettings().getShowWorkRequired()) ? "REQUIRED" : "OPTIONAL");
        setTutoringState((student.getSettings().getTutoringAvailable()) ? "ON" : "OFF");

        set(JSON_KEY, student.getJson());

        set(UID_KEY, student.getUid());
        set(EMAIL_KEY, student.getEmail());
        set(ADMIN_UID_KEY, student.getAdminUid());

        set(PASS_PERCENT_KEY, student.getPassPercent());

        set(TOTAL_USAGE_KEY, (student.getTotalUsage() != null)?student.getTotalUsage():0);

        set(NAME_KEY, student.getName());
        set(PASSCODE_KEY, student.getPasscode());
        set(BACKGROUND_STYLE, student.getBackgroundStyle());
        set(DEMO_USER_KEY, student.getIsDemoUser());
        set(PROGRAM_DESCR_KEY, student.getProgram().getProgramDescription());

        set(PROGRAM_KEY, student.getProgram());
        set(SETTINGS_KEY, student.getSettings());
    }

    public void assignExtendedData(StudentModelI sm) {
        setHasExtendedData(true);
        setLastQuiz(sm.getLastQuiz());
        setLastLogin(sm.getLastLogin());
        setTutoringUse(sm.getTutoringUse());
        setPassingCount(sm.getPassingCount());
        setNotPassingCount(sm.getNotPassingCount());
    }

    public Integer getTutoringUse() {
        return get(TUTORING_USE_KEY);
    }

    @Override
    public void setTutoringUse(Integer x) {
        set(TUTORING_USE_KEY, (x != null) ? x : 0);
    }

    @Override
    public String getName() {
        return get(NAME_KEY);
    }

    @Override
    public void setName(String name) {
        set(NAME_KEY, name);
    }

    @Override
    public String getPasscode() {
        return get(PASSCODE_KEY);
    }

    @Override
    public void setPasscode(String passcode) {
        set(PASSCODE_KEY, passcode);
    }

    @Override
    public String getGroup() {
        return get(GROUP_KEY);
    }

    public void setGroup(String group) {
        set(GROUP_KEY, group);
    }

    public Integer getGroupId() {
        return get(GROUP_ID_KEY);
    }

    @Override
    public void setGroupId(Integer groupId) {
        set(GROUP_ID_KEY, groupId);
    }

    @Override
    public void setSectionCount(Integer sectionCount) {
        set(SECTION_NUM_KEY, sectionCount);
    }

    @Override
    public Integer getSectionCount() {
        return get(SECTION_COUNT_KEY);
    }

    @Override
    public void setSectionNum(Integer sectionNum) {
        set(SECTION_NUM_KEY, sectionNum);
    }

    @Override
    public Integer getSectionNum() {
        return get(SECTION_NUM_KEY);
    }

    @Override
    public String getLastLogin() {
        return get(LAST_LOGIN_KEY);
    }

    @Override
    public void setLastLogin(String lastLogin) {
        set(LAST_LOGIN_KEY, (lastLogin != null) ? lastLogin : " ");
    }

    @Override
    public void setStatus(String status) {
        set(STATUS_KEY, (status != null) ? status : " ");
    }

    @Override
    public String getStatus() {
        return get(STATUS_KEY);
    }

    @Override
    public void setTotalUsage(Integer totalUsage) {
        set(TOTAL_USAGE_KEY, totalUsage);
    }

    @Override
    public Integer getTotalUsage() {
        return get(TOTAL_USAGE_KEY);
    }

    @Override
    public String getPassPercent() {
        return get(PASS_PERCENT_KEY);
    }

    @Override
    public void setPassPercent(String passPercent) {
        set(PASS_PERCENT_KEY, passPercent);
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

    @Override
    public void setUid(Integer uid) {
        set(UID_KEY, uid);
    }

    @Override
    public Integer getUid() {
        return get(UID_KEY);
    }

    @Override
    public void setEmail(String emailAddr) {
        set(EMAIL_KEY, emailAddr);
    }

    @Override
    public String getEmail() {
        return get(EMAIL_KEY);
    }

    @Override
    public void setAdminUid(Integer adminUid) {
        set(ADMIN_UID_KEY, adminUid);
    }

    @Override
    public Integer getAdminUid() {
        return get(ADMIN_UID_KEY);
    }

    @Override
    public void setProgramChanged(Boolean changed) {
        set("programChanged", changed);
    }

    /**
     * return true if program has been changed, or false if program was not
     * changed.
     *
     * If value is null, return false
     *
     * @return
     */
    @Override
    public Boolean getProgramChanged() {
        Boolean pc = get("programChanged");
        if (pc != null) {
            return get("programChanged");
        } else {
            return false;
        }
    }
    @Override
    public String getChapter() {
        return get(CHAPTER_KEY);
    }

    @Override
    public void setChapter(String chapter) {
        set(CHAPTER_KEY, chapter);
    }

    @Override
    public String getLastQuiz() {
        return get(LAST_QUIZ_KEY);
    }

    @Override
    public void setLastQuiz(String lastQuiz) {
        set(LAST_QUIZ_KEY, (lastQuiz != null) ? lastQuiz : " ");
    }

    @Override
    public String getJson() {
        return get(JSON_KEY);
    }

    @Override
    public void setJson(String json) {
        set(JSON_KEY, json);
    }

    @Override
    public String getBackgroundStyle() {
        return get(BACKGROUND_STYLE);
    }

    @Override
    public void setBackgroundStyle(String style) {
        set(BACKGROUND_STYLE, style);
    }

    @Override
    public void setIsDemoUser(Boolean isDemo) {
        set(DEMO_USER_KEY, isDemo);
    }

    @Override
    public Boolean getIsDemoUser() {
        return get(DEMO_USER_KEY);
    }

        @Override
        public Integer getNotPassingCount() {
                return get(NOT_PASSING_COUNT_KEY);
        }

        @Override
        public void setNotPassingCount(Integer count) {
        set(NOT_PASSING_COUNT_KEY, (count != null) ? count : 0);
        }

        @Override
        public Integer getPassingCount() {
                return get(PASSING_COUNT_KEY);
        }

        @Override
        public void setPassingCount(Integer count) {
        set(PASSING_COUNT_KEY, (count != null) ? count : 0);
        }

        public Boolean getHasLastQuiz() {
                return get(HAS_LAST_QUIZ_KEY);
        }

        public void setHasLastQuiz(Boolean val) {
                set(HAS_LAST_QUIZ_KEY, val);
        }

        public Boolean getHasLastLogin() {
                return get(HAS_LAST_LOGIN_KEY);
        }

        public void setHasLastLogin(Boolean val) {
                set(HAS_LAST_LOGIN_KEY, val);
        }

        public Boolean getHasTutoringUse() {
                return get(HAS_TUTORING_USE_KEY);
        }

        public void setHasTutoringUse(Boolean val) {
                set(HAS_TUTORING_USE_KEY, val);
        }

        public Boolean getHasPassingCount() {
                return get(HAS_PASSING_COUNT_KEY);
        }

        public void setHasPassingCount(Boolean val) {
                set(HAS_PASSING_COUNT_KEY, val);
        }

        public boolean getHasExtendedData() {
                return hasExtendedData;
        }

        public void setHasExtendedData(boolean val) {
                this.hasExtendedData = val;
        }

        public StudentProgramModel getProgram() {
            return get(PROGRAM_KEY);
        }

        public void setProgram(StudentProgramModel studyProgram) {
            set(PROGRAM_KEY, studyProgram);
        }

    @Override
    public StudentSettingsModel getSettings() {
        StudentSettingsModel settings = get(SETTINGS_KEY);
        if(settings == null) {
            setSettings(new StudentSettingsModel());
        }
        return get(SETTINGS_KEY);
    }


        @Override
        public void setSettings(StudentSettingsModel optionSettings) {
                set(SETTINGS_KEY, optionSettings);
        }
}
