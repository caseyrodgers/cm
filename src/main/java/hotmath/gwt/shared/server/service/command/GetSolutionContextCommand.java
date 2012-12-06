package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.ActionType;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;



public class GetSolutionContextCommand extends ActionBase implements ActionHandler<GetSolutionContextAction, CmList<SolutionContext>>{
    
    public GetSolutionContextCommand() {
        getActionInfo().setActionType(ActionType.ADMIN);
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionContextAction.class;
    }

    @Override
    public CmList<SolutionContext> execute(Connection conn, GetSolutionContextAction action) throws Exception {
        CmList<SolutionContext> pids = new CmArrayList<SolutionContext>();
        pids.addAll(SolutionDao.getInstance().getGlobalSolutionContextAll(action.getPid()));
        return pids;
    }
}
