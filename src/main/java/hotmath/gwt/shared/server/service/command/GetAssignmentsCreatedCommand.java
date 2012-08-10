package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentsCreatedCommand implements ActionHandler<GetAssignmentsCreatedAction, CmList<Assignment>>{

    @Override
    public CmList<Assignment> execute(Connection conn, GetAssignmentsCreatedAction action) throws Exception {
        
        CmList<Assignment> asses = new CmArrayList<Assignment>();
        asses.addAll(AssignmentDao.getInstance().getAssignments(action.getAid()));
        return asses;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentsCreatedAction.class;
    }
}
