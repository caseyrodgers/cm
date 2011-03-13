package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.action.GetCustomQuizAction;

public class GetCustomQuizCommand implements ActionHandler<GetCustomQuizAction, CmList<QuizQuestion>>{

    @Override
    public CmList<QuizQuestion> execute(Connection conn, GetCustomQuizAction action) throws Exception {
        return new CmQuizzesDao().getCustomQuizQuestions(conn, action.getAdminId());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCustomQuizAction.class;
    }
}
