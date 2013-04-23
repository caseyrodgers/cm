package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentsForUserCommand implements ActionHandler<GetAssignmentsForUserAction, CmList<StudentAssignmentInfo>>{

    @Override
    public CmList<StudentAssignmentInfo> execute(Connection conn, GetAssignmentsForUserAction action) throws Exception {
        CmList<StudentAssignmentInfo> cmList = new CmArrayList<StudentAssignmentInfo>();
        cmList.addAll(AssignmentDao.getInstance().getAssignmentsForUser(action.getUid()));
        return cmList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentsForUserAction.class;
    }
}
