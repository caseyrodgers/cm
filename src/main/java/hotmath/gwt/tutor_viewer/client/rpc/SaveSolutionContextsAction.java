package hotmath.gwt.tutor_viewer.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

import java.util.List;

public class SaveSolutionContextsAction implements Action<RpcData> {
    
    CmList<String> contexts = new CmArrayList<String>();
    String pid;
    
    public SaveSolutionContextsAction(){}
    
    public SaveSolutionContextsAction(String pid, List<String> contextsIn) {
        this.pid = pid;
        this.contexts.addAll(contextsIn);    
    }

    public CmList<String> getContexts() {
        return contexts;
    }

    public void setContexts(CmList<String> contexts) {
        this.contexts = contexts;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
