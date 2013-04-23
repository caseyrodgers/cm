package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.StepUnitPair;

import java.util.List;

public class SaveSolutionStepsAdminAction implements Action<RpcData>{
    
    /** the pid to save it to */
    String pid;
    
    /** the pid original loaded */
    String originalPid; 
    String statement;
    String statementFigure;
    String md5OnRead;
    boolean overrideDirty;
    List<StepUnitPair> steps;
    String tutorDefine;
    boolean isActive;
    
    public SaveSolutionStepsAdminAction(){}
    
    public SaveSolutionStepsAdminAction(String md5OnRead, String pid, String statement,String statementFigure, List<StepUnitPair> steps, String tutorDefine,boolean isActive) {
        this.md5OnRead = md5OnRead;
        this.statement=statement;
        this.statementFigure = statementFigure;
        this.pid = pid;
        this.steps = steps;
        this.tutorDefine = tutorDefine;
        this.isActive = isActive;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getOriginalPid() {
        return originalPid;
    }

    public void setFromPid(String originalPid) {
        this.originalPid = originalPid;
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

    public String getTutorDefine() {
        return tutorDefine;
    }

    public void setTutorDefine(String tutorDefine) {
        this.tutorDefine = tutorDefine;
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
