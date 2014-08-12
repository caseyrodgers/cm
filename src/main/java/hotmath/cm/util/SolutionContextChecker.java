package hotmath.cm.util;

import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.SolutionDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import sb.util.SbException;
import sb.util.SbTestImpl;



/** checks for existence of mobile videos for
 *  all prescriptions.
 *
 * @author casey
 *
 */
public class SolutionContextChecker implements SbTestImpl {

    public SolutionContextChecker() {}

    PreparedStatement _psLog;

    public void runTests() {

        String sql = "select * from HA_TEST_RUN order by run_id desc limit 10000";
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();

            // executeSilent(conn, "drop table junk_context");
            executeSilent(conn, "create table junk_context(aid integer, uid integer, rid integer, pid varchar(100), context text, check_time datetime)");
            _psLog = conn.prepareStatement("insert into junk_context(aid, uid, rid, pid, context, check_time)values(?,?,?,?,?, now())");
            ResultSet rs  = conn.createStatement().executeQuery(sql);
            while(rs.next()) {
                int runId = rs.getInt("run_id");

                //System.out.println("Checking run_id: " +    runId);
                checkForCorruptedSolutionContexts(conn, runId);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null,conn);
        }
    }

    private void executeSilent(Connection conn, String string) {
        try {
            conn.createStatement().executeUpdate(string);
        }
        catch(Exception e) {
            // silent
        }

    }

    private void checkForCorruptedSolutionContexts(Connection conn, int runId) throws Exception {
    	
    	
    	if(true) {
    		checkForOldContexts(conn, runId);
    	}
    	
        PreparedStatement ps=null;
        try {
            String sql = "select * from HA_SOLUTION_CONTEXT where run_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                byte[] variables = rs.getBytes("variables");
                String uncompressedContext = SolutionDao.getInstance().convertSolutionContext(variables);
                if(contextIsCorrupted(uncompressedContext)) {
                    HaTestRun tr = HaTestRunDao.getInstance().lookupTestRun(runId);
                    HaUser u = tr.getHaTest().getUser();
                    _psLog.setInt(1,  u.getAid());
                    _psLog.setInt(2,  u.getUserKey());
                    _psLog.setInt(3,  runId);
                    _psLog.setString(4,  rs.getString("pid"));
                    _psLog.setString(5, uncompressedContext);

                    _psLog.executeUpdate();

                    String sqldel = "delete from HA_SOLUTION_CONTEXT where run_id = " + runId + " and pid = '" + rs.getString("pid") + "'";
                    conn.createStatement().executeUpdate(sqldel);


                    System.out.println(new java.util.Date() + ", " + tr.getHaTest().getUser().getAid() + ", " + tr.getHaTest().getUser().getUserKey() +  ", " + rs.getInt("run_id") + ", " + rs.getString("pid"));
                    System.out.println(uncompressedContext + "\n\n");
                }
            }

        }
        finally {
            SqlUtilities.releaseResources(null,  ps, null);
        }
    }

    
    private void checkForOldContexts(Connection conn, int runId) throws Exception {
        PreparedStatement ps=null;
        int countHave=0;
        int countNotHave=0;
        
        Date minViewDate=null;
        try {
            String sql = "select * from HA_SOLUTION_CONTEXT where run_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {

            	Date viewDate=null;
                if(rs.getDate("time_viewed") != null) {
                    viewDate = rs.getDate("time_viewed");
                }
                
                if(viewDate != null) {
                	if(minViewDate == null || (viewDate.getTime() < minViewDate.getTime())) {
                		minViewDate = viewDate;
                	}
                }

                byte[] variables = rs.getBytes("variables");
                String uncompressedContext = SolutionDao.getInstance().convertSolutionContext(variables);
                
                
                if(uncompressedContext.contains("tutor_data_record")) {
                	countHave++;
                }
                else {
                	countNotHave++;
                }
            }

        }
        finally {
            SqlUtilities.releaseResources(null,  ps, null);
        }
        
        if(countNotHave > 0) {
        	System.out.println("tutor_data_record attribute: run_id:  " + runId + ", Have: " + countHave + ", Not Have: " + countNotHave + ", min_view: " + minViewDate);
        }
        
    }
    
    static final String UNDEFINED="dW5kZWZpbmV";
    private boolean contextIsCorrupted(String uncompressedContext) throws Exception {

        JSONObject json = new JSONObject(uncompressedContext);
        JSONArray vars = json.getJSONArray("_variables");

        if(vars.length() < 2) {
            return false;
        }

        for(int i=0;i<vars.length();i++) {
            JSONObject var = (JSONObject)vars.get(i);
            String val = var.getString("value");
            if(val.length() == 0 || val.contains(UNDEFINED)) {
                // not set or corrupted.
            }
            else {
                // is not corrupted ... must be OK
                return false;
            }
        }

        /** all vars are undefined/corrupted */
        return true;
    }

    @Override
    public void doTest(Object objLogger, String sFromGUI) throws SbException {
        runTests();
    }



    public static void main(String as[]) {
        try {
            System.out.println("SolutionContextChecker: look for invalid/corrupt solution contexts (writes to db table: junk_context)");
            new SolutionContextChecker().runTests();

            System.out.println("Test Complete");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
