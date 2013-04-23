package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.CopyAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class CopyAssignmentCommand implements ActionHandler<CopyAssignmentAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CopyAssignmentAction.class;
    }

    @Override
    public RpcData execute(Connection conn, CopyAssignmentAction action) throws Exception {
        String newName = AssignmentDao.getInstance().copyAssignment(action.getAssKey());
        return new RpcData("new_name=" + newName);
    }

}
