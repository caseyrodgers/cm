package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.CreateCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class CreateCustomProblemCommand implements ActionHandler<CreateCustomProblemAction, SolutionInfo> {
    @Override
    public SolutionInfo execute(Connection conn, CreateCustomProblemAction action) throws Exception {
        return CustomProblemDao.getInstance().createNewCustomProblem(action.getProblem());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateCustomProblemAction.class;
    }
}
