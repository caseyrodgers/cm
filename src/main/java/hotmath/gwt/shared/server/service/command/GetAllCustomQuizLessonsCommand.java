package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAllCustomQuizLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;

import java.sql.Connection;


public class GetAllCustomQuizLessonsCommand implements ActionHandler<GetAllCustomQuizLessonsAction, CmList<CustomLessonModel>>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAllCustomQuizLessonsAction.class;
    }

    @Override
    public CmList<CustomLessonModel> execute(Connection conn, GetAllCustomQuizLessonsAction action) throws Exception {
        return CmQuizzesDao.getInstance().getAllCustomQuizLessons(conn);
    }
    
}
