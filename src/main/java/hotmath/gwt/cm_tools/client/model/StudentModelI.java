package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import hotmath.gwt.shared.client.rpc.Response;


public interface StudentModelI extends Response {

    public String getName();

	public void setName(String name);

	public String getPasscode();

	public void setPasscode(String passcode);
    
	public String getProgramDescr();

	public void setProgramDescr(String progDescr);

	public String getGroup();

	public void setGroup(String group);
	
	public String getGroupId();
	
	public void setGroupId(String groupId);

	public Integer getUserProgramId();
	
	public void setUserProgramId(Integer userProgId);
	
	public void setSectionNum(Integer sectionNum);
	
	public Integer getSectionNum();

	public String getLastLogin();
	
	public void setLastLogin(String lastLogin);

	public void setStatus(String status);

	public String getStatus();

	public void setTotalUsage(Integer totalUsage);

	public Integer getTotalUsage();

	public String getPassPercent();

	public void setPassPercent(String passPercent);

	public void setTutoringState(String tutoringState);
	
	public String getTutoringState();

	public void setShowWorkState(String swState);
	
	public String getShowWorkState();

	public void setUid(Integer uid);
	
	public Integer getUid();
	
	public void setEmail(String emailAddr);
	
	public String getEmail();

	public void setAdminUid(Integer adminUid);
	
	public Integer getAdminUid();
	
	public void setProgramChanged(Boolean changed);

	public Boolean getProgramChanged();

	public String getProgId();
	
	public void setProgId(String progId);

	public String getSubjId();
	
	public void setSubjId(String progId);

	public String getChapter();
	
	public void setChapter(String chapter);

	public String getLastQuiz();
	
	public void setLastQuiz(String lastQuiz);

	public String getJson();
	
	public void setJson(String json);

	public Boolean getShowWorkRequired();
	
	public void setShowWorkRequired(Boolean val);

	public Boolean getTutoringAvail();
	
	public void setTutoringAvail(Boolean val);

	public String getBackgroundStyle();
	
	public void setBackgroundStyle(String style);
	
	public Boolean getIsDemoUser();
	
	public void setIsDemoUser(Boolean isDemo);
}
