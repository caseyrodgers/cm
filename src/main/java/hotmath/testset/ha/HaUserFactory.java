package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.gwt.cm.client.util.CmRpcException;
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
            String sql = "select * from HA_ADMIN where user_name = ? and passcode = ?";
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
            sql = "select * from HA_USER where user_name = ? and user_passcode = ?";
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
                student.setUserName(user);
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
}
