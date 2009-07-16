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
import java.util.logging.Logger;

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
     *    then .. this user is an Admin, and log them into the cm_admin tool
     *    
     * 2. 
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

            // first see if user is in admin
            // We search the HA_ADMIN table looking
            // for a direct user/password match
            String sql = "select * from HA_ADMIN where user_name = ? and passcode = ?";

            conn = HMConnectionPool.getConnection();
            try {
                pstat = conn.prepareStatement(sql);

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) {

                    HaAdmin admin = new HaAdmin();
                    admin.setUserName(user);
                    admin.setPassword(pwd);
                    admin.setAdminId(rs.getInt("aid"));

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
            sql = "select u.uid, u.user_name, s.type " + 
                  "from HA_USER u INNER JOIN HA_ADMIN h on u.admin_id = h.aid " +
                  "INNER JOIN SUBSCRIBERS s on s.id = h.subscriber_id " + 
                  "where s.password = ? " +
                  "  and u.user_passcode = ? and u.is_active = 1";
            try {
                pstat = conn.prepareStatement(sql);

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) { 
                    int userId = rs.getInt("uid");
                    HaUser student = HaUser.lookUser(conn, userId,null);
                    student.setUserName(rs.getString("user_name"));
                    student.setPassword(pwd);
                    
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
            sql = "select u.uid, u.user_name, s.type " + 
                  "from HA_USER u INNER JOIN HA_ADMIN h on u.admin_id = h.aid " +
                  "INNER JOIN SUBSCRIBERS s on s.id = h.subscriber_id " + 
                  "where s.student_email = ? and s.type = 'PS' " +
                  "  and u.user_passcode = ? and u.is_active = 1";
            try {
                pstat = conn.prepareStatement(sql);

                pstat.setString(1, user);
                pstat.setString(2, pwd);

                rs = pstat.executeQuery();
                if (rs.first()) { 
                    int userId = rs.getInt("uid");
                    HaUser student = HaUser.lookUser(conn, userId,null);
                    student.setUserName(rs.getString("user_name"));
                    student.setPassword(pwd);
                    
                    __logger.info("Logging in user (single user student " + rs.getString("type") + "): " + user);
                
                    return student;
                }
            } finally {
                SqlUtilities.releaseResources(rs, pstat, null);
            }
            
            
            // Finally, see if a stand-alone student
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

            cmDao.addStudent(student);

            HaBasicUser user = loginToCatchup(demoUser, demoPwd);
            return user;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, conn);
        }
    }
}
