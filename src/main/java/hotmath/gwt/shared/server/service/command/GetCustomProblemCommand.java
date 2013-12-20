package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class GetCustomProblemCommand implements ActionHandler<GetCustomProblemAction, CmList<CustomProblemModel>> {

    @Override
    public CmList<CustomProblemModel> execute(Connection conn, GetCustomProblemAction action) throws Exception {
        
        CmList<CustomProblemModel> problems = new CmArrayList<CustomProblemModel>();
        problems.addAll(CustomProblemDao.getInstance().getCustomProblemsFor(action.getTeacher()));
        return problems;
    }


    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCustomProblemAction.class;
    }

}
