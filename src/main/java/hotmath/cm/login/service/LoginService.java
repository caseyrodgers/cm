package hotmath.cm.login.service;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.testset.ha.HaLoginInfo;
import hotmath.testset.ha.HaUserFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sb.util.SbUtilities;

/**
 * Exposed as servlet that is called from Login Handler js in (core.js) of CM module
 * 
 * NOTE: this creates the CatchupMath Demo user.  
 * 
 * @TODO: move this closer to HaUser abstraction  
 * 
 * 
 * @author casey
 * 
 */
public class LoginService extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            HaBasicUser cmUser=null;
            if(user.equals("catchup_demo")) {
                cmUser = HaUserFactory.createDemoUser();
            }
            else {
                cmUser = HaUserFactory.loginToCatchup(user, pwd);
            }
            
            HaLoginInfo loginInfo = new HaLoginInfo(cmUser);
            StringBuilder sb = new StringBuilder();
            sb.append("{status:'").append((cmUser.isExpired())?"Expired":"OK");
            sb.append("', key:'").append(loginInfo.getKey());
            sb.append("', type:'").append(loginInfo.getType());
            sb.append("', accountType: '").append(cmUser.getAccountType());
            sb.append("', userId:").append(loginInfo.getUserId());
            String dateStr = (cmUser.getExpireDate() != null) ? dateFormat.format((cmUser.getExpireDate())) : "n/a";
            sb.append(", expireDate: '").append(dateStr);
            sb.append("', loginMsg: '").append((cmUser.getLoginMessage() != null)?cmUser.getLoginMessage():"NONE");
            sb.append("' }");
            resp.getWriter().write(sb.toString());
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
