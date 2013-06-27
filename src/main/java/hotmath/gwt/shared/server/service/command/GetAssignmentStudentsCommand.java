package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStudentsAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetAssignmentStudentsCommand implements ActionHandler<GetAssignmentStudentsAction, CmList<StudentDto>>{

    @Override
    public CmList<StudentDto> execute(Connection conn, GetAssignmentStudentsAction action) throws Exception {
        List<StudentDto> list = AssignmentDao.getInstance().getStudentsInAssignment(action.getAssignKey(), action.getType());
        CmList<StudentDto> clist = new CmArrayList<StudentDto>();
        clist.addAll(list);
        return clist;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentStudentsAction.class;
    }
    
}
