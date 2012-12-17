package hotmath.gwt.cm_tools.client.model;

import java.util.Date;

public class AccountInfoModelImplPojo implements AccountInfoModel {
    
    String schoolName;
    boolean isFreeAccount;
    String schoolUserName;
    String adminUserName;
    String passcode;
    String lastLogin;
    private String expirationDate;
    private Integer maxStudents;
    private Integer totalStudents;
    private Integer countFreeStudents;
    private String hasTutoring;
    private Integer tutoringMinutes;
    private String subscriberId;
    private String studentCountStyle;
    private Date accountCreateDate;
    

    public AccountInfoModelImplPojo() {}
	
	public String getSchoolName() {
		return schoolName;
	}

	public void setIsFreeAccount(Boolean yesNo) {
	    this.isFreeAccount = yesNo;
	}
	
	public Boolean getIsFreeAccount() {
	    return isFreeAccount;
	}
	
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchoolUserName() {
		return schoolUserName;
	}
	
	public void setSchoolUserName(String schoolUserName) {
		this.schoolUserName = schoolUserName;
	}
	
	public String getAdminUserName() {
		return adminUserName;
	}
	
	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}
	
	public String getPasscode() {
		return passcode; 
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public Integer getMaxStudents() {
		return maxStudents;
	}

	public void setMaxStudents(Integer maxStudents) {
		this.maxStudents = maxStudents;
	}
	
	public Integer getTotalStudents() {
		return totalStudents;
	}

	public void setTotalStudents(Integer totalStudents) {
		this.totalStudents = totalStudents;
	}
	
	public void setCountFreeStudents(Integer cnt) {
	    this.countFreeStudents = cnt;
	}
	
	public Integer getCountFreeStudents() {
	    return countFreeStudents;
	}

	public String getHasTutoring() {
		return hasTutoring;
	}

	public void setHasTutoring(String hasTutoring) {
		this.hasTutoring = hasTutoring;
	}
	
	public void setTutoringMinutes(Integer min) {
	    this.tutoringMinutes = min;
	}
	
	public Integer getTutoringMinutes() {
	    return this.tutoringMinutes;
	}

	public boolean getIsTutoringEnabled() {
	    return false;
	}
	
	public void setSubscriberId(String id) {
	    subscriberId = id;
	}
	
	public String getSubscriberId() {
	    return subscriberId;
	}
	
	public void setStudentCountStyle(String style) {
	    this.studentCountStyle = style;
	}
	
	public String getStudentCountStyle() {
	    return studentCountStyle;
	}
	
	public Date getAccountCreateDate() {
	    return accountCreateDate;
	}
	
	public void setAccountCreateDate(Date date) {
	    accountCreateDate = date;
	}
}
