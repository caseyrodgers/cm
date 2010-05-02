package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.AddUserAction;

import java.sql.Connection;

public class AddUserCommand implements ActionHandler<AddUserAction, StudentModelI>{

	@Override
	public StudentModelI execute(Connection conn, AddUserAction action) throws Exception {
		return null;
	}

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return AddUserAction.class;
	}

}
