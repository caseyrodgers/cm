package hotmath.cm.login.service;

import hotmath.gwt.cm.client.data.HaBasicUser;
import hotmath.testset.ha.HaLoginInfo;
import hotmath.testset.ha.HaUserFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exposed as servlet that is called from Login Handler js in (core.js) of CM module
 * 
 * 
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
            HaLoginInfo loginInfo = new HaLoginInfo(cmUser);
            String res = "{status:'OK',key:'" +loginInfo.getKey() + "', type:'" + loginInfo.getType() + "', userId:" + loginInfo.getUserId() + "}" ;
            resp.getWriter().write(res);
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
