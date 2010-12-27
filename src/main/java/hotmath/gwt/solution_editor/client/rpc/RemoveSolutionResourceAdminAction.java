package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;

public class RemoveSolutionResourceAdminAction implements Action<RpcData>{
    String pid;
    String file;
    ResourceType type;
    
    public RemoveSolutionResourceAdminAction() {}
    
    public RemoveSolutionResourceAdminAction(String pid, String file) {
        this.type = ResourceType.LOCAL;
        this.pid = pid;
        this.file = file;
    }

    public RemoveSolutionResourceAdminAction(String file) {
        this.type = ResourceType.GLOBAL;
        this.file = file;        
    }
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }
}
