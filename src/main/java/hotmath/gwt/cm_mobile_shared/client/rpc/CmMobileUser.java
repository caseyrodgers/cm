package hotmath.gwt.cm_mobile_shared.client.rpc;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CmMobileUser implements Response {
    
    String name;
    int userId;
    int testId;
    int testSegment;
    int testSlot;
    int runId;
    String securityKey;
    CmList<Topic> prescribedLessons;
    
    /** composite with normal CM login */
    UserLoginResponse baseLoginResponse;

    /** composite with next flow action
     * 
     */
    CmProgramFlowAction flowAction;
    
    
    AssignmentUserInfo assignmentInfo;
    private boolean passedTest;
    
    public CmMobileUser() {}
    
    public CmMobileUser(int uid, int testId, int testSegment, int testSlot,  boolean passedTest, int runId, AssignmentUserInfo assignmentInfo) {
        this.userId = uid;
        this.testId = testId;
        this.testSegment = testSegment;
        this.testSlot = testSlot;
        this.passedTest = passedTest;
        this.assignmentInfo = assignmentInfo;
    }
    
    public boolean isPassedTest() {
        return passedTest;
    }

    public void setPassedTest(boolean passedTest) {
        this.passedTest = passedTest;
    }

    public UserInfo getUserInfo() {
        return baseLoginResponse.getUserInfo();
    }
    public AssignmentUserInfo getAssignmentInfo() {
        return assignmentInfo;
    }

    public void setAssignmentInfo(AssignmentUserInfo assignmentInfo) {
        this.assignmentInfo = assignmentInfo;
    }

    public CmProgramFlowAction getFlowAction() {
        return flowAction;
    }

    public void setFlowAction(CmProgramFlowAction flowAction) {
        this.flowAction = flowAction;
    }

    public UserLoginResponse getBaseLoginResponse() {
        return baseLoginResponse;
    }

    public void setBaseLoginResponse(UserLoginResponse baseLoginResponse) {
        this.baseLoginResponse = baseLoginResponse;
    }

    public CmList<Topic> getPrescribedLessons() {
        return prescribedLessons;
    }

    public void setPrescribedLessons(CmList<Topic> prescribedLessons) {
        this.prescribedLessons = prescribedLessons;
    }

    PrescriptionData prescripion;


    public PrescriptionData getPrescripion() {
        return prescripion;
    }

    public void setPrescripion(PrescriptionData prescripion) {
        this.prescripion = prescripion;
    }


    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(int testSegment) {
        this.testSegment = testSegment;
    }

    public int getTestSlot() {
        return testSlot;
    }

    public void setTestSlot(int testSlot) {
        this.testSlot = testSlot;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @Override
    public String toString() {
        return "CmMobileUser [name=" + name + ", userId=" + userId + ", testId=" + testId + ", testSegment="
                + testSegment + ", testSlot=" + testSlot + ", runId=" + runId + ", securityKey=" + securityKey
                + ", prescribedLessons=" + prescribedLessons + ", baseLoginResponse=" + baseLoginResponse
                + ", flowAction=" + flowAction + ", prescripion=" + prescripion + "]";
    }
    
}
