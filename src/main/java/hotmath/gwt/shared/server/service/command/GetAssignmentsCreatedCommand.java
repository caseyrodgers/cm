package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentsCreatedCommand implements ActionHandler<GetAssignmentsCreatedAction, CmList<Assignment>>{

    @Override
    public CmList<Assignment> execute(Connection conn, GetAssignmentsCreatedAction action) throws Exception {
        
        CmList<Assignment> asses = new CmArrayList<Assignment>();
        asses.addAll(AssignmentDao.getInstance().getAssignments(action.getAid(), action.getGroupId()));
        return asses;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentsCreatedAction.class;
    }
}
