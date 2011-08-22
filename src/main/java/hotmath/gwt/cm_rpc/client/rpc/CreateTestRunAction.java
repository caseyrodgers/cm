package hotmath.gwt.cm_rpc.client.rpc;


public class CreateTestRunAction implements Action<CreateTestRunResponse> {

    int testId;
    
    int userId;

    public CreateTestRunAction() {}
    
    public CreateTestRunAction(int testId, int userId) {
    	this.testId = testId;
    	this.userId = userId;
    }
    
    /** Leave default with just testId, or change all calls
     * 
     * @param testId
     */
    public CreateTestRunAction(int testId) {
        this.testId = testId;
    }
    
    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }
    
    public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
    public String toString() {
        return "CreateTestRunAction [testId=" + testId + "]";
    }
}
