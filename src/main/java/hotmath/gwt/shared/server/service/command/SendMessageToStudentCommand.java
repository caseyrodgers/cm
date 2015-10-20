package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.StudentEventsDao;
import hotmath.cm.server.model.StudentEventsDao.EventType;
import hotmath.gwt.cm_rpc.client.rpc.SendMessageToStudentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
   Queue up message to be sent (either poll/native events)
   
 */
public class SendMessageToStudentCommand implements ActionHandler<SendMessageToStudentAction, RpcData>, ActionHandlerManualConnectionManagement {
	
	private static final Logger logger = Logger.getLogger(SendMessageToStudentCommand.class);

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SendMessageToStudentAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SendMessageToStudentAction action) throws Exception {
        StudentEventsDao.getInstance().addStudentEvent(action.getUid(), EventType.MESSAGE, action.getMessage());
        return new RpcData("status=ok");
    }
}
