package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.shared.client.rpc.action.LoginAction;

public class LoginCommand_Test extends CmDbTestCase {
    
    public LoginCommand_Test(String name) {
        super(name);
    }
    
    public void testLogin() throws Exception {
        LoginAction loginAction = new LoginAction("casey", "casey");
        HaBasicUser user = new LoginCommand().execute(conn, loginAction);
        assertTrue(user.getUserType() == UserType.ADMIN);
    }

    public void testLoginError() throws Exception {
        LoginAction loginAction = new LoginAction("casey", "WRONGPASSWORD");
        try {
            HaBasicUser user = new LoginCommand().execute(conn, loginAction);
            throw new Exception("Should not get here");
        }
        catch(Exception e) {
            assertTrue(e.getMessage() != null);
        }
    }
    
    public void testLoginErrorTimed() throws Exception {
        long start = System.currentTimeMillis();
        for(int i=0;i<100;i++) {
            LoginAction loginAction = new LoginAction("casey", "casey");
            HaBasicUser user = new LoginCommand().execute(conn, loginAction);
            assertTrue(user.getUserType() == UserType.ADMIN);
        }
        long end = System.currentTimeMillis();
        
        long duration = (end-start);
       
        /** should not take more than 10 seconds */
        assertTrue(duration < (1000 * 10));
        
    }

}
