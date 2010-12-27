package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.StepUnitPair;

import java.util.List;

public class SaveSolutionStepsAdminAction implements Action<RpcData>{
    String pid;
    String statement;
    List<StepUnitPair> steps;
    
    public SaveSolutionStepsAdminAction(){}
    public SaveSolutionStepsAdminAction(String pid, String statement, List<StepUnitPair> steps) {
        this.statement=statement;
        this.pid = pid;
        this.steps = steps;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getStatement() {
        return statement;
    }
    public void setStatement(String statement) {
        this.statement = statement;
    }
    public List<StepUnitPair> getSteps() {
        return steps;
    }
    public void setSteps(CmList<StepUnitPair> steps) {
        this.steps = steps;
    }
}
