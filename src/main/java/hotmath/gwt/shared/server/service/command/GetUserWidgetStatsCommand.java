package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.UserWidgetStats;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetUserWidgetStatsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

public class GetUserWidgetStatsCommand implements ActionHandler<GetUserWidgetStatsAction, UserWidgetStats>, ActionHandlerManualConnectionManagement {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserWidgetStatsAction.class;
    }

    @Override
    public UserWidgetStats execute(Connection conn, GetUserWidgetStatsAction action) throws Exception {
        return new UserWidgetStats(action.getUid(), HaUserDao.getInstance().getUserTutorInputWidgetAnswerPercentCorrect(action.getUid()));
    }

}
