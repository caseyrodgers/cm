package hotmath.gwt.shared.server.service.command.stress;

import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc.server.rpc.ContextListener;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;
import hotmath.gwt.shared.client.rpc.action.LoginAction;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

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
public class CmStress extends Thread {
	
	static List<CmStress> __instances = new ArrayList<CmStress>();

	String user, pass;
	int delay;
	boolean stopThread;
	
	static int __counter;
	
	final static Logger __logger = Logger.getLogger(CmStress.class);

	public CmStress(String user, String pass, int delay) {
		this.user = user;
		this.pass = pass;
		this.delay = delay;
		
		__instances.add(this);
	}

	int id = __counter++;

	public void startTest() {
		__logger.info("Login test start: " + this);
		try {
			Thread.sleep(delay);
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "CmStress [user=" + user + ", pass=" + pass + ", delay=" + delay	+ ", id=" + id + "]";
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();
		try {
			int delayTime = SbUtilities.getRandomNumber(10) * delay;

			__logger.info(id + " test start (delay=" + delayTime + ")");

			LoginAction login = new LoginAction(this.user, this.pass);
			HaUserLoginInfo loginInfo = ActionDispatcher.getInstance().execute(
					login);

			int userId = loginInfo.getHaLoginInfo().getUserId();
			Thread.sleep(delayTime);

			GetCmProgramFlowAction flowAction = new GetCmProgramFlowAction(
					userId, FlowType.ACTIVE);
			CmProgramFlowAction flowActive = ActionDispatcher.getInstance()
					.execute(flowAction);

			switch (flowActive.getPlace()) {
			case AUTO_ADVANCED_PROGRAM:
				break;

			case AUTO_PLACEMENT:
				break;

			case END_OF_PROGRAM:
				break;

			case PRESCRIPTION:
				assert (flowActive.getPrescriptionResponse() != null);
				break;

			case QUIZ:
				assert (flowActive.getQuizResult() != null);
				break;
			}

			__logger.info(id + " test complete, time: "
					+ (System.currentTimeMillis() - start) / 1000);

		} catch (Exception e) {
			/** only log error, do not throw exception */
			__logger.error("Error during CmStress test: " + this, e);
		} finally {
			__instances.remove(this);
		}
	}


	static public void main(String as[]) {
		new ContextListener();
		SbUtilities.addOptions(as);

		final int count = SbUtilities.getInt(SbUtilities.getOption("100",
				"-count"));
		final int delay = SbUtilities.getInt(SbUtilities.getOption("1000",
				"-delay"));
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "select a.user_name, u.user_passcode "
					+ "from HA_ADMIN a "
					+ " JOIN SUBSCRIBERS s on s.id = a.subscriber_id "
					+ " JOIN HA_USER u on u.admin_id = a.aid "
					+ " JOIN CM_USER_PROGRAM p on p.id = u.user_prog_id "
					+ " where u.is_active = 1 " + " and u.admin_id != 13 "
					+ " and u.uid = 3899 "
					+ " and is_auto_create_template = 0 " + " order by rand() "
					+ " limit " + count;
			conn = HMConnectionPool.getConnection();
			ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String uName = rs.getString("user_name");
				String uPass = rs.getString("user_passcode");

				new CmStress(uName, uPass, delay).startTest();
			}
			
			
			int cnt = __instances.size();
			
			/** create exit watcher that will perform exit
			 * when all threads have completed.
			 * 
			 * TODO: why do I have to do this?
			 */
			Thread exitWatcher = new Thread() {
				public void run() {
					try {
						while(__instances.size() > 0) {
							sleep(1000);
						}
						__logger.info("Exiting");
						System.exit(0);
					}
					catch(Exception e) {
						__logger.error(e);
					}
				}
			};
			exitWatcher.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}

	}

}
