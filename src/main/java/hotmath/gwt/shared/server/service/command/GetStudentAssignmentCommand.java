package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetStudentAssignmentCommand implements ActionHandler<GetStudentAssignmentAction, StudentAssignment> {

    @Override
    public StudentAssignment execute(Connection conn, GetStudentAssignmentAction action) throws Exception {
        return AssignmentDao.getInstance().getStudentAssignment(action.getUid(), action.getAssignKey(), true);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentAssignmentAction.class;
    }
}
