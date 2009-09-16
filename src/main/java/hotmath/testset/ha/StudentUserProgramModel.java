package hotmath.testset.ha;


import java.util.Date;

/** Identifies a complete Catchup Math program
 * 
 * @author casey
 *
 */
public class StudentUserProgramModel {
    
    Integer id;
    Integer userId;
    Integer testDefId;
    String testName;
    Integer adminId;
    Date createDate;
    HaTestConfig config;
    
    public HaTestConfig getConfig() {
        return config;
    }
    public void setConfig(HaTestConfig config) {
        this.config = config;

    }
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
    
    /** Return true if this program has alternate tests available
     * @return
     */
    public Boolean hasAlternateTests() {
        
        if(testDefId == CmProgram.ALG1_PROF.getDefId()
           || testDefId == CmProgram.PREALG_PROF.getDefId()
           || testDefId == CmProgram.ALG2_PROF.getDefId()
           || testDefId == CmProgram.GEOM_PROF.getDefId()) {
            
            return true;
        }
        else
            return false;
    }
    
    @Override
    public String toString() {
        return "StudentUserProgramModel [adminId=" + adminId + ", config=" + config + ", createDate=" + createDate
                + ", id=" + id + ", testDefId=" + testDefId + ", testName=" + testName + ", userId=" + userId + "]";
    }
    
}
