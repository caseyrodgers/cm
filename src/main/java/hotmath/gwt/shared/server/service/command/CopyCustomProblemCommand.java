package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_rpc.client.rpc.CopyCustomProblemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

public class CopyCustomProblemCommand implements ActionHandler<CopyCustomProblemAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, CopyCustomProblemAction action) throws Exception {
        String newPid = CustomProblemDao.getInstance().copyCustomProblem(action.getPid());
        return new RpcData("pid=" + newPid);
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CopyCustomProblemAction.class;
    }
}
