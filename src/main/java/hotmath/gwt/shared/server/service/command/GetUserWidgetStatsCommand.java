package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetUserWidgetStatsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

public class GetUserWidgetStatsCommand implements ActionHandler<GetUserWidgetStatsAction, UserTutorWidgetStats>, ActionHandlerManualConnectionManagement {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserWidgetStatsAction.class;
    }

    @Override
    public UserTutorWidgetStats execute(Connection conn, GetUserWidgetStatsAction action) throws Exception {
        return HaUserDao.getInstance().getUserTutorInputWidgetAnswerPercentCorrect(action.getUid());
    }

}
