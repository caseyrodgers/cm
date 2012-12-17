package hotmath.gwt.cm_tools.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AccountInfoModelImplGxt2 extends BaseModelData  implements AccountInfoModel {

	private static final long serialVersionUID = 5273566482319153369L;

	public static final String SCHOOL_NAME_KEY = "school-name";
	public static final String SCHOOL_USER_NAME_KEY = "school-user-name";
	public static final String EXPIRATION_DATE_KEY = "expiration-date";
	public static final String HAS_TUTORING_KEY = "has-tutoring";
	public static final String TUTORING_MINUTES_KEY = "tutoring-minutes";
	public static final String ADMIN_USER_NAME_KEY = "admin-user-name";
	public static final String ADMIN_SUBSCRIBER_ID = "subscriber-id";
	public static final String STUDENT_COUNT_STYLE_KEY = "student-count-style";
	public static final String TUTORING_MINUTES_LABEL = "tutoring-minutes-label";
	public static final String ACCOUNT_CREATE_DATE = "account_create_date";
	public static final String IS_FREE_ACCOUNT = "is-free-account";
	

    public AccountInfoModelImplGxt2() {
    }
	
	public String getSchoolName() {
		return get(SCHOOL_NAME_KEY);
	}

	public void setIsFreeAccount(Boolean yesNo) {
	    set(IS_FREE_ACCOUNT,yesNo);
	}
	
	public Boolean getIsFreeAccount() {
	    return get(IS_FREE_ACCOUNT);
	}
	
	public void setSchoolName(String schoolName) {
		set(SCHOOL_NAME_KEY, schoolName);
	}

	public String getSchoolUserName() {
		return get(SCHOOL_USER_NAME_KEY);
	}
	
	public void setSchoolUserName(String schoolUserName) {
		set(SCHOOL_USER_NAME_KEY, schoolUserName);
	}
	
	public String getAdminUserName() {
		return get(ADMIN_USER_NAME_KEY);
	}
	
	public void setAdminUserName(String adminUserName) {
		set(ADMIN_USER_NAME_KEY, adminUserName);
	}
	
	public String getPasscode() {
		return get("passcode");
	}

	public void setPasscode(String passcode) {
		set("passcode", passcode);
	}

	public String getLastLogin() {
		return get("last-login");
	}

	public void setLastLogin(String lastLogin) {
		set("last-login", lastLogin);
	}

	public String getExpirationDate() {
		return get(EXPIRATION_DATE_KEY);
	}
	
	public void setExpirationDate(String expirationDate) {
		set (EXPIRATION_DATE_KEY, expirationDate);
	}
	
	public Integer getMaxStudents() {
		return get("max-students");
	}

	public void setMaxStudents(Integer maxStudents) {
		set("max-students", maxStudents);
	}
	
	public Integer getTotalStudents() {
		return get("total-students");
	}

	public void setTotalStudents(Integer totalStudents) {
		set("total-students", totalStudents);
	}
	
	public void setCountFreeStudents(Integer cnt) {
	    set("free-students", cnt);
	}
	
	public Integer getCountFreeStudents() {
	    return get("free-students");
	}

	public String getHasTutoring() {
		return get(HAS_TUTORING_KEY);
	}

	public void setHasTutoring(String hasTutoring) {
		set (HAS_TUTORING_KEY, hasTutoring);
	}
	
	public void setTutoringMinutes(Integer min) {
	    set(TUTORING_MINUTES_KEY, min);
	}
	
	public Integer getTutoringMinutes() {
	    return get(TUTORING_MINUTES_KEY);
	}
	
	/** helper method to tell if tutoring enabled
	 * 
	 * @return
	 */
	public boolean getIsTutoringEnabled() {
	    String go = getHasTutoring();
	    if(go != null && go.equalsIgnoreCase("enabled"))
	        return true;
	    else
	        return false;
	}
	
	public void setSubscriberId(String id) {
	    set(ADMIN_SUBSCRIBER_ID, id);
	}
	
	public String getSubscriberId() {
	    return get(ADMIN_SUBSCRIBER_ID);
	}
	
	public void setStudentCountStyle(String style) {
	    set(STUDENT_COUNT_STYLE_KEY, style);
	}
	
	public String getStudentCountStyle() {
	    return get(STUDENT_COUNT_STYLE_KEY);
	}
	
	public Date getAccountCreateDate() {
	    return get(ACCOUNT_CREATE_DATE);
	}
	
	public void setAccountCreateDate(Date date) {
	    set(ACCOUNT_CREATE_DATE, date);
	}

    public static AccountInfoModelImplGxt2 create(AccountInfoModel m) {
        AccountInfoModelImplGxt2 ot = new AccountInfoModelImplGxt2();

        ot.setSchoolName(m.getSchoolName());
        ot.setIsFreeAccount(m.getIsFreeAccount());
        ot.setSchoolUserName(m.getSchoolUserName());
        ot.setAdminUserName(m.getAdminUserName());
        ot.setPasscode(m.getPasscode());
        ot.setLastLogin(m.getLastLogin());
        ot.setExpirationDate(m.getExpirationDate());
        ot.setMaxStudents(m.getMaxStudents());
        ot.setTotalStudents(m.getTotalStudents());
        ot.setCountFreeStudents(m.getCountFreeStudents());
        ot.setHasTutoring(m.getHasTutoring());
        ot.setTutoringMinutes(m.getTutoringMinutes());
        ot.setSubscriberId(m.getSubscriberId());
        ot.setStudentCountStyle(m.getStudentCountStyle());
        ot.setAccountCreateDate(m.getAccountCreateDate());
        
        return ot;
    }
}
