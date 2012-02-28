package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ArchiveCustomQuizAction;

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
