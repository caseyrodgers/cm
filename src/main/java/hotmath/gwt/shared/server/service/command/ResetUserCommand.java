package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

public class ResetUserCommand implements ActionHandler<ResetUserAction, RpcData>{

	@Override
	public RpcData execute(final Connection conn, ResetUserAction action) throws Exception {
		try {
            CmProgramFlow cmProgram = new CmProgramFlow(conn,action.getUid());
            cmProgram.reset(conn);
            
            return new RpcData("status=OK");
            
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }
		
	}

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return ResetUserAction.class;
	}
	

}
