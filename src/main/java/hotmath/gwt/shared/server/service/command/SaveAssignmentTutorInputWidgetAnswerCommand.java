package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.lwl.CmTutoringDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;

import java.sql.Connection;

import org.apache.log4j.Logger;

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