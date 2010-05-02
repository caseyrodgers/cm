package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.model.CentralMessage;
import hotmath.gwt.shared.client.rpc.action.CheckForCentralMessagesAction;

public class CheckForCentralMessagesCommand_Test extends CmDbTestCase {
    
    
    public CheckForCentralMessagesCommand_Test(String name) {
        super(name);
    }

    public void testGetMessage() throws Exception {
        CheckForCentralMessagesAction action = new CheckForCentralMessagesAction(1);
        CmList<CentralMessage> messages = new CheckForCentralMessagesCommand().execute(conn, action);
        assertNotNull(messages);
    }
    public void testGetMessageDispatch() throws Exception {
        CheckForCentralMessagesAction action = new CheckForCentralMessagesAction(1);
        CmList<CentralMessage> messages = ActionDispatcher.getInstance().execute(action);
        assertNotNull(messages);
    }

}
