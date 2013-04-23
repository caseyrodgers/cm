package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentCommand implements ActionHandler<GetAssignmentAction, Assignment> {


    @Override
    public Assignment execute(Connection conn, GetAssignmentAction action) throws Exception {
        return AssignmentDao.getInstance().getAssignment(action.getAssignKey());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentAction.class;
    }

}
