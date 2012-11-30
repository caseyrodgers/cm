package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class FixLessonId {
    public void doIt() {
        Connection conn=null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            conn = HMConnectionPool.getConnection();

            String sql = "select run_id, id from HA_TEST_RUN_LESSON";
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
                
            int oldRunId = 0;

            String sql2 = "select id, lid, run_id from HA_TEST_RUN_LESSON_PID_FIX where run_id = ? order by lid";
            String sql3 = "update HA_TEST_RUN_LESSON_PID_FIX set lid = ? where id = ?";

            List<Integer> lidList = new ArrayList<Integer>();
            int counter = 1;
            while (rs.next()) {
            	int runId = rs.getInt("run_id");
			    if (runId != oldRunId && oldRunId > 0) {
	            	//if (counter++ == 10) break;
                	System.out.println("counter: " + counter++);
	  				System.out.println("lidList: " + lidList.size());
	     			System.out.println("oldRunId: " + oldRunId);
    
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
					    }
					    oldLid = lid;
					    // reassign lid
					    System.out.println("change lid from: " + lid + " to "
						  	+ newLid);
					    ps3.setInt(1, newLid);
					    ps3.setInt(2, rs2.getInt("id"));
					    ps3.executeUpdate();
				    }
				    lidList = new ArrayList<Integer>();
			    }
				oldRunId = runId;
			    lidList.add(rs.getInt("id"));
		    }

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
            FixLessonId fli = new FixLessonId();
            fli.doIt();
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    	
}
