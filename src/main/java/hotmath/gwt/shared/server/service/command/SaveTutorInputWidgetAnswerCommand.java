package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

public class SaveTutorInputWidgetAnswerCommand implements ActionHandler<SaveTutorInputWidgetAnswerAction, UserTutorWidgetStats>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveTutorInputWidgetAnswerAction.class;
    }

    @Override
    public UserTutorWidgetStats execute(Connection conn, SaveTutorInputWidgetAnswerAction action) throws Exception {
        int userGlobalPercent = HaUserDao.getInstance().saveUserTutorInputWidgetAnswer(action.getUserId(), action.getRunId(), action.getPid(), action.getValue(), action.isCorrect());
        return new UserTutorWidgetStats(action.getUserId(), userGlobalPercent);
    }

}
