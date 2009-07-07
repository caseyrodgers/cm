package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class CreateTestRunAction implements Action<RpcData>{
    
    int testId;
    
    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public CreateTestRunAction(int testId) {
        this.testId = testId;
    }

}
