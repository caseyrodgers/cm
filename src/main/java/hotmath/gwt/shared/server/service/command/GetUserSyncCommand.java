package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_core.client.model.CatchupMathVersion;
import hotmath.gwt.cm_core.client.model.UserSyncInfo;
import hotmath.gwt.cm_core.client.rpc.GetUserSyncAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

/**
 * Check users' current local state information looking for out of sync data.
 * 
 * (If logout or window close)
 * If fullCheck is not requested, then only save any activeMinute data associated
 * ith logout/window close.
 * 
 * 
 * @author casey
 * 
 */
public class GetUserSyncCommand implements ActionHandler<GetUserSyncAction, UserSyncInfo>, ActionHandlerManualConnectionManagement {
    @Override
    public UserSyncInfo execute(Connection conn, GetUserSyncAction action) throws Exception {

        /**
         * if this user has been busy during this "sync cycle", then add a
         * record to track that block of time
         */
        if (action.getUserActiveMinutes() > 0) {
            HaUserDao.getInstance().addUserHasBeenActiveRecord(action.getUid(), action.getUserActiveMinutes());
        }

        if(action.isFullSyncCheck()) {
            AssignmentUserInfo assignmentInfo = AssignmentDao.getInstance().getStudentAssignmentMetaInfo(action.getUid());
    
            return new UserSyncInfo(new CatchupMathVersion(CatchupMathProperties.getInstance().getClientVersionNumber()), HaLoginInfoDao.getInstance()
                    .getLatestLoginKey(action.getUid()), assignmentInfo);
        }
        else {
            return new UserSyncInfo();
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserSyncAction.class;
    }
}
