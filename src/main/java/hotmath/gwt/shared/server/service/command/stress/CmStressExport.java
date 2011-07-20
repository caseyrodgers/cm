package hotmath.gwt.shared.server.service.command.stress;

import hotmath.HotMathProperties;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_rpc.server.rpc.ContextListener;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.command.ExportStudentsCommand;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class CmStressExport extends Thread {
	String user, pass;
	int delay;
	boolean stopThread;
	int aid;
	String emailAddress;

	static int __counter;

	final static Logger __logger = Logger.getLogger(CmStress.class);

	public CmStressExport(int aid, int delay, String emailAddress) {
		this.aid = aid;
		this.delay = delay;
		this.emailAddress = emailAddress;
	}

	public void startTest() {
		try {
			Thread.sleep(delay);
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
			__logger.info(aid + " test complete, time: "
					+ (System.currentTimeMillis() - start) / 1000);

			GetStudentGridPageAction pageAction = new GetStudentGridPageAction();
			pageAction.setAdminId(aid);

			Connection conn = HotMathProperties.getInstance()
					.getDataSourceObject().getSbDBConnection().getConnection();

			ExportStudentsAction exportAction = new ExportStudentsAction(aid,pageAction);
			exportAction.setEmailAddress(emailAddress);
			StringHolder sh = new ExportStudentsCommand().execute(conn,	exportAction);

		} catch (Exception e) {
			/** only log error, do not throw exception */
			__logger.error("Error during CmStress test: " + this, e);
		} finally {
		}
	}

	static public void main(String as[]) {
		new ContextListener();
		SbUtilities.addOptions(as);
		
		
		CmWebResourceManager.setFileBase(System.getProperty("java.io.tmpdir") + "/cm_temp");

		final int count = SbUtilities.getInt(SbUtilities.getOption("1",
				"-count"));
		final int delay = SbUtilities.getInt(SbUtilities.getOption("1000",
				"-delay"));
		String emailAddress = SbUtilities.getOption(null, "email");  /** default no email sent */
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "select aid " + "from HA_ADMIN a "
					+ " where aid <> 13" + " order by rand() "
				    + " limit "	+ count;

			conn = HMConnectionPool.getConnection();
			ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int aid = rs.getInt("aid");
				new CmStressExport(aid, delay, emailAddress).startTest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}

	}

}
