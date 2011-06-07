package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetUserSyncAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;

import java.sql.Connection;

/** Check users' current local state information looking
 *  for out of sync data.
 *  
 * 
 * @author casey
 *
 */
public class GetUserSyncCommand implements ActionHandler<GetUserSyncAction, UserSyncInfo>, ActionHandlerManualConnectionManagement{
    @Override
    public UserSyncInfo execute(Connection conn, GetUserSyncAction action) throws Exception {
       return new UserSyncInfo(
                new CatchupMathVersion(CatchupMathProperties.getInstance().getClientVersionNumber()),
                new CmProgramFlow(conn, action.getUid()).getActiveInfo());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserSyncAction.class;
    }
}
