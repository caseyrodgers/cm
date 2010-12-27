package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class SolutionMeta implements Response{
    String pid;
    int numSteps;
    String problemStatement;
    
    List<SolutionMetaStep> steps = new ArrayList<SolutionMetaStep>();
    
    public SolutionMeta() {}
    
    public SolutionMeta(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }

    public List<SolutionMetaStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SolutionMetaStep> steps) {
        this.steps = steps;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }
}
