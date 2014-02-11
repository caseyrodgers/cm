package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class GetCustomProblemLinkedLessonCommand implements ActionHandler<GetCustomProblemLinkedLessonAction,CmList<LessonModel>>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCustomProblemLinkedLessonAction.class;
    }

    @Override
    public CmList<LessonModel> execute(Connection conn, GetCustomProblemLinkedLessonAction action) throws Exception {
        return new CmArrayList<LessonModel>( CustomProblemDao.getInstance().getCustomProblemLinkedLessons(action.getPid()));
    }
}

