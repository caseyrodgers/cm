package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetCatchupMathVersionAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

/** Return current version information for Cm system
 *  that has been backed into compiled code.
 *  
 * 
 * @author casey
 *
 */
public class GetCatchupMathVersionCommand implements ActionHandler<GetCatchupMathVersionAction, CatchupMathVersion>{
    @Override
    public CatchupMathVersion execute(Connection conn, GetCatchupMathVersionAction action) throws Exception {
        return new CatchupMathVersion(CatchupMathVersionInfo.getBuildVersion());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCatchupMathVersionAction.class;
    }
}
