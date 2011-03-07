package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;

public class SaveCustomQuizCommand implements ActionHandler<SaveCustomQuizAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveCustomQuizAction action) throws Exception {
        new CmQuizzesDao().saveCustomQuiz(conn, action.getAdminId(), action.getCpName(), action.getIds());
        RpcData result = new RpcData("status=OK");
        return result;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveCustomQuizAction.class;
    }
}
