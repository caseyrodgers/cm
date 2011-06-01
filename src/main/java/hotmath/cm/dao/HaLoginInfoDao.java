package hotmath.cm.dao;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionLoginAlreadyConsumed;
import hotmath.gwt.shared.client.util.CmExceptionLoginInvalid;
import hotmath.testset.ha.HaUserExtendedDao;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class HaLoginInfoDao {
    
    public HaLoginInfoDao(){}

    
    public HaLoginInfo getLoginInfo(final Connection conn, HaBasicUser user, String browserInfo, boolean isRealLogin) throws Exception {
        
        HaLoginInfo loginInfo = new HaLoginInfo();
        loginInfo.setKey(addLoginInfo(conn, user, browserInfo,isRealLogin));
        loginInfo.setUserId(user.getUserKey());
        loginInfo.setType(user.getUserType().toString());
        loginInfo.setLoginName(user.getLoginName());
        return loginInfo;
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
    

    static int __login_key_uniquer=0;
    
    /** 
     *  Create new login record.
     *  
     * @param conn
     * @param user
     * @param browserInfo
     * @param isRealLogin  Is this login a real login from the login page.
     * @return
     * @throws Exception
     */
    public String addLoginInfo(final Connection conn, HaBasicUser user, String browserInfo, boolean isRealLogin) throws Exception {
        PreparedStatement pstat = null;

        try {
            /** during login storm we can have duplicates on mills alone
             *  so we use the static uniquer
             * 
             * TODO: make thread-safe.
             */
            String key = "cm_" + System.currentTimeMillis() + "_" + (__login_key_uniquer++);
            
            // first see if user is in admin
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_LOGIN_INFO");
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, user.getUserKey());
            pstat.setString(2, key);
            pstat.setString(3, user.getUserType().toString());
            pstat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstat.setString(5,user.getLoginName());
            pstat.setString(6, browserInfo);
            pstat.setInt(7, isRealLogin?1:0);
    
            if(pstat.executeUpdate() != 1)
                throw new Exception("could not not insert new HA_USER_LOGIN record");
            
            if (user.getUserType() == UserType.STUDENT) {
                HaUserExtendedDao.updateUserExtendedLastLogin(conn, user.getUserKey());
            }
            
            return key;
            
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }
  
}
