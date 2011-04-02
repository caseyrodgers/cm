package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;

public class GetUserInfoCommand_Test extends CmDbTestCase {
    
    public GetUserInfoCommand_Test(String name){
        super(name);
    }
    
    public void testGet1() throws Exception {
        UserLoginResponse response = new GetUserInfoCommand().execute(conn, new GetUserInfoAction(27554, null));
        assertTrue(response.getNextAction() != null);
    }
}
