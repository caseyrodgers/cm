package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetSolutionPidsAction implements Action<CmList<String>> {
    
    private String pids;
    public GetSolutionPidsAction(){}
    public GetSolutionPidsAction(String pids) {
        this.pids = pids;
    }
    public String getPids() {
        return pids;
    }
    public void setPids(String pids) {
        this.pids = pids;
    }
}
