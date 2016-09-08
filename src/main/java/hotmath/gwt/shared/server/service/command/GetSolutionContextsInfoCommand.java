package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.model.SolutionContextsInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionContextsInfoAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.SolutionDao;

/**
 * Return the raw HTML that makes up the solution
 * 
 * Use the uid to lookup if this solution has any ShowWork applied
 * 
 * Return RpcData with the following members: solutionHtml, hasShowWork
 * 
 */
public class GetSolutionContextsInfoCommand implements ActionHandler<GetSolutionContextsInfoAction, SolutionContextsInfo>,
        ActionHandlerManualConnectionManagement {

    private static final Logger logger = Logger.getLogger(GetSolutionContextsInfoCommand.class);

    @Override
    public SolutionContextsInfo execute(Connection conn, GetSolutionContextsInfoAction action) throws Exception {
    	List<SolutionContext> contexts = SolutionDao.getInstance().getGlobalSolutionContextAll(action.getPid());
    	return new SolutionContextsInfo(contexts);
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
    	return GetSolutionContextsInfoAction.class;    	
    }
}
