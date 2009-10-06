package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.shared.client.rpc.Response;


/** Represents the user's current Test Run status
 * 
 * @author casey
 *
 */
public class CreateTestRunResponse implements Response {
    
    String action;
    Integer runId;
    Integer correct;
    Integer total;
    Boolean passed;

    String assignedTest;
    Integer testSegment;
    Integer testCorrectPercent;
    
    Integer sessionCount;
    String sessionName;


    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public CreateTestRunResponse() {}
    
    public CreateTestRunResponse(String action, Integer runId, Integer correct, Integer total, Boolean passed) {
        this.action = action;
        this.runId = runId;
        this.correct = correct;
        this.total = total;
        this.passed = passed;
    }
    
    
    public Integer getTestCorrectPercent() {
        return testCorrectPercent;
    }

    public void setTestCorrectPercent(Integer testPercent) {
        this.testCorrectPercent = testPercent;
    }

    public Integer getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(Integer testSegment) {
        this.testSegment = testSegment;
    }


    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Integer getRunId() {
        return runId;
    }
    public void setRunId(Integer runId) {
        this.runId = runId;
    }
    public Integer getCorrect() {
        return correct;
    }

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getAssignedTest() {
        return assignedTest;
    }
    public void setAssignedTest(String assignedTest) {
        this.assignedTest = assignedTest;
    }
    
    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }


    @Override
    public String toString() {
        return "TestRunUserStatus [action=" + action + ", assignedTest=" + assignedTest + ", correct=" + correct
                + ", passed=" + passed + ", runId=" + runId + ", sessionCount=" + sessionCount + ", sessionName="
                + sessionName + ", testCorrectPercent=" + testCorrectPercent + ", testSegment=" + testSegment
                + ", total=" + total + "]";
    }
}
