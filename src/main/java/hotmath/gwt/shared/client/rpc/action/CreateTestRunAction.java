package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;

public class CreateTestRunAction implements Action<CreateTestRunResponse> {

    int testId;

    public CreateTestRunAction() {}
    
    public CreateTestRunAction(int testId) {
        this.testId = testId;
    }    
    
    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    
    @Override
    public String toString() {
        return "CreateTestRunAction [testId=" + testId + "]";
    }
}
