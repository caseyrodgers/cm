<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="hotmath.testset.ha.HaTest"%>
<%@page import="sb.util.SbUtilities"%><html>
<%@page import="hotmath.util.sql.*, hotmath.util.*, hotmath.cm.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>


<html>
<%
	/** resets lid in HA_TEST_RUN_LESSON_PID to match HA_TEST_RUN_LESSON
       */
       Logger __logger = Logger.getRootLogger();

       String endVal = request.getParameter("endVal");
       String startVal = request.getParameter("startVal");
       
       int start = 0;
       if (startVal != null) {
    	   start = Integer.parseInt(startVal); 
       }
       int end = 0;
       if (endVal != null) {
    	   end = Integer.parseInt(endVal); 
       }

        __logger.info("Updating HA_TEST_RUN_LESSON_PID lid");
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
			    	counter++;
                	__logger.info("counter: " + counter);
	  				__logger.info("lidList: " + lidList.size());
	     			__logger.info("oldRunId: " + oldRunId);
    
	     			if (counter < start) {
	     				oldRunId = runId;
	     				continue;
	     			}
	     			
	     			if (counter > end) break;
	     			
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
						    __logger.info("changing lid from: " + lid + " to "
								  	+ newLid);
					    }
					    oldLid = lid;

					    // reassign lid
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
	    	__logger.error(e);
	    } finally {
		    SqlUtilities.releaseResources(null, ps3, null);
		    SqlUtilities.releaseResources(rs2, ps2, null);
		    SqlUtilities.releaseResources(rs, ps, conn);
	    }
	    __logger.info("Completed updating HA_TEST_RUN_LESSON_PID_FIX");
%>
  <body>
    <h1>
     Test Run Lesson Pid LID Updated
    </h1>
  </body>
</html>
