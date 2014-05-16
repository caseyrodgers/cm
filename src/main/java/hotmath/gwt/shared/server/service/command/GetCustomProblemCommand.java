package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.CustomProblemInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class GetCustomProblemCommand implements ActionHandler<GetCustomProblemAction, CustomProblemInfo> {

    @Override
    public CustomProblemInfo execute(Connection conn, GetCustomProblemAction action) throws Exception {
        
    	return new CustomProblemInfo(
    			CustomProblemDao.getInstance().getCustomProblemsFor(action.getTeacher()),
    			CustomProblemDao.getInstance().getCustomTreePaths(action.getTeacher().getAdminId()));
    }


    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCustomProblemAction.class;
    }

}
