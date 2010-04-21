package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;

public class GetCatchupMathVersionAction implements Action<CatchupMathVersion> {
    int currentTestId;
    int testOne;
    
    public int getTestOne() {
        return testOne;
    }

    public void setTestOne(int testOne) {
        this.testOne = testOne;
    }

    public GetCatchupMathVersionAction() {}
    
    @Override
    public String toString() {
        return "GetCatchupMathVersionAction [currentTestId=" + currentTestId + ", testOne=" + testOne + "]";
    }

    public GetCatchupMathVersionAction(Integer testId) {
        this.currentTestId = testId;
    }

    public int getCurrentTestId() {
        return currentTestId;
    }

    public void setCurrentTestId(int currentTestId) {
        this.currentTestId = currentTestId;
    }
}
