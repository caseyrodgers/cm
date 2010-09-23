package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

public class CmTestUtils {
    
    
    /** Create demo user and assign to name program
     * 
     * @return
     * @throws Exception
     */
    static public int setupDemoAccount(CmProgram program) throws Exception {
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            HaBasicUser user = HaUserFactory.createDemoUser();

            new CmStudentDao().assignProgramToStudent(conn, user.getUserKey(),program,null);
            
            return user.getUserKey();
            
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }

}
