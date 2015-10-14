package hotmath.cm.dao;

import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionLoginAlreadyConsumed;
import hotmath.gwt.shared.client.util.CmExceptionLoginInvalid;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaUserExtendedDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class HaLoginInfoDao extends SimpleJdbcDaoSupport {
    
    private static HaLoginInfoDao __instance;
    public static HaLoginInfoDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (HaLoginInfoDao)SpringManager.getInstance().getBeanFactory().getBean(HaLoginInfoDao.class.getName());
        }
        return __instance;
    }
    
    static Logger __logger = Logger.getLogger(HaLoginInfoDao.class);
    
    private HaLoginInfoDao(){}

    
    public HaLoginInfo getLoginInfo(final Connection conn, HaBasicUser user, String key, ClientEnvironment clientEnv, boolean isRealLogin) throws Exception {
        
        HaLoginInfo loginInfo = new HaLoginInfo();
        loginInfo.setKey(key == null ? addLoginInfo(conn, user, clientEnv,isRealLogin): key);
        loginInfo.setUserId(user.getUserKey());
        loginInfo.setType(user.getUserType().toString());
        loginInfo.setLoginName(user.getLoginName());
        return loginInfo;
    }    

  
    /** Return the latest login key for this user.
     * 
     *  This will be the 'active' login for this user.
     *  
     * @param uid
     * @return
     * @throws Exception
     */
    public String getLatestLoginKey(int uid) throws Exception {
        String key=null;
        if(uid > 0) {
            List<String> list = getJdbcTemplate().query(
                    CmMultiLinePropertyReader.getInstance().getProperty("GET_LOGIN_INFO_KEY"),
                    new Object[]{uid},
                    new RowMapper<String>() {
                        @Override
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getString("login_key");
                        }
                    });
            if(list.size() > 0) {
                key = list.get(0);
            }
        }
        return key;
    }
    
    
    /** Reads login information for key
     *  Throws exception if not found, or invalid.
     *  
     *  After a successful read of the security key, the row is
     *  marked as is_consumed.  Any further attempt to get this
     *  login info will result in a CmExceptionLoginAlreadyConsumed being thrown.
     *  
     *  
     * @param key
     * @return
     * @throws CmExceptionLoginInvalid
     */
    public HaLoginInfo getLoginInfo(final Connection conn, String key) throws Exception {
        
        PreparedStatement pstat = null;
        try {
            // first see if user is in admin
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_LOGIN_INFO");
                
            pstat = conn.prepareStatement(sql);

            pstat.setString(1, key);
            ResultSet rs = pstat.executeQuery();
            if(!rs.first())
                throw new CmExceptionLoginAlreadyConsumed("No such Catchup Math login key: " + key);
            
//            if(rs.getInt("is_consumed") == 1) {
//                throw new CmExceptionLoginAlreadyConsumed("Security key is invalid: " + key);
//            }
    
            /** Mark this record as is_consumed, to disallow future logins
             * 
             */
            PreparedStatement stmt=null;
            try {
                String usql = CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_LOGIN_INFO");
                stmt = conn.prepareStatement(usql);
                stmt.setInt(1, rs.getInt("lid"));
                int res = stmt.executeUpdate();
                if(res != 1) {
                    throw new CmException("Could not update is_consumed for key: " + key);
                }
            }
            finally {
                stmt.close();
            }
            
            HaLoginInfo login = new HaLoginInfo();
            login.setKey(key);
            login.setType(rs.getString("user_type"));
            login.setUserId(rs.getInt("user_id"));
            login.setIsConsumed(rs.getInt("is_consumed")==0?false:true); 
            login.setLoginName(rs.getString("login_name"));
            
            return login;
        }
        finally {
            SqlUtilities.releaseResources(null, pstat,null);
        }
    }    
    
    /** 
     *  Create new login record.
     *  
     * @param conn
     * @param user
     * @param browserInfo
     * @param isRealLogin  Is this login a real login from the login page.
     * @param ClientEnvironment the client enviornment.
     * @return
     * @throws Exception
     */
    public String addLoginInfo(final Connection conn, final HaBasicUser user, ClientEnvironment clientEnv, boolean isRealLogin) throws Exception {
        PreparedStatement pstat = null;

        try {
            /** during login storm we can have duplicates on mills alone
             *  so we use the unique id
             * 
             */
        	String key="cm_" + RandomUtil.genRandom32Hex();
             
            // first see if user is in admin
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_LOGIN_INFO");
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, user.getUserKey());
            pstat.setString(2, key);
            pstat.setString(3, user.getUserType().toString());
            pstat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstat.setString(5,user.getLoginName());
            pstat.setString(6, clientEnv!=null?clientEnv.getUserAgent():"unknown");
            pstat.setInt(7, isRealLogin?1:0);
            pstat.setInt(8, (clientEnv != null && clientEnv.isFlashEnabled())?0:1);
    
            if(pstat.executeUpdate() != 1)
                throw new Exception("could not not insert new HA_USER_LOGIN record");
            
            if (user.getUserType() == UserType.STUDENT) {
                
                /** Update the extended info is separate thread to release any hold
                 * during update lock.
                 *
                 */
//                new Thread() {
//                    public void run() {
//                        try {
//                            HaUserExtendedDao.updateUserExtendedLastLogin(user.getUserKey());
//                        }
//                        catch(Exception e2) {
//                            __logger.error("Error updating extended data for: " + user);
//                        }
//                    }
//                }.start();
                HaUserExtendedDao.updateUserExtendedLastLogin(user.getUserKey());
            }
            
            return key;
            
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }


    public void addUserDevice(int uid, String deviceToken) throws Exception {
        
        Connection conn=null;
        PreparedStatement ps=null;
        try {
            conn = HMConnectionPool.getConnection();
            
            conn.createStatement().executeUpdate("delete from HA_USER_DEVICE where uid = " + uid);
            String sql = "insert into HA_USER_DEVICE(uid, device_token,register_time)values(?,?, now())";
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1,  uid);
            ps.setString(2,  deviceToken);
            if(ps.executeUpdate() != 1) {
                __logger.warn("Could not add user device record: " + uid + ", " + deviceToken);
            }
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        
        
    }
    
    
  
}


class RandomUtil {
    // Maxim: Copied from UUID implementation :)
    private static volatile SecureRandom numberGenerator = null;
    private static final long MSB = 0x8000000000000000L;

    public static String genRandom32Hex() {
        SecureRandom ng = numberGenerator;
        if (ng == null) {
            numberGenerator = ng = new SecureRandom();
        }

        return Long.toHexString(MSB | ng.nextLong()) + Long.toHexString(MSB | ng.nextLong());
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
        System.out.println(RandomUtil.genRandom32Hex());

        System.out.println();
        System.out.println(Long.toHexString(0x8000000000000000L |21));
        System.out.println(Long.toBinaryString(0x8000000000000000L |21));
        System.out.println(Long.toHexString(Long.MAX_VALUE + 1));
    }
}