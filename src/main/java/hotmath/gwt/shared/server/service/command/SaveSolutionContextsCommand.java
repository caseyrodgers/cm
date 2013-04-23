package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionType;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.tutor_viewer.client.rpc.SaveSolutionContextsAction;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;



public class SaveSolutionContextsCommand extends ActionBase implements ActionHandler<SaveSolutionContextsAction, RpcData>{
    
    public SaveSolutionContextsCommand() {
        getActionInfo().setActionType(ActionType.STUDENT);
    }
    
    @Override
    public RpcData execute(Connection conn, SaveSolutionContextsAction action) throws Exception {
        SolutionDao.getInstance().saveGlobalSolutionContexts(action.getPid(), action.getContexts());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveSolutionContextsAction.class;
    }
}
