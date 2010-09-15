package hotmath.cm.login.service;

import hotmath.cm.login.service.lcom.LcomManager;
import hotmath.cm.login.service.lcom.LcomStudentSignup;
import hotmath.cm.login.service.lcom.LcomTeacherSignup;
import hotmath.gwt.cm_rpc.client.ClientInfo;
import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.testset.ha.HaAdmin;
import hotmath.testset.ha.HaLoginInfo;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.HMConnectionPool;
import hotmath.util.Jsonizer;
import hotmath.util.sql.SqlUtilities;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

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
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        String action = req.getParameter("action");
        String key = req.getParameter("key");
        boolean isDebug=false;
        try {
        	
            if(action == null)
                action = "";
            else if(action.equals("sample")) {
                user = "catchup_demo";
                action = "login";
            }
            else if(action.startsWith("auto_test")) {
                user="catchup_demo";
            }
            else if(action.equals("LCOM")) {
            	/** if LCOM action, then handle separately
            	 * 
            	 */
            	handleLcomLogin(req, resp);
            	return;
            }

            isDebug = req.getParameter("debug") != null;
        	int uid=SbUtilities.getInt(req.getParameter("uid"));

        	HaBasicUser cmUser=null;
        	
        	if(uid == 0 ) {
        		/** uid param has precedence */
	            if(key != null) {
	            	Connection conn=null;
	            	try {
	            		conn = HMConnectionPool.getConnection();
	            		HaLoginInfo hi = HaLoginInfo.getLoginInfo(conn, key);
	            		uid = hi.getUserId();
	            		cmUser = HaUserFactory.getLoginUserInfo(uid,"STUDENT");
	            		user = cmUser.getLoginName();
	            	}
	            	finally {
	            		SqlUtilities.releaseResources(null,null,conn);
	            	}
	            }
        	}
        	
        	String type=req.getParameter("type");
        	if(type == null)
        		type = "STUDENT";
        	
            
        	if(uid > 0) {
        		cmUser = HaUserFactory.getLoginUserInfo(uid,type);
        	}
        	else if(user.equals("catchup_demo")) {
                cmUser = HaUserFactory.createDemoUser();
            }
            else if(cmUser == null) {
                cmUser = HaUserFactory.loginToCatchup(user, pwd);
            }
            
            HaLoginInfo loginInfo = new HaLoginInfo(cmUser);
            
            /** either redirect this user to CM using current information
             * or return JSON describing login info.
             */
            if(action.equals("login")) {
            	throw new Exception("Not Supported");
            }
            else if(action.equals("auto_test")) {
                /** redirect and connect to the CM student,passing auto_test parameter  
                 * 
                 */
                String urlWithSessionID = "http://" + getCmServer(req) + "/loginService?type=auto_test&debug=true&uid=" + loginInfo.getUserId();
                resp.sendRedirect(urlWithSessionID);
            }
            else if(action.equals("auto_test_net")) {
                /** redirect and connect to the CM student,passing auto_test parameter  
                 * 
                 */
                String urlWithSessionID = "http://" + getCmServer(req) + "/loginService?type=auto_test_net&debug=true&uid=" + loginInfo.getUserId();
                resp.sendRedirect(urlWithSessionID);
            }
            else {
            	
            	/** Check for expired account.  But, allow debug users to bypass
            	 * 
            	 */
            	if(!isDebug && cmUser.getExpireDate().getTime() < System.currentTimeMillis()) {
            		throw new CmExceptionUserExpired(cmUser);
            	}
            	
            	/** add shared user info into session and forward to specific
            	 *  launcher JSP pages that will encapsulate the login/init data
            	 *  in the returned HTML as JSON JS vars.
            	 *  
            	 *  Each type (cm_student, cm_admin) will have different data
            	 *  required to initialize, which is defined in each launch.jsp file.
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
        		sb.append("',  loginName: '").append((cmUser.getLoginName() != null)?cmUser.getLoginName():"");
        		sb.append("',  email: '").append((cmUser.getEmail() != null)?cmUser.getEmail():"");
        		sb.append("',  partner: '").append(cmUser.getPartner() != null?cmUser.getPartner():"");        		
        		sb.append("' }");
        		req.getSession().setAttribute("jsonizedLoginInfo", sb.toString());
        		
        		req.getSession().setAttribute("securityKey", loginInfo.getKey());

        		ClientInfo clientInfo = new ClientInfo();
        		clientInfo.setUserId(loginInfo.getUserId());
        		
            	if(cmUser instanceof HaAdmin) {
            		clientInfo.setUserType(UserType.ADMIN);

            		req.getSession().setAttribute("loginInfo", loginInfo);
            		req.getRequestDispatcher("/cm_admin/launch.jsp").forward(req, resp);
            	}
            	else {
            		clientInfo.setUserType(UserType.STUDENT);
            		UserInfo userInfo = ActionDispatcher.getInstance().execute(new GetUserInfoAction(loginInfo.getUserId(),loginInfo.getLoginName()));
            		String jsonizedUserInfo = Jsonizer.toJson(userInfo);
            		req.getSession().setAttribute("jsonizedUserInfo", jsonizedUserInfo);
            		
            		req.getRequestDispatcher("/cm_student/launch.jsp").forward(req, resp);
            	}

            	req.getSession().setAttribute("clientInfo", clientInfo);
            }
        }
        catch(Exception e) {
        	req.getSession().setAttribute("exception", e);
        	req.getRequestDispatcher("/gwt-resources/login_error.jsp").forward(req, resp);
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
    
    /** perform login for LCOM integration
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     * 
     * http://catchupmath.com/?lcomtype=student&sp1=Catchup%2520Math%2520%20Teacher&sp2=a38f84d9-29c3-4080-a5e7-5996e7b1fdb8&sp3=e1f83d2e-4e64-4342-b94f-bd7095c44c00&sp4=a38f84d9-29c3-4080-a5e7-5996e7b1fdb8
     */
    private void handleLcomLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    	try {
	    	String type = req.getParameter("lcomtype");
	    	String urlToRedirectTo=null;
	    	if(type.equals("student")) {
		    	String firstLast = URLDecoder.decode(req.getParameter("sp1"),"UTF-8");
		    	String userId = req.getParameter("sp2");
		    	String courseId = URLDecoder.decode(req.getParameter("sp3"),"UTF-8"); 
		    	String teacherId = req.getParameter("sp4");
		    	urlToRedirectTo = LcomManager.loginStudent(new LcomStudentSignup(firstLast, userId, courseId, teacherId,0));
	    	}
	    	else if(type.equals("teacher")) {
		    	String firstLast = URLDecoder.decode(req.getParameter("tp1"),"UTF-8");
		    	String teacherId = req.getParameter("tp2");
		    	String email = req.getParameter("tp3"); // null for now
		    	String district = URLDecoder.decode(req.getParameter("tp4"),"UTF-8");
		    	String zip = req.getParameter("tp5"); // null for now
		    	urlToRedirectTo = LcomManager.loginTeacher(new LcomTeacherSignup(firstLast, teacherId, email, district, zip));
	    	}
	    	resp.sendRedirect(urlToRedirectTo);
    	}
    	catch(Exception e) {
    		req.getSession().setAttribute("exception", e);
        	req.getRequestDispatcher("/lcom/login_error.jsp").forward(req, resp);
    	}
	    	
    }
}
