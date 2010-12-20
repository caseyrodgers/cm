<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="hotmath.testset.ha.HaTest"%>
<%@page import="sb.util.SbUtilities"%><html>
<%@page import="hotmath.util.sql.*, hotmath.util.*, hotmath.cm.util.*"%>
<html>
<%
       /** Rebuilds all prescriptions forcing the various
           prescription ionformation tables to be generated
           such as HA_TEST_RUN_LESSON and PID
       */
       Logger __logger = Logger.getRootLogger();

        __logger.info("Updating HA_TEST_RUN total_sessions");
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("VALID_TEST_RUNS");
            	
            String rid = request.getParameter("run_id");
            if(rid != null) {
                sql += " where run_id = " + rid;
            }
            
            sql = SbUtilities.replaceSubString(sql,"\n", " ");
            __logger.info("Processing cm_fix_test_runs: " + sql); 
            
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while(rs.next()) {
                int runId = rs.getInt("run_id");
                
                __logger.info("Processing: " + runId);
                try {
                    HaTest.updateTestRunSessions(conn, runId);
                }
                catch(Exception e) {
                    __logger.info("error processing: " + e.getMessage());
                }
            }
            __logger.info("Completed updating HA_TEST_RUN");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        SqlUtilities.releaseResources(null,null,conn);
%>
  <body>
    <h1>
     Test Runs Updated
    </h1>
  </body>
</html>
