package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetAssignmentGradeBookCommand implements ActionHandler<GetAssignmentGradeBookAction, CmList<StudentDto>> {


    @Override
    public CmList<StudentDto> execute(Connection conn, GetAssignmentGradeBookAction action) throws Exception {
        List<StudentDto> students = AssignmentDao.getInstance().getAssignmentGradeBook(action.getAssignKey());
        CmList<StudentDto> cmStudents = new CmArrayList<StudentDto>();
        cmStudents.addAll(students);
        return cmStudents;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentGradeBookAction.class;
    }


}
