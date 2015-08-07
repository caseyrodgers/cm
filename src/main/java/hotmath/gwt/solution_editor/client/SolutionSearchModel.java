package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.BaseModel;

public class SolutionSearchModel implements Response {

    private String pid;
    private String label;
    
    private boolean isActive;

    public SolutionSearchModel() {
        /** empty */
    }

    public SolutionSearchModel(String pid, boolean isActive) {
        this.pid = pid;
        this.isActive = isActive;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getLabel() {
        return getPid() + (label!=null?(" " + label):"");
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
