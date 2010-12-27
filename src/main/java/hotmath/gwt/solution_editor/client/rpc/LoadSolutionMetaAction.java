package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;

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
