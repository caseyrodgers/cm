package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

/** Save a single whiteboard command into database
 * 
 *  Return RpcData with single var (status == OK)
 *  
 * @author casey
 *
 */
public class SaveAssignmentTutorInputWidgetAnswerCommand implements ActionHandler<SaveAssignmentTutorInputWidgetAnswerAction, RpcData> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAssignmentTutorInputWidgetAnswerAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SaveAssignmentTutorInputWidgetAnswerAction action) throws Exception {
        AssignmentDao.getInstance().saveTutorInputWidgetAnswer(action.getUid(),action.getAssignKey(), action.getPid(), action.getValue(), action.isCorrect());
        return new RpcData("status=OK");
    }
    
}