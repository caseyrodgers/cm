package hotmath.gwt.cm_tools.client.model;


import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code>StudentModelBase</code> represents the Base student information,
 * that is, information that can be quickly obtained from the DB for a student.
 *
 * @author bob
 *
 */

public class StudentModelBase implements IsSerializable, StudentModelI {

 	private static final long serialVersionUID = 3692580752279795982L;
	
	Integer uid;
    Integer adminUid;

    String  name;
    String  passcode;
    String  email;
    Boolean isTutorAvailable;
	String  backgroundStyle;
    Boolean isShowWorkRequired;
	Boolean isDemoUser;
	String  groupId;
	String  group;
	Integer userProgramId;
	
	String chapter;
	String passPercent;
	String progId;
	String programDescr;
	String subjId;
	String testConfigJson;

    GroupInfoModelI groupModel;

	public StudentModelBase() {
    }

	@Override
    public Boolean getIsDemoUser() {
		return isDemoUser;
	}

	@Override
	public void setIsDemoUser(Boolean isDemoUser) {
		this.isDemoUser = isDemoUser;
	}

	public GroupInfoModelI getGroupModel() {
		return groupModel;
	}

	public void setGroupModel(GroupInfoModelI groupModel) {
		this.groupModel = groupModel;
	}

	public Boolean getIsShowWorkRequired() {
		return isShowWorkRequired;
	}

	public void setIsShowWorkRequired(Boolean isShowWorkReqd) {
		this.isShowWorkRequired = isShowWorkReqd;
	}

    public Boolean getIsTutorAvailable() {
		return isTutorAvailable;
	}

	public void setIsTutorAvailable(Boolean isTutorAvailable) {
		this.isTutorAvailable = isTutorAvailable;
	}

	@Override
    public Integer getUserProgramId() {
        return userProgramId;
    }

	@Override
    public void setUserProgramId(Integer userProgramId) {
        this.userProgramId = userProgramId;
    }

	@Override
    public Integer getUid() {
        return uid;
    }

	@Override
    public void setUid(Integer uid) {
        this.uid = uid;
    }

	@Override
    public String getEmail() {
        return email;
    }

	@Override
    public void setEmail(String email) {
        this.email = email;
    }

	@Override
    public Integer getAdminUid() {
        return adminUid;
    }

	@Override
    public void setAdminUid(Integer adminUid) {
        this.adminUid = adminUid;
    }

	@Override
    public String getName() {
        return name;
    }

	@Override
    public void setName(String name) {
        this.name = name;
    }

	@Override
    public String getPasscode() {
        return passcode;
    }

	@Override
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

	@Override
    public String getBackgroundStyle() {
        return backgroundStyle;
    }

	@Override
    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String getChapter() {
		return chapter;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public String getJson() {
		return testConfigJson;
	}

	@Override
	public String getLastLogin() {
		// not implemented
		return null;
	}

	@Override
	public String getLastQuiz() {
		// not implemented
		return null;
	}

	@Override
	public Integer getNotPassingCount() {
		// not implemented
		return null;
	}

	@Override
	public String getPassPercent() {
		return passPercent;
	}

	@Override
	public Integer getPassingCount() {
		// not implemented
		return null;
	}

	@Override
	public String getProgId() {
		return progId;
	}

	@Override
	public Boolean getProgramChanged() {
		// not implemented
		return null;
	}

	@Override
	public String getProgramDescr() {
		return programDescr;
	}

	@Override
	public Integer getSectionNum() {
		// not implemented
		return null;
	}

	@Override
	public Boolean getShowWorkRequired() {
		return isShowWorkRequired;
	}

	@Override
	public String getShowWorkState() {
		// not implemented
		return null;
	}

	@Override
	public String getStatus() {
		// not implemented here
		return null;
	}

	@Override
	public String getSubjId() {
		return subjId;
	}

	@Override
	public Integer getTotalUsage() {
		// not implemented
		return null;
	}

	@Override
	public Boolean getTutoringAvail() {
		return isTutorAvailable;
	}

	@Override
	public String getTutoringState() {
		// not implemented
		return null;
	}

	@Override
	public Integer getTutoringUse() {
		// not implemented
		return null;
	}

	@Override
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	@Override
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public void setJson(String json) {
		testConfigJson = json;
	}

	@Override
	public void setLastLogin(String lastLogin) {
		// not implemented
	}

	@Override
	public void setLastQuiz(String lastQuiz) {
		// not implemented
	}

	@Override
	public void setNotPassingCount(Integer count) {
		// not implemented
	}

	@Override
	public void setPassPercent(String passPercent) {
		this.passPercent = passPercent;
	}

	@Override
	public void setPassingCount(Integer count) {
		// not implemented here
	}

	@Override
	public void setProgId(String progId) {
		this.progId = progId;
	}

	@Override
	public void setProgramChanged(Boolean changed) {
		// not implemented
	}

	@Override
	public void setProgramDescr(String progDescr) {
		programDescr = progDescr;
	}

	@Override
	public void setSectionNum(Integer sectionNum) {
		// not implemented
	}

	@Override
	public void setShowWorkRequired(Boolean val) {
		isShowWorkRequired = val;
	}

	@Override
	public void setShowWorkState(String swState) {
		// not implemented
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
		// not implemented
	}

	@Override
	public void setTutoringAvail(Boolean val) {
		this.isTutorAvailable = val;
	}

	@Override
	public void setTutoringState(String tutoringState) {
		// not implemented
	}

	@Override
	public void setTutoringUse(Integer x) {
		// not implemented
	}

}
