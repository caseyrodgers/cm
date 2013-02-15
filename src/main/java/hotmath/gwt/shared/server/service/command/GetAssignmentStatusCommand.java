package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.AssignmentStatus;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStatusAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

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
