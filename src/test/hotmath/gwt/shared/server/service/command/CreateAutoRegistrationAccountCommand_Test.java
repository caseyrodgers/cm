package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;

public class CreateAutoRegistrationAccountCommand_Test extends CmDbTestCase {
    
    public CreateAutoRegistrationAccountCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null)
            setupDemoAccount();
    }

    /** Get user/pass that is duplicated, how?
     * 
     * @throws Exception
     */
    public void testCreateDuplicate() throws Exception {
        CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(_user.getUid(),"test", "test");
        RpcData data = new CreateAutoRegistrationAccountCommand().execute(conn, action);
        assertTrue(data.getDataAsString("error_message") != null);
    }
    
    public void testCreateNew() throws Exception {
        CreateAutoRegistrationAccountAction action = new CreateAutoRegistrationAccountAction(_user.getUid(),_user.getUserName(), _user.getPassword());
        RpcData data = new CreateAutoRegistrationAccountCommand().execute(conn, action);
        assertTrue(data.getDataAsString("key")  != null);
    }
}
