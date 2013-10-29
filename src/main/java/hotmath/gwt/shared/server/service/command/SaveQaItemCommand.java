package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveQaItemCommand implements ActionHandler<SaveQaItemAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveQaItemAction action) throws Exception {
        boolean saved = new CmQaDao().saveQaItem(conn, action.getUserName(), action.getItem(), action.isVerified(), action.isProblem());
        return new RpcData("status=" + (saved?"OK":"Not Saved"));
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveQaItemAction.class;
    }    
}
