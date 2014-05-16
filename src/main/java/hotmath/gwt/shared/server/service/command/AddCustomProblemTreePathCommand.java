package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_rpc.client.rpc.AddCustomProblemTreePathAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

public class AddCustomProblemTreePathCommand implements ActionHandler<AddCustomProblemTreePathAction, RpcData> {

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return AddCustomProblemTreePathAction.class;
	}

	@Override
	public RpcData execute(Connection conn,	AddCustomProblemTreePathAction action) throws Exception {
		CustomProblemDao.getInstance().addTreePath(action.getTeacher(), action.getTreePath());
		return new RpcData("status=OK");
	}

}
