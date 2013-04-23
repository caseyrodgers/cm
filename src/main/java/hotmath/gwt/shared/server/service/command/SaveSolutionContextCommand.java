package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionType;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;



public class SaveSolutionContextCommand extends ActionBase implements ActionHandler<SaveSolutionContextAction, RpcData>{
    
    public SaveSolutionContextCommand() {
        getActionInfo().setActionType(ActionType.STUDENT);
    }
    
    @Override
    public RpcData execute(Connection conn, SaveSolutionContextAction action) throws Exception {
        try {
            SolutionDao.getInstance().saveSolutionContext(action.getRunId(),action.getPid(),action.getProblemNumber(),action.getContextVariables());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveSolutionContextAction.class;
    }
}
