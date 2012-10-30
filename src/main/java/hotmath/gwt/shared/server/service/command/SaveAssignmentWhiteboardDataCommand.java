package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveAssignmentWhiteboardDataCommand implements ActionHandler<SaveAssignmentWhiteboardDataAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAssignmentWhiteboardDataAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SaveAssignmentWhiteboardDataAction action) throws Exception {
        AssignmentDao.getInstance().saveWhiteboardData(action.getUid(),action.getAssignKey(),action.getPid(),action.getCommandType(),action.getCommandData(), action.isAdmin());
        return new RpcData("status=OK");
    }

}
