package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class LoadSolutionMetaAction implements Action<SolutionMeta>{
    String pid;
    public LoadSolutionMetaAction() {}
    
    public LoadSolutionMetaAction(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
}
