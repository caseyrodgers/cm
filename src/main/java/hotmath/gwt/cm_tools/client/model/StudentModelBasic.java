package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StudentModelBasic implements StudentModelI, IsSerializable{

    Integer adminUid;
    Integer userProgramId;
    String backgroundStyle;
    String email;
    String group;
    String groupId;
    Integer uid;
    String name;
    String passcode;
    Integer notPassingCount;
    Integer passingCount;
    Integer tutoringUse;

    String progId;
    String subjId;
    String chapter;
    String json;
    Boolean isDemoUser;

    StudentProgramModel program = new StudentProgramModel();
    
    String passPercent;

    StudentSettingsModel settings = new StudentSettingsModel();
    
    public Integer getTutoringUse() {
        return tutoringUse;
    }


    public void setTutoringUse(Integer tutoringUse) {
        this.tutoringUse = tutoringUse;
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
    public String getGroupId() {
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
        return null;
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
    public void setGroupId(String groupId) {
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
        // TODO Auto-generated method stub
        
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
}
