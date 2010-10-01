package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;

public class CreateTestRunAction implements Action<CreateTestRunResponse> {

    int testId;
    
    int userId;

    public CreateTestRunAction() {}
    
    public CreateTestRunAction(int testId, int userId) {
    	this.testId = testId;
    	this.userId = userId;
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
