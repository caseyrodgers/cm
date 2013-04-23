package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.AssignStudentsToAssignmentAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class AssignStudentsToAssignmentCommand implements ActionHandler<AssignStudentsToAssignmentAction, AssignmentInfo>{

    @Override
    public AssignmentInfo execute(Connection conn, AssignStudentsToAssignmentAction action) throws Exception {
        AssignmentInfo info = AssignmentDao.getInstance().assignStudents(action.getAssignKey(), action.getStudents());
        return info;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AssignStudentsToAssignmentAction.class;
    }
}
