package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentSolutionContextAction;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveAssignmentSolutionContextCommand implements ActionHandler<SaveAssignmentSolutionContextAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAssignmentSolutionContextAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SaveAssignmentSolutionContextAction action) throws Exception {
        AssignmentDao.getInstance().saveAssignmentSolutionContext(action.getUid(), action.getAssignKey(), action.getPid(),action.getVariablesJson());
        return new RpcData("status=OK");
    }
    

}
