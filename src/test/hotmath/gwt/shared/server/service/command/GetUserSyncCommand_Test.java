package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.action.GetUserSyncAction;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;

public class GetUserSyncCommand_Test extends CmDbTestCase {
    
    public GetUserSyncCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null) {
            setupDemoAccount();
        }
    }
    
    public void testUserSync() throws Exception {
        UserSyncInfo usi = new GetUserSyncCommand().execute(conn, new GetUserSyncAction(_user.getUid()));
        assertTrue(usi.getVersionInfo() != null);
    }

}
