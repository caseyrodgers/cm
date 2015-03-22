package hotmath.cm.server.rest;

import hotmath.gwt.cm_rpc.client.rpc.GetCm2QuizHtmlAction;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;

/** Central place to request a CM2 request
 *  with any specialized formatting required.
 *  
 * @author casey
 *
 */
public class Cm2ActionManager {

    public static String getUserProgram(int userId) throws Exception {
        // new         // 678549
        // GetCmProgramFlowAction flowAction = new GetCmProgramFlowAction(userId, FlowType.ACTIVE);
        // GetUserSyncAction syncAction = new GetUserSyncAction(678549);
        
        int randomTestId = getRandomTestId();
        GetCm2QuizHtmlAction action = new GetCm2QuizHtmlAction(randomTestId);
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        return jsonResponse;
    }

    private static int getRandomTestId() throws Exception {
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            String sql = 
                    " select t.test_id, count(*) as cnt_problems " +
                    " from   HA_TEST t     " +
                    "    JOIN HA_USER u on u.uid = t.user_id " +    
                    "    JOIN HA_ADMIN a on a.aid = u.admin_id   " +
                    "    JOIN HA_TEST_IDS ti on ti.test_id = t.test_id " +
                    " where a.aid in (2, 216, 5) and u.is_active = 1   "  + 
                    " group by t.test_id " +
                    " order by rand()  " +
                    " limit 1 ";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            rs.next();
            int testId = rs.getInt("test_id");
            return testId;
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

}
