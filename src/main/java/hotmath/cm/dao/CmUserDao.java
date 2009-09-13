package hotmath.cm.dao;

import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CmUserDao {
    
    
    /** Return list of HaUser objects that are currently
     *  assigned to the given adminId.
     *  
     *   
     * @param adminId
     * @return
     * @throws Exception
     */
    public List<HaUser> getUsers(Integer adminId) throws Exception {
    
        List<HaUser> users = new ArrayList<HaUser>();
        
        String GET_ADMIN_SQL = "select * from HA_USER where admin_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(GET_ADMIN_SQL);
            ps.setInt(1,adminId);
            
            rs = ps.executeQuery();
            while(rs.next()) {
                
                /** Return bare info
                 * @TODO: return all user info
                 */
                HaUser user = new HaUser();
                user.setUid(rs.getInt("uid"));
                user.setUserName(rs.getString("user_name"));
                user.setPassword(rs.getString("user_passcode"));
                
                users.add(user);
            }
            
            return users;
            
        }
        finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }

}
