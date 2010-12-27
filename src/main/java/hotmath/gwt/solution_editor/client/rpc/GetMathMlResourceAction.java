package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;


public class GetMathMlResourceAction implements Action<MathMlResource>{

    String pid;
    String resourceName;
    ResourceType type;

    public GetMathMlResourceAction() {}

    public GetMathMlResourceAction(String pid, String resourceName) {
        this.type = ResourceType.LOCAL;
        this.pid = pid;
        this.resourceName = resourceName;
    }
    
    public GetMathMlResourceAction(String resourceName) {
        this.type = ResourceType.GLOBAL;        
        this.resourceName = resourceName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }
    
}
