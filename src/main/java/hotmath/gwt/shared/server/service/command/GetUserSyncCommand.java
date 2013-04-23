package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.shared.client.rpc.action.GetUserSyncAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;

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
      
      AssignmentUserInfo assignmentInfo = AssignmentDao.getInstance().getStudentAssignmentMetaInfo(action.getUid());
        
       return new UserSyncInfo(
                new CatchupMathVersion(CatchupMathProperties.getInstance().getClientVersionNumber()),
                HaLoginInfoDao.getInstance().getLatestLoginKey(action.getUid()),assignmentInfo);
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserSyncAction.class;
    }
}
