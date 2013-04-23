package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ArchiveCustomQuizAction;

import java.sql.Connection;

public class ArchiveCustomQuizCommand implements ActionHandler<ArchiveCustomQuizAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ArchiveCustomQuizAction.class;
    }

    @Override
    public RpcData execute(Connection conn, ArchiveCustomQuizAction action) throws Exception {
        CmQuizzesDao.getInstance().archiveCustomQuiz(action.getQuizId());
        return new RpcData("status=OK");
    }

}
