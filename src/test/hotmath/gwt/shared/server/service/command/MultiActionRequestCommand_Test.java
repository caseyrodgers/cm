package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;

public class MultiActionRequestCommand_Test extends CmDbTestCase {
    
    public MultiActionRequestCommand_Test(String name) {
        super(name);
    }
    
    
    public void testCreate1() throws Exception {
        MultiActionRequestAction action = new MultiActionRequestAction();
        action.getActions().add(new GetAccountInfoForAdminUidAction(2));
        
        MultiActionRequestCommand command = new MultiActionRequestCommand();
        assertTrue(command.execute(conn, action) != null);
    }

}
