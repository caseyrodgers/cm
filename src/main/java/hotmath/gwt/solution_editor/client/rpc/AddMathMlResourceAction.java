package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;


public class AddMathMlResourceAction implements Action<RpcData>{

    String pid;
    String mathMl;
    String resourceName;
    ResourceType type;

    public AddMathMlResourceAction() {}

    public AddMathMlResourceAction(String pid, String resourceName, String mathMl) {
        this.type = ResourceType.LOCAL;
        this.pid = pid;
        this.resourceName = resourceName;
        this.mathMl = mathMl;
    }
    
    public AddMathMlResourceAction(String resourceName, String mathMl) {
        this.type = ResourceType.GLOBAL;
        this.resourceName = resourceName;
        this.mathMl = mathMl;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMathMl() {
        return mathMl;
    }

    public void setMathMl(String mathMl) {
        this.mathMl = mathMl;
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
