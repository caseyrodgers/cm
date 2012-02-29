package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.DeleteCustomQuizAction;

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
