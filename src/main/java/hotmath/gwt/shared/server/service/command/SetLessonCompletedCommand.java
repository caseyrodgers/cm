package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SetLessonCompletedAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;


/** Set completed status for specified Lesson
 * 
 * @author casey
 *
 */
public class SetLessonCompletedCommand implements ActionHandler<SetLessonCompletedAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SetLessonCompletedAction action) throws Exception {
        
        HaTestRunDao.getInstance().setLessonCompleted(conn, action.getRunId(), action.getSession());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SetLessonCompletedAction.class;
    }

}
