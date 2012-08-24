package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentsForUserCommand implements ActionHandler<GetAssignmentsForUserAction, CmList<Assignment>>{

    @Override
    public CmList<Assignment> execute(Connection conn, GetAssignmentsForUserAction action) throws Exception {
        CmList<Assignment> cmList = new CmArrayList<Assignment>();
        cmList.addAll(AssignmentDao.getInstance().getAssignmentsForUser(action.getUid()));
        return cmList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentsForUserAction.class;
    }
}
