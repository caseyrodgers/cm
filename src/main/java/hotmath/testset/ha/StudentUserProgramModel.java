package hotmath.testset.ha;



import java.util.Date;
import java.util.List;

/** Identifies a complete Catchup Math program
 * which defines all aspects of a problem.  
 * 
 * Includes the test_def and the custom configuration info.
 * 
 * @author casey
 *
 */
public class StudentUserProgramModel {
    
    Integer id;
    Integer userId;
    Integer testDefId;
    Integer passPercent;
    String testName;
    Integer adminId;
    Date createDate;
    HaTestConfig config;
    HaTestDef testDef;
    int customProgramId;
    String customProgramName;
    
    
    public String getCustomProgramName() {
        return customProgramName;
    }
    public void setCustomProgramName(String customProgramName) {
        this.customProgramName = customProgramName;
    }
    public int getCustomProgramId() {
        return customProgramId;
    }
    public void setCustomProgramId(Integer customProgramId) {
        this.customProgramId = customProgramId;
    }

    List<HaTest> tests;
    
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
    
    public Integer getPassPercent() {
    	return passPercent;
    }
    
    public void setPassPercent(Integer passPercent) {
    	this.passPercent = passPercent;
    }
    
    public List<HaTest> getTests() {
        return tests;
    }
    public void setTests(List<HaTest> tests) {
        this.tests = tests;
    }

    public HaTestDef getTestDef() {
		return testDef;
	}
	public void setTestDef(HaTestDef testDef) {
		this.testDef = testDef;
	}

    
    @Override
    public String toString() {
        return "StudentUserProgramModel [adminId=" + adminId + ", config=" + config + ", createDate=" + createDate
                + ", customProgramId=" + customProgramId + ", id=" + id + ", passPercent=" + passPercent + ", testDef="
                + testDef + ", testDefId=" + testDefId + ", testName=" + testName + ", tests=" + tests + ", userId="
                + userId + "]";
    }
    
}
