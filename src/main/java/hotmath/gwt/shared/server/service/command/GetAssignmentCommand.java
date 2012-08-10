package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

public class GetAssignmentCommand implements ActionHandler<GetAssignmentAction, Assignment> {


    @Override
    public Assignment execute(Connection conn, GetAssignmentAction action) throws Exception {
        return AssignmentDao.getInstance().getAssignment(action.getAssignKey());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentAction.class;
    }

}
