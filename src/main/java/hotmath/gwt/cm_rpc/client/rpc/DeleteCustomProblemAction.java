package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class DeleteCustomProblemAction implements Action<RpcData>{
    
    private CustomProblemModel problem;

    public DeleteCustomProblemAction() {}
    
    public DeleteCustomProblemAction(CustomProblemModel problem) {
        this.problem = problem;
    }

    public CustomProblemModel getProblem() {
        return problem;
    }

    public void setProblem(CustomProblemModel problem) {
        this.problem = problem;
    }

}
