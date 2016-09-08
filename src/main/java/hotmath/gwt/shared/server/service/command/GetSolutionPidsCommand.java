package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_rpc.client.rpc.GetSolutionPidsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

/** Return the pids associated with named book
 * 
 * @author casey
 *
 */
public class GetSolutionPidsCommand implements ActionHandler<GetSolutionPidsAction, CmList<String>> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionPidsAction.class;
    }

    @Override
    public CmList<String> execute(Connection conn, GetSolutionPidsAction action) throws Exception {
        CmList<String> pids = new CmArrayList<String>();
        
        switch(action.getType()) {
        
        case OUT_OF_DATE:
        	pids.addAll(SolutionDao.getInstance().getDynamicSolutionPidsNotProcessed());
        	break;
        	
        	
        case DYNAMIC_SOLUTIONS:
        	pids.addAll(SolutionDao.getInstance().getAllPidsWithGlobalSolutionContexts());
        	break;
        }
        
        
        return pids;
    }
}