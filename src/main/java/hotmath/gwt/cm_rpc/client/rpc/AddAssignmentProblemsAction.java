package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;

import java.util.List;

public class AddAssignmentProblemsAction implements Action<RpcData>{
    
    private List<ProblemDto> problemsToAdd;
    private int assignId;

    public AddAssignmentProblemsAction(){}
    
    public AddAssignmentProblemsAction(int assignId, List<ProblemDto> problemsToAdd) {
        this.assignId = assignId;
        this.problemsToAdd = problemsToAdd;
    }

    public List<ProblemDto> getProblemsToAdd() {
        return problemsToAdd;
    }

    public void setProblemsToAdd(List<ProblemDto> problemsToAdd) {
        this.problemsToAdd = problemsToAdd;
    }

    public int getAssignId() {
        return assignId;
    }

    public void setAssignId(int assignId) {
        this.assignId = assignId;
    }
}
