package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_rpc.client.rpc.DeleteCustomProblemTreePathAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

public class DeleteCustomProblemTreePathCommand implements ActionHandler<DeleteCustomProblemTreePathAction, RpcData>{

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return DeleteCustomProblemTreePathAction.class;
	}

	@Override
	public RpcData execute(Connection conn,DeleteCustomProblemTreePathAction action) throws Exception {
		CustomProblemDao.getInstance().deleteTreePath(action.getTeacher(), action.isTeacherNode()?null:action.getPath(), action.isTeacherNode());
		return new RpcData("status=OK");
	}

}
