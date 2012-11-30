package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class FixLessonId {

    public void doIt(int start, int limit) {
        Connection conn=null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            conn = HMConnectionPool.getConnection();

            String sql = "select run_id, id from HA_TEST_RUN_LESSON where id >= " + start + " limit " + limit;

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            int oldRunId = 0;

            String sql2 = "select id, lid, run_id from HA_TEST_RUN_LESSON_PID_FIX where run_id = ? order by lid";
            String sql3 = "update HA_TEST_RUN_LESSON_PID_FIX set lid = ? where id = ?";

            List<Integer> lidList = new ArrayList<Integer>();
            int counter = 1;
            int lastId = 0 ;
            while (rs.next()) {

            	int runId = rs.getInt("run_id");
            	lastId = rs.getInt("id");

            	if (runId != oldRunId && oldRunId > 0) {
                    if (counter++%1000 == 0) {
                        System.out.println("counter: " + counter);
                        System.out.println("lidList: " + lidList.size());
                        System.out.println("oldRunId: " + oldRunId);
                    }

                    ps2 = conn.prepareStatement(sql2);
                    ps3 = conn.prepareStatement(sql3);
                    ps2.setInt(1, oldRunId);
                    rs2 = ps2.executeQuery();

                    int oldLid = 0;
                    int newLid = 0;
                    int idx = 0;
                    while (rs2.next()) {
                        int lid = rs2.getInt("lid");
                        if (oldLid != lid) {
                            if (idx == lidList.size()) {
                                // reset
                                idx = 0;
                             }
                            newLid = lidList.get(idx++);
                            //System.out.println("changing lid from: " + lid + " to "
                            //          + newLid);
                        }
                        oldLid = lid;

                        // reassign lid
                        ps3.setInt(1, newLid);
                        ps3.setInt(2, rs2.getInt("id"));
                        if (ps3.executeUpdate() != 1) {
                        	System.out.println("COULD NOT UPDATE RECORD: id: " + rs2.getInt("id"));
                        }
                    }
                    lidList = new ArrayList<Integer>();
                }
                oldRunId = runId;
                lidList.add(rs.getInt("id"));
            }
            System.out.println("lastId: " + lastId);
        } catch (Exception e) { 
	    	e.printStackTrace();
	    } finally {
		    SqlUtilities.releaseResources(null, ps3, null);
		    SqlUtilities.releaseResources(rs2, ps2, null);
		    SqlUtilities.releaseResources(rs, ps, conn);
	    }
	    System.out.println("Completed updating HA_TEST_RUN_LESSON_PID_FIX");
	}
    
    static public void main(String as[]) {
        try {
            int start=Integer.parseInt(as[0]);
            int limit=Integer.parseInt(as[1]);

            FixLessonId fli = new FixLessonId();
            fli.doIt(start, limit);
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    	
}
