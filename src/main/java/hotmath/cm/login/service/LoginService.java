package hotmath.cm.login.service;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.testset.ha.HaLoginInfo;
import hotmath.testset.ha.HaUserFactory;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Exposed as servlet that is called from Login Handler js in (core.js) of CM module
 * 
 * NOTE: this creates the CatchupMath Demo user.  
 * 
 * parameters: action=sample will create a new sample user in non-debug mode.
 *             action=auto_test will launch a new sample user auto test and debug mode. 
 * 
 * @TODO: move this closer to HaUser abstraction  
 * 
 * 
 * @author casey
 * 
 */
public class LoginService extends HttpServlet {
	
    static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        String action = req.getParameter("action");

        
        if(action == null)
            action = "";
        
        else if(action.equals("sample")) {
            user = "catchup_demo";
            action = "login";
        }
        else if(action.startsWith("auto_test")) {
            user="catchup_demo";
        }
        
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
            
            /** either redirect this user to CM using current information
             * or return JSON describing login info.
             */
            if(action.equals("login")) {
                /** redirect and connect to the CM student  
                 * 
                 */
                String urlWithSessionID = "http://" + getCmServer(req) + "/cm_student/CatchupMath.html?key=" + loginInfo.getKey();
                resp.sendRedirect(urlWithSessionID);
            }
            else if(action.equals("auto_test")) {
                /** redirect and connect to the CM student,passing auto_test parameter  
                 * 
                 */
                String urlWithSessionID = "http://" + getCmServer(req) + "/cm_student/CatchupMath.html?type=auto_test&debug=true&uid=" + loginInfo.getUserId() + "&key=" + loginInfo.getKey();
                resp.sendRedirect(urlWithSessionID);
            }
            else if(action.equals("auto_test_net")) {
                /** redirect and connect to the CM student,passing auto_test parameter  
                 * 
                 */
                String urlWithSessionID = "http://" + getCmServer(req) + "/cm_student/CatchupMath.html?type=auto_test_net&debug=true&uid=" + loginInfo.getUserId() + "&key=" + loginInfo.getKey();
                resp.sendRedirect(urlWithSessionID);
            }

            else {
                /** create JSON that can be used to connect to this account
                 * 
                 */
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
        }
        catch(Exception e) {
            resp.getWriter().write(e.getMessage());
            LOGGER.error(String.format("*** Login failed for user: %s, pwd: %s", user, pwd), e);
        }
    }
	
	/** return the server name to use for the CM Student
	 * app.  This is complicated due to different deployment
	 * configurations.
	 * 
	 * @param req
	 * @return
	 */
	private String getCmServer(HttpServletRequest req) {
	    String sn = req.getServerName();
	    int port = req.getServerPort();
	    
	    if(sn.indexOf("kattare") > -1)
	        port = 80;
	    
	    return sn + (port != 80?":" + port:"");
	}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
