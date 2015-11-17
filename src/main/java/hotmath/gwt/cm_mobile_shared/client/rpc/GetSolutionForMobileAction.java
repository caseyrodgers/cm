package hotmath.gwt.cm_mobile_shared.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.HmMobileActionBase;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetSolutionForMobileAction extends HmMobileActionBase implements Action<SolutionResponse> {
    
    String pid;

    public GetSolutionForMobileAction() {}
    
    public GetSolutionForMobileAction(String pid) {
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