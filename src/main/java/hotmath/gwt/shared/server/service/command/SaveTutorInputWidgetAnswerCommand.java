package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

public class SaveTutorInputWidgetAnswerCommand implements ActionHandler<SaveTutorInputWidgetAnswerAction, UserTutorWidgetStats>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveTutorInputWidgetAnswerAction.class;
    }

    @Override
    public UserTutorWidgetStats execute(Connection conn, SaveTutorInputWidgetAnswerAction action) throws Exception {
        return HaUserDao.getInstance().saveUserTutorInputWidgetAnswer(action.getUserId(), action.getRunId(), action.getPid(), action.getValue(), action.isCorrect());
    }

}
