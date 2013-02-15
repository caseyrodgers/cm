package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CopyAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class CopyAssignmentCommand implements ActionHandler<CopyAssignmentAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CopyAssignmentAction.class;
    }

    @Override
    public RpcData execute(Connection conn, CopyAssignmentAction action) throws Exception {
        String newName = AssignmentDao.getInstance().copyAssignment(action.getAid(), action.getAssKey());
        return new RpcData("new_name=" + newName);
    }

}
