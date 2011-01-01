package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

public class GetSolutionResourcesAdminAction implements Action<CmList<SolutionResource>> {
    String pid;
    ResourceType type;
    
    public GetSolutionResourcesAdminAction(){}
    public GetSolutionResourcesAdminAction(String pid,ResourceType type) {
        this.pid = pid;
        this.type = type;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    
    
    public ResourceType getType() {
        return type;
    }
    public void setType(ResourceType type) {
        this.type = type;
    }


    static public enum ResourceType{LOCAL,GLOBAL,WIDGET};
}
