package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class SaveCustomProblemCommand implements ActionHandler<SaveCustomProblemAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveCustomProblemAction action) throws Exception {
        switch(action.getType()) {
        case WIDGET:
            CustomProblemDao.getInstance().saveProblemWidget(conn, action.getPid(), action.getData());
            break;
            
        case HINTSTEP:
            CustomProblemDao.getInstance().saveProblemHintStep(conn, action.getPid(), action.getSolutionMeta());
            break;
            
            
        case PROBLEM_STATEMENT_TEXT:
            CustomProblemDao.getInstance().saveProblemStatementText(conn, action.getPid(), action.getData());
            break;
            
            default:
                throw new Exception("Unknown SaveType:" + action);
        }
        
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveCustomProblemAction.class;
    }
}
