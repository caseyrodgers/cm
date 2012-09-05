package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

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
