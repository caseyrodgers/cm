package hotmath.cm.login.service;

import hotmath.cm.login.service.lcom.LcomManager;
import hotmath.cm.login.service.lcom.LcomStudentSignup;
import hotmath.cm.login.service.lcom.LcomTeacherSignup;
import hotmath.cm.util.CmMessagePropertyReader;
import hotmath.cm.util.UserAgentDetect;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.CmExceptionFreeProgramDenied;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_core.client.ClientInfo;
import hotmath.gwt.cm_rpc_core.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc_core.client.CmExceptionDoNotNotify;
import hotmath.gwt.cm_rpc_core.client.CmUserException;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc_core.server.rpc.ContextListener;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;
import hotmath.gwt.shared.client.rpc.action.LoginAction;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.testset.ha.HaAdmin;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.HaUserAutoRegSelfPay;
import hotmath.testset.ha.HaUserDao;
import hotmath.testset.ha.HaUserFactory;
import hotmath.testset.ha.HaUserParallelProgram;
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

	private static final long serialVersionUID = 8258701625476557059L;

	static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
    static String startDate;
    
    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if(ContextListener.getStartDate() != null)
            startDate = sdf.format(ContextListener.getStartDate());
        else
            startDate = sdf.format(System.currentTimeMillis());
    }
    
    boolean _isMobileOverride;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String user = req.getParameter("user");
		String pwd = req.getParameter("pwd");
		String action = req.getParameter("action");
		String key = req.getParameter("key");
		
		_isMobileOverride = SbUtilities.getBoolean(req.getParameter("mobile"));
		
		String exploreTopic = req.getParameter("explore");

		boolean isDebug=false;
		
		/** real_login passed as hidden input from login.html
		 *   
		 *  We want to mark a login from the login.html file special. 
		 *  This service is called on any refresh which should be 
		 *  distinguished from a 'real_login'.
		 */
		boolean isRealLogin = req.getParameter("real_login")!=null;
		
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setUserType(UserType.UNKNOWN);
		clientInfo.setUserId(-1);
    	String actionId = new StringBuilder().append(startDate).append(".").append("LOGIN").toString();
    	
        LOGGER.debug(String.format("RPC Action (userId:%d,userType:%s) (ID:%s) executing: %s toString: %s",
            	clientInfo.getUserId(), clientInfo.getUserType(), actionId, LoginService.class.getName(), (action != null)?action:"NULL"));

		Connection conn=null;
		try {

        	LOGGER.debug(String.format("RPC Action: (ID:%s) about to request DB Connection, openConnectionCount: (%d)", actionId,
        			HMConnectionPool.getInstance().getConnectionCount()));

		    conn = HMConnectionPool.getConnection();
		    
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
			String type=req.getParameter("type");

			LoginAction loginAction = new LoginAction();
			loginAction.setDebug(isDebug);
			loginAction.setKey(key);
			loginAction.setPassword(pwd);
			loginAction.setType(type);
			loginAction.setUid(uid);
			loginAction.setUserName(user);
			loginAction.setBrowserInfo(req.getHeader("User-Agent"));
			loginAction.setRealLogin(isRealLogin);
			loginAction.setSubject(req.getParameter("prog"));

			/** Try to log in, Action will throw exception on 
			 * any login error which will be caught below
			 * and redirected to login error page.  
			 * 
			 * cmUser will never be null.
			 * 
			 * Why bypass ActionDispatcher?  
			 * To allow use of already created connection.
			 *  
			 */
			HaUserLoginInfo userLoginInfo;
			HaLoginInfo loginInfo;
			HaBasicUser cmUser;
			if(exploreTopic==null) {
			    userLoginInfo = ActionDispatcher.getInstance().execute(loginAction, conn);
			    cmUser = userLoginInfo.getHaUser();
			    loginInfo = userLoginInfo.getHaLoginInfo();
			}
			else {
			    cmUser = HaUserFactory.createAnonymousUser(exploreTopic);
			    loginInfo = new HaLoginInfo();
			    loginInfo.setUserId(cmUser.getUserKey());
			}
			assert(cmUser != null);
			assert(loginInfo != null);


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
				String params = "type=auto_test&debug=true&uid=" + loginInfo.getUserId();
				if(SbUtilities.getBoolean(req.getParameter("test_rpp_only"))) {
					params += "&test_rpp_only=true";
				}

				String urlWithSessionID = "http://" + getCmServer(req) + "/loginService?" + params;
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

				if (cmUser.getExpireDate() == null) {
					/**
					 * this should never happen...
					 */

					throw new CmInvalidAccountException();
				}

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
				sb.append("',  is_mobile: ").append( (_isMobileOverride || isIpadOrIPhone(loginAction.getBrowserInfo()))?"true":"false");
				sb.append(" }");
				
				req.getSession().setAttribute("jsonizedLoginInfo", sb.toString());

				req.getSession().setAttribute("securityKey", loginInfo.getKey());

				clientInfo.setUserId(loginInfo.getUserId());
				
				if(cmUser instanceof HaAdmin) {
					clientInfo.setUserType(UserType.ADMIN);

					req.getSession().setAttribute("loginInfo", loginInfo);
					req.getRequestDispatcher("/cm_admin/launch.jsp").forward(req, resp);
				}
				else {
				    
				    /** is cm_student 
				     * 
				     */
	                /** allow override of alternate test (segment_slot) to be set on URL
	                 * 
	                 */
	                String sSegmentSlot = req.getParameter("alt_test");
	                if(sSegmentSlot != null) {
	                    int segmentSlot = SbUtilities.getInt(sSegmentSlot);
	                    HaUserDao.getInstance().setSegmentSlot(loginInfo.getUserId(),segmentSlot);
	                }
				    
				    if (cmUser instanceof HaUserParallelProgram) {
    					clientInfo.setUserType(UserType.STUDENT);
    					
    					UserLoginResponse response = new UserLoginResponse();
    					UserInfo userInfo = new UserInfo();
    					userInfo.setUid(loginInfo.getUserId());
    					userInfo.setLoginName(loginInfo.getLoginName());
    					response.setUserInfo(userInfo);
    					CmDestination dest = new CmDestination(CmPlace.WELCOME);
    					response.setNextAction(dest);
    					String jsonizedUserInfo = Jsonizer.toJson(response);			
    					req.getSession().setAttribute("jsonizedUserInfo", jsonizedUserInfo);
                        
                        if(_isMobileOverride || isIpadOrIPhone(loginAction.getBrowserInfo())) {
                            String props = "&type=PARALLEL_PROGRAM";
                            resp.sendRedirect("/cm_mobile3/?uid=" + loginInfo.getUserId() + props);
                        }
                        else {
                            req.getRequestDispatcher("/cm_student/launch.jsp").forward(req, resp);
                        }
				    }
    				else if (cmUser instanceof HaUserAutoRegSelfPay) {
    					clientInfo.setUserType(UserType.STUDENT);

    					// TODO: handle mobile devices ?

                        resp.sendRedirect("/cm-college-self-pay.html?uid=" + loginInfo.getUserId());
				    }
                    else {
    					clientInfo.setUserType(UserType.STUDENT);
    					
    					// if this is a "real login" and Student is currently in a "Parallel Program",
    					// then need to reassign to "Main Program" before calling getUserInfoAction
    					ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
    					if (isRealLogin && ppDao.isStudentInParallelProgram(loginInfo.getUserId()) == true) {
    						ppDao.reassignMainProgram(loginInfo.getUserId());
    					}
    
    					UserLoginResponse response = new GetUserInfoCommand().execute(conn, new GetUserInfoAction(loginInfo.getUserId(),loginInfo.getLoginName(),req.getParameter("debug")!=null));
    					//UserInfo userInfo = response.getUserInfo();
    					String jsonizedUserInfo = Jsonizer.toJson(response);
    					req.getSession().setAttribute("jsonizedUserInfo", jsonizedUserInfo);
    
                        if(_isMobileOverride || isIpadOrIPhone(loginAction.getBrowserInfo())) {
                            
                            String props="";
                            if(loginInfo.getType().equals("AUTO_CREATE")) {
                                props = "&type=AUTO_CREATE";
                            }
                            resp.sendRedirect("/cm_mobile3/?uid=" + loginInfo.getUserId() + props);
                            
                            //req.getRequestDispatcher("/cm_mobile3/launch.jsp").forward(req, resp);
                        }
                        else {
                            req.getRequestDispatcher("/cm_student/launch.jsp").forward(req, resp);
                        }
    				}
				}

				req.getSession().setAttribute("clientInfo", clientInfo);
			}
		}
		catch(Exception e) {
			if ( (e instanceof CmExceptionDoNotNotify)
			        || e instanceof CmRpcException && ((CmRpcException)e).isUserException()
			        || e instanceof CmUserException
			        || e instanceof CmExceptionFreeProgramDenied) {
			    req.getSession().setAttribute("error-msg", e.getMessage());
			}
			else {
				String msg = null;
				try {
					msg = CmMessagePropertyReader.getInstance().getProperty("SYSTEM_ERROR_MSG");
					LOGGER.error(msg, e);
				}
				catch(Exception cme) {}
			    req.getSession().setAttribute("error-msg",   msg);
			}
			
			LOGGER.error(String.format("*** Login failed for user: %s, pwd: %s", user, pwd), e);
			req.getRequestDispatcher("/gwt-resources/login_error.jsp").forward(req, resp);
		}
		finally {
		    SqlUtilities.releaseResources(null, null, conn);

            LOGGER.debug(String.format("RPC Action: (ID:%s) DB Connection %s, openConnectionCount: (%d)",
            		actionId, (conn != null)?"closed":"NULL, not closed", HMConnectionPool.getInstance().getConnectionCount()));
            if (HMConnectionPool.getInstance().getConnectionCount() > ActionDispatcher.CONNECTION_WARNING_THRESHOLD) {
            	LOGGER.warn(String.format("RPC Action: DB openConnectionCount: %d over threshold: %d", HMConnectionPool.getInstance().getConnectionCount(), ActionDispatcher.CONNECTION_WARNING_THRESHOLD));
            }

		}
	}

	private boolean isIpadOrIPhone(String browserInfo) {
	    UserAgentDetect userAgent = new UserAgentDetect(browserInfo, "");
	    boolean isNotDesktop = userAgent.detectTierTablet() || userAgent.detectMobileQuick();
	    return isNotDesktop;
	}

    /** return the server name to use for the CM Student app.
	 * This is complicated due to different deployment configurations.
	 *
	 * @param req
	 * @return
	 */
	private String getCmServer(HttpServletRequest req) {
		String sn = req.getServerName();
		int port = req.getServerPort();

		//if(sn.indexOf("kattare") > -1)
		//    port = 80;

		// only append port number if > 80
		return sn + ((port > 80) ? ":"+ port : "");
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
