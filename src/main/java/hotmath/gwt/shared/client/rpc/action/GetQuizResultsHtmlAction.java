package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class GetQuizResultsHtmlAction implements Action<RpcData>{
    
    int runId;
    
    public GetQuizResultsHtmlAction(){}
    
    public GetQuizResultsHtmlAction(int runId) {
        this.runId = runId; 
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

}
