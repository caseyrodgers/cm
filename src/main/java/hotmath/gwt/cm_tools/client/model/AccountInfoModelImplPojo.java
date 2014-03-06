package hotmath.gwt.cm_tools.client.model;

import java.util.Date;

public class AccountInfoModelImplPojo implements AccountInfoModel {
    
	private static final long serialVersionUID = 1816274210633870397L;

	String schoolName;
    boolean isFreeAccount;
    boolean isCollege;
    String schoolUserName;
    String adminUserName;
    String passcode;
    String lastLogin;
    private String expirationDate;
    private Integer maxStudents;
    private Integer totalStudents;
    private Integer countFreeStudents;
    private int countCommunityStudents;
    private String hasTutoring;
    private Integer tutoringMinutes;
    private String subscriberId;
    private String studentCountStyle;
    private Date accountCreateDate;
    private String accountRepEmail;
    

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
	
	public void setIsCollege(boolean isCollege) {
	    this.isCollege = isCollege;
	}
	
	public boolean getIsCollege() {
	    return isCollege;
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

	public void setCountCommunityStudents(int count) {
	    this.countCommunityStudents = count;
	}
	
	public int getCountCommunityStudents() {
	    return countComunityStudents;
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

    @Override
    public String getIsFreeMessage() {
        return isFreeAccount?"Free Account for Essentials":"";
    }

	@Override
	public void setAccountRepEmail(String email) {
		this.accountRepEmail = email;
	}

	@Override
	public String getAccountRepEmail() {
		return accountRepEmail;
	}

    @Override
    public String toString() {
        return "AccountInfoModelImplPojo [schoolName=" + schoolName + ", isFreeAccount=" + isFreeAccount + ", schoolUserName=" + schoolUserName
                + ", adminUserName=" + adminUserName + ", passcode=" + passcode + ", lastLogin=" + lastLogin + ", expirationDate=" + expirationDate
                + ", maxStudents=" + maxStudents + ", totalStudents=" + totalStudents + ", countFreeStudents=" + countFreeStudents + ", hasTutoring="
                + hasTutoring + ", tutoringMinutes=" + tutoringMinutes + ", subscriberId=" + subscriberId + ", studentCountStyle=" + studentCountStyle
                + ", accountCreateDate=" + accountCreateDate + ", accountRepEmail=" + accountRepEmail + "]";
    }
}
