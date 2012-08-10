package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveAssignmentCommand implements ActionHandler<SaveAssignmentAction,RpcData>{
    
    @Override
    public RpcData execute(Connection conn, SaveAssignmentAction action) throws Exception {
        int assKey = AssignmentDao.getInstance().saveAssignement(action.getAid(), action.getAssignment());
        return new RpcData("key=" + assKey);
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAssignmentAction.class;
    }
}
