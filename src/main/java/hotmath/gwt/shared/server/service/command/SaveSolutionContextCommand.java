package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.ActionType;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;



public class SaveSolutionContextCommand extends ActionBase implements ActionHandler<SaveSolutionContextAction, RpcData>{
    
    public SaveSolutionContextCommand() {
        getActionInfo().setActionType(ActionType.STUDENT);
    }
    
    @Override
    public RpcData execute(Connection conn, SaveSolutionContextAction action) throws Exception {
        SolutionDao.getInstance().saveSolutionContext(action.getRunId(),action.getPid(),action.getProblemNumber(),action.getContextVariables());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveSolutionContextAction.class;
    }
}
