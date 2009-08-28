package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Create the appropriate user type
 * 
 * @author casey
 * 
 */

public class HaUserFactory {
    
    static Logger __logger = Logger.getLogger(HaUserFactory.class.getName());

    /**
     * Determine the type of user and create the appropriate user object
     * 
     * Return either a HaUser or HaAdmin object both containing basic login info
     * 
     * Catchup Login Logic:
     * 
     * 1.  if HA_ADMIN.user_name == user && HA_ADMIN_passcode == password
     *    then .. this user is an Admin, and log them into the cm_admin tool642
     *    
     * 2. If SUBSCRIBER.password = user and HA_USER.passcode = password, then is school account.
     *    
     * 3.  If SUBSCRIBER.student_email + HA_USER.passcode match, then 
     *     user is a SINGLE USER, not a school.
     *     
     * 4.  If HA_USER.group_name = passcode + HA_USER.group_name = password, then 
     * this user is a HaUserAuto and will create a new account before login completes.
     * 
     * @param user
     * @param pwd
     * @return
     * @throws Exception
     */
    static public HaBasicUser loginToCatchup(String user, String pwd) throws Exception {
        Connection conn = null;
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {

            // first see if user is an admin
            // We search the HA_ADMIN table looking
            // for a direct user/password match
        	StringBuilder sb = new StringBuilder();
        	sb.append("select a.*, max(ss.date_expire) as date_expire, ss.subscriber_id, s.type as account_type ");
            sb.append("from HA_ADMIN a ");
            sb.append("inner join SUBSCRIBERS s ");
            sb.append("   on s.id = a.subscriber_id ");
            sb.append("left outer join (");
            sb.append("   select subscriber_id, max(date_expire) as date_expire from SUBSCRIBERS_SERVICES ");
            sb.append("   where service_name = 'catchup' " );
            sb.append("   group by subscriber_id ");
            sb.append(") ss ");
            sb.append("on a.subscriber_id = ss.subscriber_id ");
            sb.append("where a.user_name = ? and a.passcode = ? ");
            sb.append("group by date_expire");

            conn = HMConnectionPool.getConnection();
            try {
                pstat = conn.prepareStatement(sb.toString());

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) {

                    HaAdmin admin = new HaAdmin();
                    admin.setUserName(user);
                    admin.setPassword(pwd);
                    admin.setAdminId(rs.getInt("aid"));
                    java.sql.Date date = rs.getDate("date_expire");
                    if (date != null)
                        admin.setExpireDate(new Date(date.getTime()));
                    admin.setAccountType(rs.getString("account_type"));
                    
                    __logger.info(String.format("+++ date_expire: %s, isExpired: ", date, admin.isExpired()));

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
            sb.delete(0, sb.toString().length());
            
            sb.append("select u.uid, u.user_name, s.type, ss.date_expire ");
            sb.append("from HA_USER u INNER JOIN HA_ADMIN h on u.admin_id = h.aid ");
            sb.append("INNER JOIN SUBSCRIBERS s on s.id = h.subscriber_id ");
            sb.append("left outer join (");
            sb.append("   select subscriber_id, max(date_expire) as date_expire from SUBSCRIBERS_SERVICES ");
            sb.append("   where service_name = 'catchup' ");
            sb.append("   group by subscriber_id ");
            sb.append(") ss ");
            sb.append("on h.subscriber_id = ss.subscriber_id ");
            sb.append("where s.password = ? ");
            sb.append("  and u.user_passcode = ? and u.is_active = 1");
            
            try {
                pstat = conn.prepareStatement(sb.toString());

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) { 
                    int userId = rs.getInt("uid");
                    HaUser student = HaUser.lookUser(conn, userId,null);
                    student.setUserName(rs.getString("user_name"));
                    student.setPassword(pwd);
                    student.setAccountType(rs.getString("type"));
                    java.sql.Date date = rs.getDate("date_expire");
                    if (date != null)
                        student.setExpireDate(new Date(date.getTime()));
                    
                    __logger.info("Logging in user (school student " + rs.getString("type") + "): " + user);
                
                    return student;
                }
            } finally {
                SqlUtilities.releaseResources(rs, pstat, null);
            }

            
            // perhaps it is a Single User student
            // Then we search for the SUBSCRIBER.student_email
            // associated with the SUBCRIBERS record that the 
            // user's HA_ADMIN record is linked to.
            sb.delete(0, sb.toString().length());
            
            sb.append("select u.uid, u.user_name, s.type, ss.date_expire ");
            sb.append("from HA_USER u INNER JOIN HA_ADMIN h on u.admin_id = h.aid ");
            sb.append("INNER JOIN SUBSCRIBERS s on s.id = h.subscriber_id ");
            sb.append("left outer join (");
            sb.append("   select subscriber_id, max(date_expire) as date_expire from SUBSCRIBERS_SERVICES ");
            sb.append("   where service_name = 'catchup' ");
            sb.append("   group by subscriber_id ");
            sb.append(") ss ");
            sb.append("on h.subscriber_id = ss.subscriber_id ");
            sb.append("where s.student_email = ? and s.type = 'PS' ");
            sb.append("  and u.user_passcode = ? and u.is_active = 1");
            
            try {
                pstat = conn.prepareStatement(sb.toString());

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) { 
                    int userId = rs.getInt("uid");
                    HaUser student = HaUser.lookUser(conn, userId,null);
                    student.setUserName(rs.getString("user_name"));
                    student.setPassword(pwd);
                    student.setAccountType(rs.getString("type"));
                    java.sql.Date date = rs.getDate("date_expire");
                    if (date != null)
                        student.setExpireDate(new Date(date.getTime()));
                    
                    __logger.info("Logging in user (single user student [" + rs.getString("type") + "]): " + user);
                
                    return student;
                }
            } finally {
                SqlUtilities.releaseResources(rs, pstat, null);
            }
            
            
            
            // The final possibility is an Auto Registration match on the userName and passcode. 
            // If the passcode matches the GROUP_NAME of the HA_USER record that defines 
            // this Auto Registration Setup. The user must be taken on the 'Auto Registration Path' 
            // right after login. It will be an error if the subscriber record is not
            // an ST.
            // perhaps it is a Single User student
            // Then we search for the SUBSCRIBER.student_email
            // associated with the SUBCRIBERS record that the 
            // user's HA_ADMIN record is linked to.
            sb.delete(0, sb.toString().length());

            sb.append("select u.uid ");
            sb.append("from   HA_USER u JOIN CM_GROUP g ON u.group_id = g.id ");
            sb.append("    JOIN HA_ADMIN a ON u.admin_id = a.aid ");
            sb.append("    JOIN SUBSCRIBERS s ON a.subscriber_id = s.id ");
            sb.append("where s.type = 'ST' ");
            sb.append("  and s.password = ? ");
            sb.append("  and g.name = ? ");
            sb.append("  and is_auto_create_template = 1");
            
            try {
                pstat = conn.prepareStatement(sb.toString());

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) { 
                    int userId = rs.getInt("uid");
                    HaUserAutoRegistration student = new HaUserAutoRegistration(HaUser.lookUser(conn, userId,null).getUid());
                    student.setUserName("auto");
                    student.setPassword("auto");
                    student.setAccountType("ST");
                    student.setExpireDate(new Date(System.currentTimeMillis() + (1000*3600*24)));
                    
                    __logger.info("Logging in user (auto registration user student " + userId + "): " + user);
                
                    return student;
                }
            } finally {
                SqlUtilities.releaseResources(rs, pstat, null);
            }            
            
            throw new HotMathException("Could not login user to Catchup Math: " + user);
            
            
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error logging in");
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

    /**
     * Create a unique demo user, created by reading a template record
     * 
     * @throws Exception
     */
    final public static int DEMO_UID = 547;

    static public HaBasicUser createDemoUser() throws Exception {

        Connection conn = null;
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "select * from HA_USER where uid = ?";

            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, DEMO_UID);
            rs = pstat.executeQuery();
            if (!rs.first())
                throw new HotMathException("Error reading demo template user");

            int adminId = rs.getInt("admin_id");

            String demoUser = "catchup_demo";
            String demoPwd = "demo_" + System.currentTimeMillis();

            CmStudentDao cmDao = new CmStudentDao();

            StudentModel student = new StudentModel();
            student.setName("Student");
            student.setPasscode(demoPwd);
            student.setAdminUid(adminId);
            student.setGroupId("1");
            student.setProgId("Prof");
            student.setSubjId("Pre-Alg");
            student.setTutoringAvail(false);
            student.setShowWorkRequired(false);

            cmDao.addStudent(conn, student);

            HaBasicUser user = loginToCatchup(demoUser, demoPwd);
            return user;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, conn);
        }
    }
}
