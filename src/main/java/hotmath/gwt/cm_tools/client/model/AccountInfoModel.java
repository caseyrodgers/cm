package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.Date;

public interface AccountInfoModel extends Response {

    public String getSchoolName();

    public void setIsFreeAccount(Boolean yesNo);

    public Boolean getIsFreeAccount();

    public void setSchoolName(String schoolName);

    public String getSchoolUserName();

    public void setSchoolUserName(String schoolUserName);

    public String getAdminUserName();

    public void setAdminUserName(String adminUserName);

    public String getPasscode();

    public void setPasscode(String passcode);

    public String getLastLogin();

    public void setLastLogin(String lastLogin);

    public String getExpirationDate();

    public void setExpirationDate(String expirationDate);

    public String getCurrentDate();

    public void setCurrentDate(String currentDate);

    public Integer getMaxStudents();

    public void setMaxStudents(Integer maxStudents);

    public Integer getTotalStudents();

    public void setTotalStudents(Integer totalStudents);

    public void setCountFreeStudents(Integer cnt);

    public Integer getCountFreeStudents();

    public void setCountCommunityStudents(int count);

    public int getCountCommunityStudents();

    public String getHasTutoring();

    public void setHasTutoring(String hasTutoring);

    public void setTutoringMinutes(Integer min);

    public Integer getTutoringMinutes();

    public boolean getIsTutoringEnabled();

    public void setSubscriberId(String id);

    public String getSubscriberId();

    public void setSubscriberPassword(String password);

    public String getSubscriberPassword();

    public void setStudentCountStyle(String style);

    public String getStudentCountStyle();

	public void setExpirationDateStyle(String string);

	public String getExpirationDateStyle();

    public Date getAccountCreateDate();

    public void setAccountCreateDate(Date date);
    
    public String getIsFreeMessage();

    public void setAccountRepEmail(String emaail);

    public String getAccountRepEmail();

    public void setIsCollege(boolean isCollege);

    public boolean getIsCollege();
}
