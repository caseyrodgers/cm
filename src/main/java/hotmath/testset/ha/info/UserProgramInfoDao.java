package hotmath.testset.ha.info;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaUser;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Provide data access to the ProgramInfo set of tables
 * 
 * @author casey
 *
 */
public class UserProgramInfoDao {

    
    /** Create a UserProgramInfo object describing a single userProgram
     * 
     * @param conn
     * @param userProgId
     * @return
     * @throws Exception
     */
    public UserProgramInfo getUserProgramInfo(final Connection conn, Integer userProgId) throws Exception {
        UserProgramInfo  info = new UserProgramInfo();
        info.setProgram(new CmUserProgramDao().loadProgramInfo(conn, userProgId));
        info.setTests(HaTestDao.getProgramTests(conn, userProgId));
        
        return info;
    }

    
    /** Return list of user info objects that describe all students under this admin user
     * 
     * @param conn
     * @param adminId
     * @return
     * @throws Exception
     */
    public List<UserInfo> getUserProgramInfoForAdmin(final Connection conn, Integer adminId) throws Exception {
        
        PreparedStatement stmt=null;
        try {
            
            List<UserInfo> users = new ArrayList<UserInfo>();
            
            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("USER_PROGRAM_INFO_ADMIN"));
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                UserInfo userInfo = new UserInfo();
                HaUser user = HaUser.lookUser(conn, rs.getInt("uid"),null);
                userInfo.setUser(user);
               
                users.add(userInfo);
            }
            return users;
        }
        finally {
            SqlUtilities.releaseResources(null, stmt,null);
        }
    }
    
    
    /** Return list of user program info objects that describe all students under this admin user
     * 
     * @param conn
     * @param adminId
     * @return
     * @throws Exception
     */
    public List<UserProgramInfo> getUserProgramInfoForUser(final Connection conn, Integer userId) throws Exception {
        
        PreparedStatement stmt=null;
        try {
            
            List<UserProgramInfo> programs = new ArrayList<UserProgramInfo>();
            
            stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("USER_PROGRAM_INFO_FOR_USER"));
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                UserProgramInfo upi = getUserProgramInfo(conn, rs.getInt("id"));
                programs.add(upi);
            }
            return programs;
        }
        finally {
            SqlUtilities.releaseResources(null, stmt,null);
        }
    }
    
    
    /** Return the date of the first activity for this user.
     * 
     *  Where activity means any 'activity', not just viewing 
     *  an INMH item.
     *  
     *  
     * @param uid
     * @return date of first activity, null if no activity has occurred.
     * @throws Exception
     */
    public Date getUserFirstActivityDate(final Connection conn, Integer uid) throws Exception {
        
        PreparedStatement ps=null;
        try {
            String sql = "select * from v_HA_USER_ACTIVITY where user_id = ? order by activity_time desc limit 1";
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, uid);
            ResultSet rs = ps.executeQuery();
            if(!rs.first()) {
                return null;
            }
            
            Date firstActivity = rs.getDate("activity_time");
            return firstActivity;
        }
        finally {
            SqlUtilities.releaseResources(null,ps, null);
        }
    }    
}
