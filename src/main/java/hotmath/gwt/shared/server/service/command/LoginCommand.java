package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm_rpc_core.client.CmUserException;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;
import hotmath.gwt.shared.client.rpc.action.LoginAction;
import hotmath.testset.ha.HaUserFactory;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Log user into CM and return HaBasicUser impl
 * 
 * Throws Exception on any error logging in.
 * 
 * 
 * @author bob
 *
 */
public class LoginCommand implements ActionHandler<LoginAction, HaUserLoginInfo>{

	private static final Logger LOGGER = Logger.getLogger(LoginCommand.class);

	public HaUserLoginInfo execute(final Connection conn, LoginAction action) throws Exception {

		HaBasicUser cmUser = null;

		int uid = action.getUid();
		String username = action.getUserName();
		if (username != null) username = username.trim();
		String passwd = action.getPassword();
		if (passwd != null) passwd = passwd.trim();
		String type = action.getType();
		boolean isRealLogin = action.isRealLogin();

		if (type == null) type = "STUDENT";
		
		if (uid == 0 ) {
			/** uid param has precedence */
			String key = action.getKey();

			if (key != null) {
				HaLoginInfo hi = HaLoginInfoDao.getInstance().getLoginInfo(conn, key);
				uid = hi.getUserId();
				cmUser = HaUserFactory.getLoginUserInfo(conn, uid, type);
				username = cmUser.getLoginName();
			}
		}
		
		if (uid > 0)  {
			cmUser = HaUserFactory.getLoginUserInfo(conn, uid, type);
		}
		else if (username != null && username.equals("catchup_demo")) {
			cmUser = HaUserFactory.createDemoUser(conn, action.getSubject(), HaBasicUser.UserType.STUDENT);
		}
		else if(cmUser == null && username != null && username.length() > 0 &&
				passwd != null && passwd.length() > 0) {
			cmUser = HaUserFactory.loginToCatchup(conn, username, passwd, HaBasicUser.UserType.STUDENT);
		}

		if (cmUser == null) {
			CmUserException cme = new CmUserException("login failed");
			if (uid > 0) {
				cme = new CmUserException("Invalid login attempted.");
			}
			else if (username == null || passwd == null || username.length() == 0 || passwd.length() == 0 ) {
				cme = new CmUserException("Login Name and Password are required.");
			}
			String browserInfo = action.getBrowserInfo();
			String key = action.getKey();
			LOGGER.warn(String.format("execute(): uid: %d, username: %s, passwd: %s, key: %s, cmUser: %s, isRealLogin: %s, browserInfo: %s",
					uid, (username == null)?"NULL":username, (passwd == null)?"NULL":"not null", (key == null)?"NULL":key,
					(cmUser == null)?"NULL":"not null", isRealLogin, (browserInfo == null)?"NULL":browserInfo), cme);

			throw cme;
		}
		
		assert(cmUser != null);

		HaLoginInfo loginInfo = HaLoginInfoDao.getInstance().getLoginInfo(conn, cmUser, action.getKey(), new ClientEnvironment(action.getBrowserInfo()),isRealLogin);

		LOGGER.debug("+++ loginInfo: " + loginInfo.toString());
		return new HaUserLoginInfo(cmUser, loginInfo);
	}

    public Class<? extends Action<? extends Response>> getActionType() {
        return LoginAction.class;
    }

}
