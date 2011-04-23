package hotmath.testset.ha;

import hotmath.cm.util.CmMessagePropertyReader;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmUserException;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Create the appropriate user type
 * 
 * @author casey
 * @author bob
 * 
 */

public class HaUserFactory {

	static Logger __logger = Logger.getLogger(HaUserFactory.class.getName());

	static public HaBasicUser loginToCatchup(String user, String pwd) throws Exception {
        Connection conn = null;
        try {
			conn = HMConnectionPool.getConnection();
			return loginToCatchup(conn, user, pwd);
        }
        finally {
        	
        }
	}
	
	/**
	 * Determine the type of user and create the appropriate user object
	 * 
	 * Return either a HaUser or HaAdmin object both containing basic login info
	 * 
	 * Catchup Login Logic:
	 * 
	 * 1. if HA_ADMIN.user_name == user && HA_ADMIN_passcode == password then ..
	 * this user is an Admin, and log them into the cm_admin tool642
	 * 
	 * 2. If SUBSCRIBER.password = user and HA_USER.passcode = password, then is
	 * school account.
	 * 
	 * 3. If SUBSCRIBER.student_email + HA_USER.passcode match, then user is a
	 * SINGLE USER, not a school.
	 * 
	 * 4. If HA_USER.group_name = passcode + HA_USER.group_name = password, then
	 * this user is a HaUserAuto and will create a new account before login
	 * completes.
	 * 
	 * @param user
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	static public HaBasicUser loginToCatchup(final Connection conn, String user, String pwd)
			throws Exception {
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {

			// first see if user is an admin
			// We search the HA_ADMIN table looking
			// for a direct user/password match

			String adminLoginSQL = CmMultiLinePropertyReader.getInstance()
					.getProperty("ADMIN_LOGIN");

			try {
				pstat = conn.prepareStatement(adminLoginSQL);

				pstat.setString(1, user);
				pstat.setString(2, pwd);

				rs = pstat.executeQuery();
				if (rs.first()) {

					HaAdmin admin = new HaAdmin();
					admin.setUserName(user);
					admin.setPassword(pwd);
					admin.setAdminId(rs.getInt("aid"));
					admin.setEmail(rs.getString("student_email"));
					admin.setPartner(rs.getString("partner_key"));
					java.sql.Date date = rs.getDate("date_expire");
					if (date != null)
						admin.setExpireDate(new Date(date.getTime()));
					admin.setAccountType(rs.getString("account_type"));

					__logger.info(String.format(
							"+++ date_expire: %s, isExpired: ", date,
							admin.isExpired()));

					__logger.info("Logging in user (CM Admin): " + user);

					return admin;
				}
			} finally {
				SqlUtilities.releaseResources(rs, pstat, null);
			}

			// perhaps it is a normal student
			// To search for a student, we search the
			// password associated with the SUBCRIBERS
			// record that the user's HA_ADMIN record is
			// linked to.
			String userLoginSchoolSQL = CmMultiLinePropertyReader.getInstance()
					.getProperty("USER_LOGIN_SCHOOL");

			try {
				pstat = conn.prepareStatement(userLoginSchoolSQL);

				pstat.setString(1, user);
				pstat.setString(2, pwd);

				rs = pstat.executeQuery();
				if (rs.first()) {
					int userId = rs.getInt("uid");
					HaUser student = HaUser.lookUser(conn, userId, null);
					student.setUserName(rs.getString("user_name"));
					student.setPassword(pwd);
					student.setLoginName(user);
					student.setAccountType(rs.getString("type"));
					student.setPartner(rs.getString("partner_key"));
					java.sql.Date date = rs.getDate("date_expire");
					if (date != null)
						student.setExpireDate(new Date(date.getTime()));

					__logger.info("Logging in user (school student "
							+ rs.getString("type") + "): " + user);

					return student;
				}
			} finally {
				SqlUtilities.releaseResources(rs, pstat, null);
			}

			// perhaps it is a Single User student
			// Then we search for the SUBSCRIBER.student_email
			// associated with the SUBCRIBERS record that the
			// user's HA_ADMIN record is linked to.

			String userLoginIndivSQL = CmMultiLinePropertyReader.getInstance()
					.getProperty("USER_LOGIN_INDIV");

			try {
				pstat = conn.prepareStatement(userLoginIndivSQL);

				pstat.setString(1, user);
				pstat.setString(2, pwd);

				rs = pstat.executeQuery();
				if (rs.first()) {
					int userId = rs.getInt("uid");
					HaUser student = HaUser.lookUser(conn, userId, null);
					student.setUserName(rs.getString("user_name"));
					student.setPassword(pwd);
					student.setLoginName(user);
					student.setAccountType(rs.getString("type"));
					student.setPartner(rs.getString("partner_key"));
					java.sql.Date date = rs.getDate("date_expire");
					if (date != null)
						student.setExpireDate(new Date(date.getTime()));

					__logger.info("Logging in user (single user student ["
							+ rs.getString("type") + "]): " + user);

					return student;
				}
			} finally {
				SqlUtilities.releaseResources(rs, pstat, null);
			}

			// The final possibility is an Auto Registration match on the
			// userName and passcode.
			// If the passcode matches the GROUP_NAME of the HA_USER record that
			// defines
			// this Auto Registration Setup. The user must be taken on the 'Auto
			// Registration Path'
			// right after login. It will be an error if the subscriber record
			// is not
			// an ST.
			// perhaps it is a Single User student
			// Then we search for the SUBSCRIBER.student_email
			// associated with the SUBCRIBERS record that the
			// user's HA_ADMIN record is linked to.
			String userLoginAutoRegSQL = CmMultiLinePropertyReader
					.getInstance().getProperty("USER_LOGIN_AUTOREG");

			try {
				pstat = conn.prepareStatement(userLoginAutoRegSQL);

				pstat.setString(1, user);
				pstat.setString(2, pwd);

				rs = pstat.executeQuery();
				if (rs.first()) {
					int userId = rs.getInt("uid");
					HaUserAutoRegistration student = new HaUserAutoRegistration(
							HaUser.lookUser(conn, userId, null).getUid());
					student.setPartner("partner");
					student.setLoginName(user);
					student.setUserName("auto");
					student.setPassword("auto");
					student.setAccountType("ST");
					student.setExpireDate(new Date(System.currentTimeMillis()
							+ (1000 * 3600 * 24)));

					__logger.info("Logging in user (auto registration user student "
							+ userId + "): " + user);

					return student;
				}
			} finally {
				SqlUtilities.releaseResources(rs, pstat, null);
			}

			checkForSchoolLoginFailure(conn, user);

			checkForIndivAcctLoginFailure(conn, user);

			//checkForAdminLoginFailure(;, userName);

            throw new CmUserException(CmMessagePropertyReader.getInstance().getProperty("LOGIN_NAME_NOT_RECOGNIZED"));
		} catch (CmException cme) {
			throw cme;
		} catch (Exception e) {
			throw new CmException("Error logging in", e);
		}
	}

	static public HaBasicUser createDemoUser() throws Exception {

		Connection conn = null;
		try {
			conn = HMConnectionPool.getConnection();
			return createDemoUser(conn);
		}
		finally {
			
		}
	}

	/**
	 * Create a unique demo user, created by reading a template record
	 * 
	 * @throws Exception
	 */
	static public HaBasicUser createDemoUser(final Connection conn) throws Exception {

		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			String sql = "select * from HA_USER where uid = ?";

			pstat = conn.prepareStatement(sql);

			int demoId = Integer.parseInt(CmMultiLinePropertyReader
					.getInstance().getProperty("DEMO_USER_ID").trim());

			__logger.debug("Creating demo user with DEMO_USER_ID = " + demoId);

			pstat.setInt(1, demoId);
			rs = pstat.executeQuery();
			if (!rs.first())
				throw new CmException("Error reading demo template user");

			int adminId = rs.getInt("admin_id");

			String demoUser = "catchup_demo";
			String demoPwd = "demo_" + System.currentTimeMillis();

			CmStudentDao cmDao = new CmStudentDao();

			StudentModel student = new StudentModel();
			student.setName("Student: " + System.currentTimeMillis());
			student.setPasscode(demoPwd);
			student.setAdminUid(adminId);
			student.setGroupId("1");
			student.getProgram().setProgramType(
					CmProgram.PREALG_PROF.getProgramType());
			student.getProgram().setSubjectId(
					CmProgram.PREALG_PROF.getSubject());
			student.setPassPercent("70%");
			student.getSettings().setTutoringAvailable(false);
			student.getSettings().setShowWorkRequired(false);
			student.setIsDemoUser(true);

			cmDao.addStudent(conn, student);

			HaBasicUser user = loginToCatchup(conn, demoUser, demoPwd);
			return user;
		} finally {
			SqlUtilities.releaseResources(rs, pstat, null);
		}
	}

	/**
	 * Create a unique demo user, created by reading a template record
	 * 
	 * @throws Exception
	 */
	static public int createUser(final Connection conn, int adminId,
			String groupName, String userName, String userPass)
			throws Exception {

		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			String sql = "select * from HA_USER where uid = ?";

			pstat = conn.prepareStatement(sql);

			CmStudentDao cmDao = new CmStudentDao();

			StudentModel student = new StudentModel();
			student.setName(userName);
			student.setPasscode(userPass);
			student.setAdminUid(adminId);

			GroupInfoModel group = new CmAdminDao().getGroup(conn, adminId,
					groupName);
			if (group == null)
				throw new CmException("Group '" + groupName
						+ "' could not be found");

			student.setGroupId(Integer.toString(group.getId()));
			student.getProgram().setProgramType(
					CmProgram.PREALG_PROF.getProgramType());
			student.getProgram().setSubjectId(
					CmProgram.PREALG_PROF.getSubject());
			student.setPassPercent("70%");
			student.getSettings().setTutoringAvailable(false);
			student.getSettings().setShowWorkRequired(false);
			student.setIsDemoUser(false);

			int uid = cmDao.addStudent(conn, student).getUid();
			return uid;
		} finally {
			SqlUtilities.releaseResources(rs, pstat, null);
		}
	}

	static public HaBasicUser getLoginUserInfo(int uid, String type) throws Exception {
        Connection conn = null;
		try {
			conn = HMConnectionPool.getConnection();
			return getLoginUserInfo(conn, uid, type);
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);			
		}
	}

	/**
	 * Return login information user for user with known uid
	 * 
	 * @param uid
	 * @param type
	 * @return
	 * @throws Exception
	 */
	static public HaBasicUser getLoginUserInfo(final Connection conn, int uid, String type)
			throws Exception {
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			if (type.equals("ADMIN")) {
				/** return admin information */
				HaAdmin admin = new HaAdmin();
				String sql = "select a.*,a.partner_key, ss.date_expire, ss.subscriber_id, s.type as account_type,a.passcode,s.student_email "
						+ " from HA_ADMIN a "
						+ "  inner join SUBSCRIBERS s on s.id = a.subscriber_id "
						+ " left outer join ( "
						+ " select subscriber_id, max(date_expire) as date_expire from SUBSCRIBERS_SERVICES "
						+ " where service_name = 'catchup' "
						+ " group by subscriber_id "
						+ ") ss on a.subscriber_id = ss.subscriber_id "
						+ " where a.aid = ? ";

				pstat = conn.prepareStatement(sql);
				pstat.setInt(1, uid);
				rs = pstat.executeQuery();
				if (rs.first()) {
					admin.setUserName(rs.getString("user_name"));
					admin.setPassword(rs.getString("passcode"));
					admin.setAdminId(rs.getInt("aid"));
					admin.setEmail(rs.getString("student_email"));
					admin.setPartner(rs.getString("partner_key"));
					java.sql.Date date = rs.getDate("date_expire");
					if (date != null)
						admin.setExpireDate(new Date(date.getTime()));
					admin.setAccountType(rs.getString("account_type"));

					return admin;
				}
			} else {
				/** return student information */
				String sql = "select u.uid, u.user_name,  s.type,  ss.date_expire, s.password,h.partner_key"
						+ " from   HA_USER u "
						+ " inner join HA_ADMIN h "
						+ "  on u.admin_id = h.aid "
						+ " inner join SUBSCRIBERS s "
						+ " on s.id = h.subscriber_id "
						+ " left outer join (select subscriber_id, "
						+ "max(date_expire) as date_expire "
						+ " from   SUBSCRIBERS_SERVICES "
						+ " where  service_name = 'catchup' "
						+ " group  by subscriber_id) ss "
						+ " on h.subscriber_id = ss.subscriber_id "
						+ " where  u.uid = ? " + " and u.is_active = 1";
				pstat = conn.prepareStatement(sql);
				pstat.setInt(1, uid);
				rs = pstat.executeQuery();
				if (rs.first()) {
					int userId = rs.getInt("uid");
					HaUser student = HaUser.lookUser(conn, userId, null);
					student.setUserName(rs.getString("user_name"));
					student.setPassword(rs.getString("password"));
					student.setLoginName(rs.getString("user_name"));
					student.setAccountType(rs.getString("type"));
					student.setPartner(rs.getString("partner_key"));
					java.sql.Date date = rs.getDate("date_expire");
					if (date != null)
						student.setExpireDate(new Date(date.getTime()));
					return student;
				}
			}

			throw new CmException("Could not find user/admin with uid: " + uid);

		} finally {
			SqlUtilities.releaseResources(rs, pstat, null);
		}
	}

	/**
	 * 
	 * @param userName
	 * 
	 * if userName is a valid school id, then password is wrong for school account; throw Exception
     *
	 */
    private static void checkForSchoolLoginFailure(final Connection conn, String userName)  throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("SCHOOL_ACCOUNT_SERVICES");

        try {
	        ps = conn.prepareStatement(sql);

	        ps.setString(1, userName);
	        ps.setString(2, userName);
            rs = ps.executeQuery();
            
            Map <String, Boolean> serviceMap = loadServices(rs);
            
        	if (serviceMap.size() > 0) {
        		
        		Boolean cmIsExpired = serviceMap.get("catchup");
        		// if cmIsExpired is not null, then user (student OR admin) must have entered an incorrect password
        		if (cmIsExpired != null) {
            		throw new CmUserException(CmMessagePropertyReader.getInstance().getProperty("STUDENT_OR_ADMIN_WRONG_PASSWORD_SCHOOL_ACCT"));
        		}

        		if (serviceMap.containsKey("solution")) {
        			throw new CmUserException(CmMessagePropertyReader.getInstance().getProperty("SCHOOL_HOTMATH_ACCT"));
        		}
        	}

        }
        finally {
        	SqlUtilities.releaseResources(rs, ps, null);
        }
    }

	/**
	 * 
	 * @param userName
	 * 
	 * if userName is an email address, then password is wrong for personal account; throw Exception
	 */
    private static void checkForIndivAcctLoginFailure(final Connection conn, String userName)  throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("INDIV_ACCOUNT_SERVICES");

        try {
	        ps = conn.prepareStatement(sql);

	        ps.setString(1, userName);
	        ps.setString(2, userName);
            rs = ps.executeQuery();
            
            Map <String, Boolean> serviceMap = loadServices(rs);
            
        	if (serviceMap.size() > 0) {
        		
        		Boolean cmIsExpired = serviceMap.get("catchup");
        		// if cmIsExpired is not null, then indiv user must have entered an incorrect password
        		if (cmIsExpired != null) {
            		throw new CmUserException(CmMessagePropertyReader.getInstance().getProperty("WRONG_PASSWORD_INDIV_ACCT"));        			
        		}

        		if (serviceMap.containsKey("solution")) {
        			throw new CmUserException(CmMessagePropertyReader.getInstance().getProperty("INDIV_HOTMATH_ACCT"));
        		}
        	}

        }
        finally {
        	SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    private static Map<String, Boolean> loadServices(ResultSet rs) throws Exception {
        Map <String, Boolean> serviceMap = new HashMap<String, Boolean>();
    	if (rs.first()) {
    		do {
    			String serviceName = rs.getString("service_name");
    			Boolean isExpired = (rs.getInt("is_expired") > 0);
    			serviceMap.put(serviceName, isExpired);
    		} while (rs.next());
    	}
    	return serviceMap; 
    }
    
    /**
     * 
     * @param userName
     *
     * if userName is a valid Admin id, then password is wrong for Admin account; throw Exception
     * 
     * 
     * 
     */
    private static void checkForAdminLoginFailure(final Connection conn, String userName) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADMIN_ACCOUNT_EXISTS");

        try {
	        ps = conn.prepareStatement(sql);

	        ps.setString(1, userName);
            rs = ps.executeQuery();
        	if (rs.first()) {
        		throw new CmUserException(CmMessagePropertyReader.getInstance().getProperty("ADMIN_WRONG_PASSWORD_SCHOOL_ACCT"));
        	}
        }
        finally {
        	SqlUtilities.releaseResources(rs, ps, null);
        }        
    }

	static public void main(String as[]) {
		Connection conn=null;
	try {
		conn = HMConnectionPool.getConnection();
		for(int i=20;i>0;i--) {
			HaBasicUser u = createDemoUser();
			
			
			int uid = u.getUserKey();
			
			StudentModelI student = new CmStudentDao().getStudentModelBase(conn, uid);
			StudentActiveInfo sai = new CmStudentDao().loadActiveInfo(conn, uid);
			System.out.println("Info: " + student.getUid() + ", " + sai.getActiveSegment());
		}
		
		System.out.println("Done!");
		System.exit(0);
	}
	catch(Exception e) {
		e.printStackTrace();
	}
}
}
