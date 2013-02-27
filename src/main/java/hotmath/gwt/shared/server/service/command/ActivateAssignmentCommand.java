package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActivateAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class ActivateAssignmentCommand implements ActionHandler<ActivateAssignmentAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, ActivateAssignmentAction action) throws Exception {
        AssignmentDao.getInstance().activateAssignment(action.getAssignmentKey());
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ActivateAssignmentAction.class;
    }
}
