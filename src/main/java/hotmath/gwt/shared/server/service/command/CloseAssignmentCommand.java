package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CloseAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class CloseAssignmentCommand implements ActionHandler<CloseAssignmentAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, CloseAssignmentAction action) throws Exception {
        AssignmentDao.getInstance().closeAssignment(action.getUid(), action.getAssignKey());
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CloseAssignmentAction.class;
    }
}
