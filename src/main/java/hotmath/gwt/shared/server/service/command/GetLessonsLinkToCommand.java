package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.LessonLinkedModel;
import hotmath.gwt.cm_rpc.client.rpc.GetLessonsLinkToAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class GetLessonsLinkToCommand implements ActionHandler<GetLessonsLinkToAction, CmList<LessonLinkedModel>> {


    @Override
    public CmList<LessonLinkedModel> execute(Connection conn, GetLessonsLinkToAction action) throws Exception {
        return new CmArrayList<LessonLinkedModel>(CustomProblemDao.getInstance().getLessonsLinkedToCustomProblems(action.getAdminId()));
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetLessonsLinkToAction.class;
    }

}
