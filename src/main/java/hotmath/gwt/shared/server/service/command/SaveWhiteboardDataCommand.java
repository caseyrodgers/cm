package hotmath.gwt.shared.server.service.command;

import hotmath.cm.lwl.CmTutoringDao;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Save a single whiteboard command into database
 * 
 * Return RpcData with single var (status == OK)
 * 
 * @author casey
 * 
 */
public class SaveWhiteboardDataCommand implements
		ActionHandler<SaveWhiteboardDataAction, RpcData> {

	static Logger logger = Logger.getLogger(SaveWhiteboardDataCommand.class);

	@Override
	public RpcData execute(Connection conn, SaveWhiteboardDataAction action)
			throws Exception {
		RpcData rData = new RpcData();

		switch (action.getCommandType()) {
		case CLEAR:
			rData = new ClearWhiteboardDataCommand().execute(
					conn,
					new ClearWhiteboardDataAction(action.getUid(), action
							.getRid(), action.getPid()));
			break;

		case DELETE:
			CmTutoringDao.deleteWhiteboardData(conn, action.getUid(),
					action.getRid(), action.getPid(), action.getIndex());
			break;
			
		case UPDATE:
			CmTutoringDao.updateWhiteboardData(conn, action.getUid(),
					action.getRid(), action.getPid(), action.getIndex(), action.getCommandData());
			break;
			

		default:
			CmTutoringDao.saveWhiteboardData(conn, action.getUid(),
					action.getRid(), action.getPid(), action.getCommandData(),
					action.getCommandType());
			break;
		}
		return rData;
	}

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return SaveWhiteboardDataAction.class;
	}
}
