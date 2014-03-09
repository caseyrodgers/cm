package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveCustomProblemAction implements Action<RpcData> {
    
    public enum SaveType{WIDGET, HINTSTEP, PROBLEM_STATEMENT_TEXT}

    private String pid;
    private SaveType type;
    private String data;
    private SolutionMeta solutionMeta;;
    public SaveCustomProblemAction() {}
    
    public SaveCustomProblemAction(String pid, SaveType type, String data) {
        this.pid = pid;
        this.type = type;
        this.data = data;
    }

    public SaveCustomProblemAction(String pid, SaveType saveType, SolutionMeta solutionMeta) {
        this(pid, saveType, (String)null);
        this.solutionMeta = solutionMeta;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public SaveType getType() {
        return type;
    }

    public void setType(SaveType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public SolutionMeta getSolutionMeta() {
        return solutionMeta;
    }

    public void setSolutionMeta(SolutionMeta solutionMeta) {
        this.solutionMeta = solutionMeta;
    }

}
