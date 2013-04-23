package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentGradeBookCommand implements ActionHandler<GetAssignmentGradeBookAction, CmList<StudentAssignment>> {


    @Override
    public CmList<StudentAssignment> execute(Connection conn, GetAssignmentGradeBookAction action) throws Exception {
        CmList<StudentAssignment> students = AssignmentDao.getInstance().getAssignmentGradeBook(action.getAssignKey());
        return students;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentGradeBookAction.class;
    }


}
