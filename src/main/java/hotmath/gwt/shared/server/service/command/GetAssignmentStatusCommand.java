package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.AssignmentStatus;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStatusAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentStatusCommand implements ActionHandler<GetAssignmentStatusAction, AssignmentStatus>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentStatusAction.class;
    }

    @Override
    public AssignmentStatus execute(Connection conn, GetAssignmentStatusAction action) throws Exception {
        return AssignmentDao.getInstance().getAssignmentStatus(action.getAid(), action.getAssKey());
    }

}
