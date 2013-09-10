package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStats;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentRealTimeStatsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentRealTimeStatsCommand implements ActionHandler<GetAssignmentRealTimeStatsAction, AssignmentRealTimeStats>{
    @Override
    public AssignmentRealTimeStats execute(Connection conn, GetAssignmentRealTimeStatsAction action) throws Exception {
        return AssignmentDao.getInstance().getAssignmentStats(action.getAssignKey());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentRealTimeStatsAction.class;
    }
}
