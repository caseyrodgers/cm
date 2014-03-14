package hotmath.gwt.cm_tools.client.model;


public class StudentModel implements StudentModelI {

    String group;
    int groupId;
    Integer sectionCount;
    Integer sectionNum=0;
    String chapter;
    String lastQuiz;
    String lastLogin;
    String json;
    String status;
    Integer uid;
    String email;
    Integer adminUid;
    Integer totalUsage;
    Integer passingCount;
	Integer notPassingCount;
    Integer tutoringUse;

    String name;
    String passcode;
    String backgroundStyle;
    Boolean isDemoUser;
    String passPercent;
    Boolean programChanged;
    
    boolean hasExtendedData;
    boolean selfPay;
    boolean isCollege;
    
    StudentProgramModel program = new StudentProgramModel();

    StudentSettingsModel settings = new StudentSettingsModel();
    private String showWorkState;

    @Override
    public void setShowWorkState(String swState) {
        this.showWorkState = swState;
    }

    @Override
    public String getShowWorkState() {
        return this.showWorkState;
    }

    public StudentModel() {
    }

    @Override
    public boolean getHasExtendedData() {
        return this.hasExtendedData;
    }
    
    @Override
    public void setHasExtendedData(boolean extended) {
        this.hasExtendedData = extended;
    }
    
    public Integer getTutoringUse() {
        return tutoringUse;
    }


    public void setTutoringUse(Integer tutoringUse) {
        this.tutoringUse = tutoringUse;
    }
    
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    public Integer getSectionNum() {
        return sectionNum;
    }

    public void setSectionNum(Integer sectionNum) {
        this.sectionNum = sectionNum;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getLastQuiz() {
        return lastQuiz;
    }

    public void setLastQuiz(String lastQuiz) {
        this.lastQuiz = lastQuiz;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(Integer adminUid) {
        this.adminUid = adminUid;
    }

    public Integer getTotalUsage() {
        return totalUsage;
    }

    public void setTotalUsage(Integer totalUsage) {
        this.totalUsage = totalUsage;
    }

    public Integer getPassingCount() {
		return passingCount;
	}

	public void setPassingCount(Integer passingCount) {
		this.passingCount = passingCount;
	}

	public Integer getNotPassingCount() {
		return notPassingCount;
	}

	public void setNotPassingCount(Integer notPassingCount) {
		this.notPassingCount = notPassingCount;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    @Override
    public Boolean getIsDemoUser() {
        return isDemoUser;
    }

    @Override
    public String getPassPercent() {
        return passPercent;
    }

    @Override
    public Boolean getProgramChanged() {
        return programChanged;
    }

    @Override
    public void setIsDemoUser(Boolean isDemo) {
        this.isDemoUser = isDemo;
    }

    @Override
    public void setPassPercent(String passPercent) {
        this.passPercent = passPercent;
    }

    @Override
    public void setProgramChanged(Boolean changed) {
        programChanged = changed;
    }

    @Override
    public StudentProgramModel getProgram() {
        return program;
    }

    @Override
    public void setProgram(StudentProgramModel program) {
        this.program = program;
    }

	@Override
	public StudentSettingsModel getSettings() {
		return settings;
	}

	@Override
	public void setSettings(StudentSettingsModel settings) {
		this.settings = settings;
	}

	@Override
	public void setSelfPay(boolean selfPay) {
		this.selfPay = selfPay;
	}

	@Override
	public boolean getSelfPay() {
		return selfPay;
	}

	@Override
	public void setIsCollege(boolean isCollege) {
		this.isCollege = isCollege;
	}

	@Override
	public boolean getIsCollege() {
		return isCollege;
	}
}
