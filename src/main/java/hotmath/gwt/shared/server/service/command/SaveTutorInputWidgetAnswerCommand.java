package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

public class SaveTutorInputWidgetAnswerCommand implements ActionHandler<SaveTutorInputWidgetAnswerAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveTutorInputWidgetAnswerAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SaveTutorInputWidgetAnswerAction action) throws Exception {
        HaUserDao.getInstance().saveUserTutorInputWidgetAnswer(action.getRunId(), action.getPid(), action.isCorrect());
        return new RpcData("status=OK");
    }

}
