package hotmath.gwt.cm_tools.client.model;


public class StudentModel implements StudentModelI {

    String group;
    String groupId;
    Integer sectionNum;
    Integer userProgramId;
    String progId;
    String subjId;
    String chapter;
    String lastQuiz;
    String lastLogin;
    String json;
    String status;
    Integer uid;
    String email;
    Integer adminUid;
    String showWork;
    String showWorkState;
    Integer totalUsage;
    Integer passingCount;
	Integer notPassingCount;
    Boolean tutoringAvail;
    Integer tutoringUse;

    String tutoringState;
    String name;
    String passcode;
    String backgroundStyle;
    Boolean isDemoUser;
    String passPercent;
    Boolean programChanged;
    String progDescr;
    Boolean showWorkRequired;


    public StudentModel() {
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getSectionNum() {
        return sectionNum;
    }

    public void setSectionNum(Integer sectionNum) {
        this.sectionNum = sectionNum;
    }

    public Integer getUserProgramId() {
        return userProgramId;
    }

    public void setUserProgramId(Integer userProgramId) {
        this.userProgramId = userProgramId;
    }

    public String getProgId() {
        return progId;
    }

    public void setProgId(String progId) {
        this.progId = progId;
    }

    public String getSubjId() {
        return subjId;
    }

    public void setSubjId(String subjId) {
        this.subjId = subjId;
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

    public String getShowWorkState() {
        return showWorkState;
    }

    public void setShowWorkState(String showWorkState) {
        this.showWorkState = showWorkState;
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

    public Boolean getTutoringAvail() {
        return tutoringAvail;
    }

    public void setTutoringAvail(Boolean tutoringAvail) {
        this.tutoringAvail = tutoringAvail;
    }

    public String getTutoringState() {
        return tutoringState;
    }

    public void setTutoringState(String tutoringState) {
        this.tutoringState = tutoringState;
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
    public String getProgramDescr() {
        return this.progDescr;
    }

    @Override
    public Boolean getShowWorkRequired() {
        return showWorkRequired;
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
    public void setProgramDescr(String progDescr) {
        this.progDescr = progDescr;
    }

    @Override
    public void setShowWorkRequired(Boolean val) {
        showWorkRequired = val;
    }
}
