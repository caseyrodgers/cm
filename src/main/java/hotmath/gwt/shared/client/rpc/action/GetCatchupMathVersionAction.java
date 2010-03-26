package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;

public class GetCatchupMathVersionAction implements Action<CatchupMathVersion> {
    int currentTestId;
    
    public GetCatchupMathVersionAction() {}
    
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
