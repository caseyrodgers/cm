package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWorkForUserAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentWorkForUserCommand implements ActionHandler<GetAssignmentWorkForUserAction, CmList<StudentAssignment>>{

    @Override
    public CmList<StudentAssignment> execute(Connection conn, GetAssignmentWorkForUserAction action) throws Exception {
        CmList<StudentAssignment> cmList = new CmArrayList<StudentAssignment>();
        cmList.addAll(AssignmentDao.getInstance().getAssignmentWorkForStudent(action.getUid(), action.getFromDate(), action.getToDate()));
        return cmList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentWorkForUserAction.class;
    }
}
