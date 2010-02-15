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
    Boolean showWorkRequired;
    Boolean tutorAvailable;
    Integer notPassingCount;
    Integer passingCount;
    Integer tutoringUse;

    String progId;
    String subjId;
    String chapter;
    String json;
    String passPercent;
    Boolean isDemoUser;

    @Override
    public String toString() {
        return "StudentModelBasic [adminUid=" + adminUid + ", backgroundStyle=" + backgroundStyle + ", chapter="
                + chapter + ", email=" + email + ", group=" + group + ", groupId=" + groupId + ", json=" + json
                + ", name=" + name + ", passcode=" + passcode + ", progId=" + progId + ", showWorkRequired="
                + showWorkRequired + ", subjId=" + subjId + ", tutorAvailable=" + tutorAvailable + ", uid=" + uid
                + ", userProgramId=" + userProgramId + ", isDemoUser=" + isDemoUser + "]";
    }

    
    public Boolean getTutorAvailable() {
        return tutorAvailable;
    }


    public void setTutorAvailable(Boolean tutorAvailable) {
        this.tutorAvailable = tutorAvailable;
    }


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
    public String getProgId() {
        return this.progId;
    }

    @Override
    public Boolean getProgramChanged() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProgramDescr() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getSectionNum() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean getShowWorkRequired() {
        return this.showWorkRequired;
    }

    @Override
    public String getShowWorkState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSubjId() {
        return this.subjId;
    }

    @Override
    public Integer getTotalUsage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean getTutoringAvail() {
        return this.tutorAvailable;
    }

    @Override
    public String getTutoringState() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Integer getUserProgramId() {
        return this.userProgramId;
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
    public void setProgId(String progId) {
        this.progId = progId;
    }

    @Override
    public void setProgramChanged(Boolean changed) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProgramDescr(String progDescr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSectionNum(Integer sectionNum) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShowWorkRequired(Boolean val) {
        this.showWorkRequired = val;
    }

    @Override
    public void setShowWorkState(String swState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setStatus(String status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSubjId(String subjId) {
        this.subjId = subjId;
    }

    @Override
    public void setTotalUsage(Integer totalUsage) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTutoringAvail(Boolean val) {
        this.tutorAvailable = val;
        
    }

    @Override
    public void setTutoringState(String tutoringState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUid(Integer uid) {
        this.uid = uid;    
    }

    @Override
    public void setUserProgramId(Integer userProgId) {
        this.userProgramId = userProgId;
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

}
