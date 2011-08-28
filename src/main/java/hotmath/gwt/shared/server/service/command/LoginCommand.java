package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
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
		String passwd = action.getPassword();
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
			cmUser = HaUserFactory.createDemoUser(conn);
		}
		else if(cmUser == null && username != null && passwd != null) {
			cmUser = HaUserFactory.loginToCatchup(conn, username, passwd);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("execute(): cmUser is: %s", (cmUser == null)?"NULL":"not null"));
		}
		assert(cmUser != null);

		HaLoginInfo loginInfo = HaLoginInfoDao.getInstance().getLoginInfo(conn, cmUser, new ClientEnvironment(action.getBrowserInfo()),isRealLogin);

		return new HaUserLoginInfo(cmUser, loginInfo);
	}

    public Class<? extends Action<? extends Response>> getActionType() {
        return LoginAction.class;
    }

}
