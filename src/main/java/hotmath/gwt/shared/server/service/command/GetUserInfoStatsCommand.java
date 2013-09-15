package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoStatsAction;
import hotmath.gwt.cm_rpc.client.rpc.UserInfoStats;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

public class GetUserInfoStatsCommand implements ActionHandler<GetUserInfoStatsAction, UserInfoStats>, ActionHandlerManualConnectionManagement {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserInfoStatsAction.class;
    }

    @Override
    public UserInfoStats execute(Connection conn, GetUserInfoStatsAction action) throws Exception {
        return HaUserDao.getInstance().getUserInfoStats(action.getUid());
    }

}
