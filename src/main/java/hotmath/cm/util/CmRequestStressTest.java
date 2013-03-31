package hotmath.cm.util;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.GetUserSyncAction;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import sb.util.SbUtilities;

/** attempt to stress test the request infrastructure
 * 
 * @author casey
 *
 */
public class CmRequestStressTest extends Thread {
    
    int uid;
    int loops;
    
    public CmRequestStressTest(int uid, int loops) {
        this.uid = uid;
        this.loops = loops;
    }
    
    public void startTest() {
        System.out.println("Starting test: " + this.uid);
        
        
        start();
    }
    
    @Override
    public void run() {
        
        try {
            
            GetUserSyncAction action = new GetUserSyncAction(uid);
             UserSyncInfo results = ActionDispatcher.getInstance().execute(action);
            
            System.out.println("UID: " + this.uid + ", COUNT: " + results.getAssignmentInfo());
           
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    

    static public void main(String as[]) {
        int cnt = SbUtilities.getInt(as[0]);

        List<Integer> uids = new ArrayList<Integer>();
        /** get n random UIDS */
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("select uid from HA_USER order by rand() limit " + cnt);
            while(rs.next()) {
                uids.add(rs.getInt("uid"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }

        for(int i=0;i<cnt;i++) {
            new CmRequestStressTest(uids.get(i), 10).startTest();
        }
    }
}
