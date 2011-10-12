package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.StepUnitPair;

import java.util.List;

public class SaveSolutionStepsAdminAction implements Action<RpcData>{
    String pid;
    String statement;
    String statementFigure;
    String md5OnRead;
    boolean overrideDirty;
    List<StepUnitPair> steps;
    
    public SaveSolutionStepsAdminAction(){}
    
    public SaveSolutionStepsAdminAction(String md5OnRead, String pid, String statement,String statementFigure, List<StepUnitPair> steps) {
        this.md5OnRead = md5OnRead;
        this.statement=statement;
        this.statementFigure = statementFigure;
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
    public String getMd5OnRead() {
        return md5OnRead;
    }
    public void setM5OnRead(String md5OnRead) {
        this.md5OnRead = md5OnRead;
    }

    public boolean isOverrideDirty() {
        return overrideDirty;
    }

    public void setForceWrite(boolean overrideDirty) {
        this.overrideDirty = overrideDirty;
    }

    public String getStatementFigure() {
        return statementFigure;
    }

    public void setStatementFigure(String statementFigure) {
        this.statementFigure = statementFigure;
    }
}
