package hotmath.gwt.cm_tools.client.model;

import java.util.Date;

public class StudentUserProgramModel {
    
    Integer id;
    Integer userId;
    Integer testDefId;
    String testName;
    Integer passPercent;
    Integer adminId;
    Date createDate;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId() {
        return userId;
    }
    public String getTestName() {
        return testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getTestDefId() {
        return testDefId;
    }
    public void setTestDefId(Integer testDefId) {
        this.testDefId = testDefId;
    }
    public Integer getPassPercent() {
        return passPercent;
    }
    public void setPassPercent(Integer passPercent) {
        this.passPercent = passPercent;
    }
    public Integer getAdminId() {
        return adminId;
    }
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    @Override
    public String toString() {
        return "StudentUserProgramModel [adminId=" + adminId + ", createDate=" + createDate + ", id=" + id
                + ", passPercent=" + passPercent + ", testDefId=" + testDefId + ", testName=" + testName + ", userId="
                + userId + "]";
    }
    
}
