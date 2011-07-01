package hotmath.gwt.cm_qa.server.rpc;

import hotmath.gwt.cm_qa.client.rpc.UpdateQaItemAction;
import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class UpdateQaItemCommand implements ActionHandler<UpdateQaItemAction, RpcData>{

	@Override
	public RpcData execute(Connection conn, UpdateQaItemAction action) throws Exception {
		new CmQaDao().updateQaItem(conn, action.getItem(), action.getDescription());
		return new RpcData("status=OK");
	}
	

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return UpdateQaItemAction.class;
	}	
}
