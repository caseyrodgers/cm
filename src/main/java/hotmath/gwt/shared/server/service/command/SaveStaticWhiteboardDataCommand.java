package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveStaticWhiteboardDataAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.WhiteboardDao;

import java.sql.Connection;

public class SaveStaticWhiteboardDataCommand implements ActionHandler<SaveStaticWhiteboardDataAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveAssignmentWhiteboardDataAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SaveStaticWhiteboardDataAction action) throws Exception {
        WhiteboardDao.getInstance().saveStaticWhiteboardData(action.getAdminId(), action.getPid(), action.getCommandType(), action.getData());
        return new RpcData("status=OK");
    }

}
