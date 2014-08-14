package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;

import com.google.gwt.user.client.rpc.IsSerializable;

class StudentModelBasic implements StudentModelI, IsSerializable{

    Integer adminUid;
    Integer userProgramId;
    String backgroundStyle;
    String email;
    String group;
    int groupId;
    Integer uid;
    String name;
    String passcode;
    Integer notPassingCount;
    Integer passingCount;
    Integer tutoringUse;
    Integer sectionNum=0;

    String progId;
    String subjId;
    String chapter;
    String json;
    Boolean isDemoUser;

    StudentProgramModel program = new StudentProgramModel();
    
    String passPercent;

    StudentSettingsModel settings = new StudentSettingsModel();
    
    boolean hasExtendedData;
    boolean selfPay;
    boolean isCollege;
    
    private String showWorkState;

    @Override
    public void setShowWorkState(String swState) {
        this.showWorkState = swState;
    }

    @Override
    public String getShowWorkState() {
        return this.showWorkState;
    }
    
    public Integer getTutoringUse() {
        return tutoringUse;
    }


    public void setTutoringUse(Integer tutoringUse) {
        this.tutoringUse = tutoringUse;
    }

    @Override
    public boolean getHasExtendedData() {
        return this.hasExtendedData;
    }

    @Override
    public void setHasExtendedData(boolean extended) {
        this.hasExtendedData = extended;
    }
    
    @Override
    public Integer getUid() {
        // TODO Auto-generated method stub
        return uid;
    }

    
    @Override
    public Integer getAdminUid() {
        return adminUid; 
    
    }

    @Override
    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    @Override
    public String getChapter() {
        return this.chapter;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public Integer getGroupId() {
        return this.groupId;
    }

    @Override
    public String getJson() {
        return json;
    }

    @Override
    public String getLastLogin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLastQuiz() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPassPercent() {
        return this.passPercent;
    }

    @Override
    public String getPasscode() {
        return this.passcode;
    }

    @Override
    public Boolean getProgramChanged() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getSectionCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getSectionNum() {
        // TODO Auto-generated method stub
    	return sectionNum;
    }

    @Override
    public String getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getTotalUsage() {
        // TODO Auto-generated method stub
        return null;
    }

	public Boolean getIsDemoUser() {
		return this.isDemoUser;
	}

    @Override
    public void setAdminUid(Integer adminUid) {
        this.adminUid = adminUid;
    }

    @Override
    public void setBackgroundStyle(String style) {
        this.backgroundStyle = style;
    }

    @Override
    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    @Override
    public void setEmail(String emailAddr) {
        this.email = emailAddr;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public void setLastLogin(String lastLogin) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLastQuiz(String lastQuiz) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPassPercent(String passPercent) {
        this.passPercent = passPercent;
    }

    @Override
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    @Override
    public void setProgramChanged(Boolean changed) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSectionCount(Integer sectionCount) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSectionNum(Integer sectionNum) {
    	this.sectionNum = sectionNum;
    }

    @Override
    public void setStatus(String status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTotalUsage(Integer totalUsage) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUid(Integer uid) {
        this.uid = uid;    
    }

    @Override
	public void setIsDemoUser(Boolean isDemo) {
		this.isDemoUser = isDemo;
	}

	@Override
	public Integer getNotPassingCount() {
		return notPassingCount;
	}

	@Override
	public Integer getPassingCount() {
		return passingCount;
	}

	@Override
	public void setNotPassingCount(Integer count) {
		this.notPassingCount = count;
	}

	@Override
	public void setPassingCount(Integer count) {
		this.passingCount = count;
	}


    @Override
    public StudentProgramModel getProgram() {
        return program;
    }


    @Override
    public void setProgram(StudentProgramModel studyProgram) {
        program = studyProgram;
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
