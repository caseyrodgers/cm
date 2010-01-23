package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;

public class GetAccountInfoForAdminUidCommand_Test extends CmDbTestCase {
    
    public GetAccountInfoForAdminUidCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(_user == null)
            setupDemoAccount();        
    }
    
    public void testRead() throws Exception {
        GetAccountInfoForAdminUidAction a = new GetAccountInfoForAdminUidAction(_user.getAid());
        AccountInfoModel aim = new GetAccountInfoForAdminUidCommand().execute(conn, a);
        assertTrue(aim.getAdminUserName() != null);
    }
    
    public void testGetLwlMinutes() throws Exception {
        GetAccountInfoForAdminUidAction a = new GetAccountInfoForAdminUidAction(126);
        AccountInfoModel aim = new GetAccountInfoForAdminUidCommand().execute(conn, a);
        assertTrue(aim.getTutoringMinutes() > 0);
    }
}
