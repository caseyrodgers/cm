package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.GetViewedInmhItemsResult;

public class GetViewedInmhItemsAction  implements Action<GetViewedInmhItemsResult>{

    int runId;
    
    public GetViewedInmhItemsAction(int runId) {
        this.runId = runId;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }
    
}
