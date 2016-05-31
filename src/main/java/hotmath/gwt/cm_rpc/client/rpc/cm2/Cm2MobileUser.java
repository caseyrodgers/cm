package hotmath.gwt.cm_rpc.client.rpc.cm2;


import hotmath.cm.server.model.CmPurchases;
import hotmath.gwt.cm_core.client.model.Cm2PrescriptionTopic;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class Cm2MobileUser implements Response {
    
    String name;
    int userId;
    int testId;
    int testSegment;
    int testSlot;
    int runId;
    String securityKey;
    CmPlace place;

    /** composite with normal CM login */
    UserLoginResponse baseLoginResponse;

    /** composite with next flow action
     * 
     */
    CmProgramFlowAction _flowAction;
    
    
    AssignmentUserInfo assignmentInfo;
    private boolean passedTest;
    
    public Cm2MobileUser() {}
    
    
    List<Cm2PrescriptionTopic> prescriptionTopics = new ArrayList<Cm2PrescriptionTopic>();
    
    public Cm2MobileUser(int uid, int testId, int testSegment, int testSlot,  boolean passedTest, int runId, AssignmentUserInfo assignmentInfo) {
        this.userId = uid;
        this.testId = testId;
        this.testSegment = testSegment;
        this.testSlot = testSlot;
        this.passedTest = passedTest;
        this.runId = runId;
        this.assignmentInfo = assignmentInfo;
    }
    
    public CmPlace getPlace() {
        return place;
    }

    public void setPlace(CmPlace place) {
        this.place = place;
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

    public UserLoginResponse getBaseLoginResponse() {
        return baseLoginResponse;
    }

    public void setBaseLoginResponse(UserLoginResponse baseLoginResponse) {
        this.baseLoginResponse = baseLoginResponse;
    }

    public List<Cm2PrescriptionTopic> getPrescriptionTopics() {
        return this.prescriptionTopics;
    }
    
    public void setPrescriptionTopics(List<Cm2PrescriptionTopic> topics) {
        this.prescriptionTopics = topics;
    }
    
    PrescriptionData prescripion;
    private QuizCm2HtmlResult quizResponse;
	private CmPurchases purchases;


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
    
    public void setCm2QuizResponse(QuizCm2HtmlResult quizResponse) {
        this.quizResponse = quizResponse;
    }

	public void setPurchases(CmPurchases purchases) {
		this.purchases = purchases;
	}
	
	public CmPurchases getPurchases() {
		return purchases;
	}
}
