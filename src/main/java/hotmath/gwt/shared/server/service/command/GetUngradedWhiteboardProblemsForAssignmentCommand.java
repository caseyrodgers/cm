package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetUngradedWhiteboardProblemsForAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;


/** Get the count of students that have ungraded whiteboard problems
 * 
 *  returned in RpcData 'count'
 * @author casey
 *
 */
public class GetUngradedWhiteboardProblemsForAssignmentCommand implements ActionHandler<GetUngradedWhiteboardProblemsForAssignmentAction, RpcData> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUngradedWhiteboardProblemsForAssignmentAction.class;
    }

    @Override
    public RpcData execute(Connection conn, GetUngradedWhiteboardProblemsForAssignmentAction action) throws Exception {
        int count = AssignmentDao.getInstance().getCountUngradedWhiteboardProblems(action.getAssignKey());
        return new RpcData("count=" + count);
    }
}
