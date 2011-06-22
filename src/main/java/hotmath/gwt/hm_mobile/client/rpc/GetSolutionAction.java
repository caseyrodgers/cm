package hotmath.gwt.hm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

public class GetSolutionAction implements Action<SolutionResponse> {
    
    String pid;

    public GetSolutionAction() {}
    
    public GetSolutionAction(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "GetSolutionAction [pid=" + pid + "]";
    }

}