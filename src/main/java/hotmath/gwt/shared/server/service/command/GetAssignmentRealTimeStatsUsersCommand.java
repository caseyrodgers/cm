package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStatsUsers;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentRealTimeStatsUsersAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentRealTimeStatsUsersCommand implements ActionHandler<GetAssignmentRealTimeStatsUsersAction, AssignmentRealTimeStatsUsers>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentRealTimeStatsUsersAction.class;
    }

    @Override
    public AssignmentRealTimeStatsUsers execute(Connection conn, GetAssignmentRealTimeStatsUsersAction action) throws Exception {
        return AssignmentDao.getInstance().getAssignmentProblemStatsUsers(action.getAssignKey(), action.getPid());
    }

}
