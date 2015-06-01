package hotmath.cm.util;



public class PilotCreatedInfo {

    private String userName;
    private String passWord;
    private boolean isCollege;
    private String salesRep;
    private String schoolName;
    private String expireDate;
    private Integer adminId;

    public PilotCreatedInfo(Integer adminId, String userName, String passWord, boolean isCollege, String salesRep, String schoolName, String expireDate) {
        this.adminId = adminId;
        this.userName = userName;
        this.passWord = passWord;
        this.isCollege = isCollege;
        this.salesRep = salesRep;
        this.schoolName = schoolName;
        this.expireDate = expireDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public boolean isCollege() {
        return isCollege;
    }

    public String getSalesRep() {
        return salesRep;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public Integer getAdminId() {
        return adminId;
    }
    
}
