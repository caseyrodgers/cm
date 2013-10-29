package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.action.GetCustomQuizAction;

import java.sql.Connection;

public class GetCustomQuizCommand implements ActionHandler<GetCustomQuizAction, CmList<QuizQuestion>>{

    @Override
    public CmList<QuizQuestion> execute(Connection conn, GetCustomQuizAction action) throws Exception {
        return CmQuizzesDao.getInstance().getCustomQuizQuestions(conn, action.getCustomQuizId());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCustomQuizAction.class;
    }
}
