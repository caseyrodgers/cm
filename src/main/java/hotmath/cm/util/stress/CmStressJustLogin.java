package hotmath.cm.util.stress;

import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;
import hotmath.gwt.shared.client.rpc.action.LoginAction;

/**
 * Stand alone tool and called from /assets/util to stress test the Login
 * Process.
 * 
 * 
 * Log in as each user and do the first thing required. Which might be create a
 * test, prescription, etc..
 * 
 * 
 * @author casey
 * 
 */
public class CmStressJustLogin extends Thread implements StressTest {

    public void runTest(int aid, int uid, String user, String pass) throws Exception {
        LoginAction login = new LoginAction(user, pass);
        HaUserLoginInfo loginInfo = ActionDispatcher.getInstance().execute(login);
    }
}
