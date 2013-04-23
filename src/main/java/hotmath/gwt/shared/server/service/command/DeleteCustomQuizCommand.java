package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.DeleteCustomQuizAction;

import java.sql.Connection;

public class DeleteCustomQuizCommand implements ActionHandler<DeleteCustomQuizAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return DeleteCustomQuizAction.class;
    }

    @Override
    public RpcData execute(Connection conn, DeleteCustomQuizAction action) throws Exception {
        CmQuizzesDao.getInstance().deleteCustomQuiz(conn, action.getAdminId(), action.getQuizId());
        return new RpcData("status=OK");
    }

}
