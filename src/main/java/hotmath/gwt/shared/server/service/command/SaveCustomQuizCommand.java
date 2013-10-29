package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;

import java.sql.Connection;

public class SaveCustomQuizCommand implements ActionHandler<SaveCustomQuizAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveCustomQuizAction action) throws Exception {
        int quizId = CmQuizzesDao.getInstance().saveCustomQuiz(action.getCustomQuiz(), action.getIds());
        RpcData result = new RpcData("status=OK,custom_quiz_id=" + quizId);
        return result;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveCustomQuizAction.class;
    }
}
