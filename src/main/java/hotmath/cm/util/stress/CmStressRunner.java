package hotmath.cm.util.stress;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


public class CmStressRunner {

    private int count;
    private int delay;
    private String testClassName;
    private int adminId;
    
    static Logger __logger = Logger.getLogger(CmStressRunner.class);

    public interface CallBack {
        void onEnd();
    }
    
    public CmStressRunner(int adminId, int count, int delay, String testClassName) throws ClassNotFoundException {
        this.adminId = adminId;
        this.count = count;
        this.delay = delay;
        this.testClassName = testClassName!=null?testClassName:CmStressLoginAndGetActiveFlow.class.getName();
        
        
        // error early
        Class.forName(this.testClassName);
        
    }

    public void runTests() {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "select a.user_name, u.user_passcode,u.uid,u.admin_id " + "from HA_ADMIN a "
                    + " JOIN SUBSCRIBERS s on s.id = a.subscriber_id " + " JOIN HA_USER u on u.admin_id = a.aid "
                    + " JOIN CM_USER_PROGRAM p on p.id = u.user_prog_id " 
                    + " where u.is_active = 1 " 
                    + " and u.admin_id != 13 " + " and u.date_created > '2010-11-01' "
                    + " and is_auto_create_template = 0 ";
            if(adminId > 0) {
                sql += " and a.aid = " + adminId;
            }
            
            sql  += " order by rand() " + " limit " + count;

            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uName = rs.getString("user_name");
                String uPass = rs.getString("user_passcode");
                int uid = rs.getInt("uid");
                int aid = rs.getInt("admin_id");
                
                new CmStressThread(aid, uid, uName, uPass,delay, this.testClassName).runTest();
            }

        } catch (Exception e) {
            __logger.error("Error running stress tests", e);
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }

    }

}

