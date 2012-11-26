package hotmath.cm.util.stress;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
        
        //String url = "http://new.catchupmath.com/loginService?user=" + user + "&pwd=" + pass;
        String url = "http://new.catchupmath.com/loginService?uid=2&type=ADMIN";
        
        URL oracle = new URL(url);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        StringBuffer sb = new StringBuffer();
        while ((inputLine = in.readLine()) != null) 
            sb.append(inputLine);
        in.close();
        
        
        //HaUserFactory.loginToCatchup("abc980", "t1");
        //LoginAction login = new LoginAction(user, pass);
        //HaUserLoginInfo loginInfo = ActionDispatcher.getInstance().execute(login);
    }
}
