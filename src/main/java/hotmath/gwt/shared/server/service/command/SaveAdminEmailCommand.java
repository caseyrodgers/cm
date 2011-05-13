package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.SaveAdminEmailAction;

public class SaveAdminEmailCommand implements ActionHandler<SaveAdminEmailAction, StringHolder> {
	
	public SaveAdminEmailCommand() {}
	
	@Override
	public StringHolder execute(Connection conn, SaveAdminEmailAction action)throws Exception {
	    CmAdminDao.getInstance().setAdminPassword(conn, action.getAdminId(), action.getEmail());
		return new StringHolder("OK");
	}
	
	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return SaveAdminEmailAction.class;
	}
}
