package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

/** Save custom problem linked lessons
 * 
 * TODO: remove comment to separate action?
 * @author casey
 *
 */
public class SaveCustomProblemLinkedLessonCommand implements ActionHandler<SaveCustomProblemLinkedLessonAction,RpcData>{


    @Override
    public RpcData execute(Connection conn, SaveCustomProblemLinkedLessonAction action) throws Exception {
        
        CustomProblemDao.getInstance().setCustomProblemLinkedLessons(action.getAdminId(), action.getPid(), action.getLessons());
        CustomProblemDao.getInstance().setCustomProblemComment(action.getPid(), action.getComments());
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveCustomProblemLinkedLessonAction.class;
    }

}

