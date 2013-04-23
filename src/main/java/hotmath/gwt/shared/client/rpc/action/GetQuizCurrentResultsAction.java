package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class GetQuizCurrentResultsAction implements Action<CmList<RpcData>> {
    
    int testId;
    public GetQuizCurrentResultsAction() {}
    
    public GetQuizCurrentResultsAction(int testId) {
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
        return "GetQuizCurrentResultsAction [testId=" + testId + "]";
    }
}
