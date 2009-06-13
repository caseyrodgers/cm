package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;


/** Catchup math login information
 * 
 * 
 * 
 * @author casey
 *
 */
public class HaLoginInfo {

    String key;
    String type;
    int    userId;
    
    public HaLoginInfo() {
        /** empty */
    }
    
    public HaLoginInfo(HaBasicUser user) throws Exception {
        try {
            key = addLoginInfo(user);
            userId = user.getUserKey();
            type = user.getUserType().toString();
            
        }
        catch(Exception e) {
            throw new HotMathException(e, "Error logging into Catchup Math");
        }
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    
    public String toString() {
       return  this.key + ", " + this.type + ", " + this.userId;
    }


    static private String addLoginInfo(HaBasicUser user) throws Exception {
        Connection conn = null;
        PreparedStatement pstat = null;

        try {
            String key = "cm_" + System.currentTimeMillis();
            // first see if user is in admin
            String sql = "insert into HA_USER_LOGIN(user_id, login_key, user_type, login_time)values(?,?,?,?)";
                
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, user.getUserKey());
            pstat.setString(2, key);
            pstat.setString(3, user.getUserType().toString());
            pstat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
    
            if(pstat.executeUpdate() != 1)
                throw new Exception("could not not insert new HA_USER_LOGIN record");
            
            return key;
            
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }
    
    static public HaLoginInfo getLoginInfo(String key) throws Exception {
        Connection conn = null;
        PreparedStatement pstat = null;

        try {
            // first see if user is in admin
            String sql = "select * " +
                         " from HA_USER_LOGIN " +
                         " where login_key = ?";
                
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setString(1, key);
            ResultSet rs = pstat.executeQuery();
            if(!rs.first())
                throw new Exception("No such Catchup Math login key: " + key);
            
    
            HaLoginInfo login = new HaLoginInfo();
            login.setKey(key);
            login.setType(rs.getString("user_type"));
            login.setUserId(rs.getInt("user_id"));
            
            return login;
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }
    
    
    static public void main(String as[]) {
        try {
            //HaBasicUser user = HaUserFactory.loginToCatchup("casey_test1","casey_test1");
            HaBasicUser user = HaUserFactory.createDemoUser();
            HaLoginInfo info = new HaLoginInfo(user);
            
            System.out.println(info);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}


