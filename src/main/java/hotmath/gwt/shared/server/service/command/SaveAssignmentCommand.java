package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveAssignmentCommand implements ActionHandler<SaveAssignmentAction,RpcData>{
    
    @Override
    public RpcData execute(Connection conn, SaveAssignmentAction action) throws Exception {
        int assKey = AssignmentDao.getInstance().saveAssignment(action.getAssignment());
        return new RpcData("key=" + assKey);
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAssignmentAction.class;
    }
}
