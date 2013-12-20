package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class CreateCustomProblemAction implements Action<SolutionInfo>{
    
    private CustomProblemModel problem;

    public CreateCustomProblemAction(){}

    public CreateCustomProblemAction(CustomProblemModel problem) {
        this.problem = problem;
    }

    public CustomProblemModel getProblem() {
        return problem;
    }

    public void setProblem(CustomProblemModel problem) {
        this.problem = problem;
    }

}
