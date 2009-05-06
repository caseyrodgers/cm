package hotmath.cm.login.service;

import hotmath.testset.ha.HaBasicUser;
import hotmath.testset.ha.HaUserFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provide redirect back to main server to get login information.
 * 
 * Login information is stored in CmLoginInfo which provivdes basic login
 * information, such as:
 * 
 * if is admin user (should show the admin tool on login) or a student user,
 * which would load the student tool
 * 
 * Information is stored in CmLoginInfo object serialized as JSON
 * 
 * 
 * @TODO: expose via GWT RPC
 * 
 * @author casey
 * 
 */
public class LoginService extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        try {
            HaBasicUser cmUser = HaUserFactory.loginToCatchup(user, pwd);
            
            // create a security cookie, and return URL with reference to key
            long key = System.currentTimeMillis();
            String skey = "cm_" + key;
            Cookie loginCookie = new Cookie("cm_key", "{key:'" + skey + "',uid:" + cmUser.getUserKey() + ", type:'" + cmUser.getUserType() + "'}");
            resp.addCookie(loginCookie);
            
            String res = "{status:'OK',key:'" + skey + "'}" ;
            resp.getWriter().write(res);
        }
        catch(Exception e) {
            resp.getWriter().write(e.getMessage());
            e.printStackTrace();
        }
    }
}
