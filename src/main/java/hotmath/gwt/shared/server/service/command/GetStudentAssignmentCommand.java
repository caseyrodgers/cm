package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetStudentAssignmentCommand implements ActionHandler<GetStudentAssignmentAction, StudentAssignment> {

    @Override
    public StudentAssignment execute(Connection conn, GetStudentAssignmentAction action) throws Exception {
        return AssignmentDao.getInstance().getStudentAssignment(action.getUid(), action.getAssignKey());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentAssignmentAction.class;
    }
}
