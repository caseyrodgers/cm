package hotmath.gwt.cm_mobile.server.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;

import java.sql.Connection;

import sb.util.SbUtilities;

/**
 * Return the raw HTML that makes up the solution
 * 
 * Use the uid to lookup if this solution has any ShowWork applied
 * 
 * Return RpcData with the following members: solutionHtml, hasShowWork
 * 
 */
public class GetMobileSolutionCommand implements ActionHandler<GetMobileSolutionAction, SolutionResponse> {

    @Override
    public SolutionResponse execute(final Connection conn, GetMobileSolutionAction action) throws Exception {
        try {
            GetSolutionAction solutionAction = new GetSolutionAction(action.getUid(),action.getPid());
            RpcData data = new GetSolutionCommand().execute(conn,solutionAction);
            
            SolutionResponse rs = new SolutionResponse(data.getDataAsString("solutionHtml"), SbUtilities.getBoolean(data.getDataAsInt("hasShowWork")));
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException(e);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMobileSolutionAction.class;
    }
}
    