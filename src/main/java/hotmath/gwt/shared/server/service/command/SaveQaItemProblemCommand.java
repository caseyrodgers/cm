package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemProblemAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveQaItemProblemCommand implements ActionHandler<SaveQaItemProblemAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveQaItemProblemAction action) throws Exception {
        boolean saved = new CmQaDao().saveQaItemProblem(conn, action.getUserName(), action.getItem(), action.getProblem());
        return new RpcData("status=" + (saved?"OK":"Not Saved"));
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveQaItemProblemAction.class;
    }    
}
