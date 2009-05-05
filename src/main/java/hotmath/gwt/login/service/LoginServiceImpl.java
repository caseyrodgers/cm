package hotmath.gwt.login.service;

import hotmath.testset.ha.HaBasicUser;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.Jsonizer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Is an impl because it will be implementing soon ...
 * 
 * @author casey
 *
 */
public class LoginServiceImpl extends HttpServlet {
    
    
    /** Lookup this user information in the CM tables and return 
     *  the basic user information.
     *  
     *  
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        try {
            HaBasicUser cmUser = HaUserFactory.loginToCatchup(user, pwd);
            
            String json = Jsonizer.toJson(cmUser);
            
            resp.getWriter().write(json);
        }
        catch(Exception e) {
            resp.getWriter().write(e.getMessage());
            e.printStackTrace();
        }
    }

    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
}
