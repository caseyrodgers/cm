package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Create the appropriate user type
 * 
 * @author casey
 *
 */
public class HaUserFactory {

    
    /** Determine the type of user and create the appropriate user object
     * 
     * 
     * Return either a HaUser or HaAdmin object both containing basic login info
     * 
     * @param user
     * @param pwd
     * @return
     * @throws Exception
     */
    static public HaBasicUser loginToCatchup(String user, String pwd) throws Exception {
        Connection conn=null;
        PreparedStatement pstat=null;
        try {
            
            // first see if user is in admin
            // We search the HA_ADMIN table looking
            // for a direct user/password match
            String sql = "select * " +
                         "from HA_ADMIN " +
                         "where user_name = ? and passcode = ?";
            
            conn = HMConnectionPool.getConnection();
            try {
                pstat = conn.prepareStatement(sql);
                
                pstat.setString(1,user);
                pstat.setString(2,pwd);
                
                ResultSet rs = pstat.executeQuery();
                if(rs.first()) {
                   
                    HaAdmin admin = new HaAdmin();
                    admin.setUserName(user);
                    admin.setPassword(pwd);
                    admin.setAdminId(rs.getInt("aid"));
                    
                    return admin;
                }
            }
            finally {
                SqlUtilities.releaseResources(null,pstat,null);
            }
            
            
            // perhaps it is a normal student
            // To search for a student, we search the
            // password associated with the SUBCRIBERS
            // record that the user's HA_ADMIN record is
            // linked to.  
            sql = "select u.uid, u.user_name " +
                  "from HA_USER u INNER JOIN HA_ADMIN h on u.admin_id = h.aid " +
                  "INNER JOIN SUBSCRIBERS s on s.id = h.subscriber_id " +
                  "where  s.password = ? " + 
                  "and    u.user_passcode = ?";
            try {
                pstat = conn.prepareStatement(sql);
                
                pstat.setString(1, user);
                pstat.setString(2,pwd);
                
                ResultSet rs = pstat.executeQuery();
                if(!rs.first()) {
                    throw new HotMathException("Invalid login");
                }
                
                int userId = rs.getInt("uid");
                HaUser student = HaUser.lookUser(userId, null);
                student.setUserName(rs.getString("user_name"));
                student.setPassword(pwd);
                return student;
            }
            finally {
                SqlUtilities.releaseResources(null,pstat,null);
            }
       }
        catch(HotMathException hme) {
            throw hme;
        }
        catch(Exception e) {
            throw new HotMathException(e, "Error logging in");
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }        
    }
    
    /** Create a unique demo user, created by reading a template record
     * 
     * @throws Exception
     */
    final public static int DEMO_UID = 547;
    static public HaBasicUser createDemoUser() throws Exception {
        Connection conn=null;
        PreparedStatement pstat=null;
        try {
            String sql = "select * " +
                         "from HA_USER " +
                         "where uid = ?";
            
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1,DEMO_UID);
            ResultSet rs = pstat.executeQuery();
            if(!rs.first())
                throw new HotMathException("Error reading demo template user");
            
            
            String demoUser="catchup_demo";
            String demoPwd="demo_" + System.currentTimeMillis();
            
            PreparedStatement pstmt=null;
            try {
                sql = "insert into HA_USER(admin_id, user_name,user_passcode,test_def_id)values(?,?,?,?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1,rs.getInt("admin_id"));
                pstmt.setString(2,"Student");
                pstmt.setString(3,demoPwd);
                pstmt.setInt(4,HaTestDef.PREALG_PROFICENCY);

                int cnt = pstmt.executeUpdate();
                if(cnt != 1)
                    throw new HotMathException("Could not add new demo record");
                
                HaBasicUser user = loginToCatchup(demoUser, demoPwd);
                return user;
            }
            finally {
                pstmt.close();
            }
        }
        finally{
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }
}
