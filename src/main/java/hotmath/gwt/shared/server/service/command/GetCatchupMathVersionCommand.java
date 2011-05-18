package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetCatchupMathVersionAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;

import java.sql.Connection;

/** Return current version information for Cm system
 *  that has been backed into compiled code.
 *  
 * 
 * @author casey
 *
 */
public class GetCatchupMathVersionCommand implements ActionHandler<GetCatchupMathVersionAction, CatchupMathVersion>, ActionHandlerManualConnectionManagement{
    @Override
    public CatchupMathVersion execute(Connection conn, GetCatchupMathVersionAction action) throws Exception {
        
        CmStudentDao.getInstance().verifyActiveProgram(action.getCurrentTestId());
        
        return new CatchupMathVersion(CatchupMathProperties.getInstance().getClientVersionNumber());
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCatchupMathVersionAction.class;
    }
}
