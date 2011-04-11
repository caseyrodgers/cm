package hotmath.gwt.cm_rpc.client.rpc;

public class ResetStudentActivityAction implements Action<RpcData> {
    
    int testId;
    int runId;
    int userId;
    public ResetStudentActivityAction() {}
    
    public ResetStudentActivityAction(int userId, int testId, int runId) {
        this.userId = userId;
        this.testId = testId;
        this.runId = runId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
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

    @Override
    public String toString() {
        return "ResetStudentActivityAction [testId=" + testId + ", runId=" + runId + ", userId=" + userId + "]";
    }

}
