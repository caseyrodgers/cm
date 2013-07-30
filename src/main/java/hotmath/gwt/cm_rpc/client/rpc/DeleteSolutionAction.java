package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class DeleteSolutionAction implements Action<RpcData>{
    
    private String pid;

    public DeleteSolutionAction(){}
    
    public DeleteSolutionAction(String pid) {
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
        return "DeleteSolutionAction [pid=" + pid + "]";
    }
}
