package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class HaTestRun {

    static Logger __logger = Logger.getLogger(HaTestRun.class);
    
    Integer runId;
    Long runTime;
    HaTest haTest;
    int answeredCorrect, answeredIncorrect;
    int sessionNumber;
    boolean isPassing;

    public boolean isPassing() {
        return isPassing;
    }

    public void setPassing(boolean isPassing) {
        this.isPassing = isPassing;
    }

    public int getSessionNumber() {
        return this.sessionNumber;
    }

    public void setSessionNumber(final Connection conn, int sn) throws Exception {
        this.sessionNumber = sn;
        PreparedStatement pstat = null;
        try {
            String sql = "update HA_TEST_RUN set run_session = ? where run_id = ?";
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, sn);
            pstat.setInt(2, this.runId);
            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Could not update session_number for run_id = " + this.runId);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    public int getAnsweredCorrect() {
        return answeredCorrect;
    }

    public void setAnsweredCorrect(int answeredCorrect) {
        this.answeredCorrect = answeredCorrect;
    }

    public int getAnsweredIncorrect() {
        return answeredIncorrect;
    }

    public void setAnsweredIncorrect(int answeredIncorrect) {
        this.answeredIncorrect = answeredIncorrect;
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    public Long getRunTime() {
        return runTime;
    }

    public void setRunTime(Long runTime) {
        this.runTime = runTime;
    }

    public HaTest getHaTest() {
        return haTest;
    }

    public void setHaTest(HaTest haTest) {
        this.haTest = haTest;
    }

    List<HaTestRunResult> results = new ArrayList<HaTestRunResult>();

    public List<HaTestRunResult> getTestRunResults() {
        return results;
    }

    /**
     * transfer data from HA_TEST_RUN_CURRENT into HA_TEST_RUN as to persist
     * with this run. This is to be able to include the selected indexes for
     * each question, otherwise we only have the number of correct/wrong.
     * 
     * @param conn
     * @throws Exception
     */
    public void transferCurrentToTestRun(final Connection conn) throws Exception {
        removeAllQuizResponses(conn);

        List<HaTestRunResult> currentSelections = getHaTest().getTestCurrentResponses(conn);
        for (HaTestRunResult tr : currentSelections) {
            addRunResult(conn, tr.getPid(), tr.getResult(),tr.getResponseIndex());
        }
    }

    /** Remove any current results for this test run
     * 
     * @TODO: why is this needed?  Is this not a new (empty) run?
     * 
     * @param conn
     * @throws Exception
     */
    private void removeAllQuizResponses(Connection conn) throws Exception {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("delete from HA_TEST_RUN_RESULTS where run_id = " + getRunId());
        } finally {
            SqlUtilities.releaseResources(null, stmt,null);
        }
    }

    public HaTestRunResult addRunResult(final Connection conn, String pid, String answerStatus, int answerIndex) throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "insert into HA_TEST_RUN_RESULTS(run_id, pid, answer_status, answer_index)values(?,?,?,?)";
            pstat = conn.prepareStatement(sql);
            HaTestRunResult testRun = new HaTestRunResult();

            pstat.setInt(1, this.runId);
            pstat.setString(2, pid);
            pstat.setString(3, answerStatus);
            pstat.setInt(4, answerIndex);

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Could not create new test run result for: " + runId);

            int autoIncKeyFromApi = -1;

            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                autoIncKeyFromApi = rs.getInt(1);
            } else {
                throw new HotMathException("Error creating PK for test");
            }

            testRun.setResultId(autoIncKeyFromApi);
            testRun.setResult(answerStatus);
            testRun.setPid(pid);

            this.results.add(testRun);

            return testRun;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error adding run result: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    /**
     * Return comma separated list of pids that represent the incorrect answers
     * for this test run
     * 
     * @return
     */
    public String getPidList() {
        String pids = "";
        for (HaTestRunResult r : results) {
            if(r.isCorrect())
                continue;
            
            if (pids.length() > 0)
                pids += ",";
            pids += r.getPid();
        }
        return pids;
    }

    public String toString() {
        return runId + "," + runTime + ", " + haTest;
    }

    static public HaTestRun lookupTestRun(final Connection conn, int runId) throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "select t.*, r.*, s.pid, s.answer_status, s.answer_index, s.rid, r.is_passing "
                    + " from   HA_TEST_RUN r INNER JOIN HA_TEST t on r.test_id = t.test_id "
                    + " LEFT JOIN HA_TEST_RUN_RESULTS s on s.run_id = r.run_id " + " where r.run_id = ? ";

            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);

            rs = pstat.executeQuery();
            if (!rs.first())
                throw new Exception("No such test run: " + runId);

            HaTestRun testRun = new HaTestRun();
            testRun.setRunId(runId);
            testRun.setRunTime(rs.getTimestamp("run_time").getTime());
            testRun.setSessionNumber(conn, rs.getInt("run_session"));
            testRun.setAnsweredCorrect((rs.getInt("answered_correct")));
            testRun.setPassing(rs.getInt("is_passing")==0?false:true);

            testRun.setHaTest(HaTest.loadTest(conn,rs.getInt("test_id")));
            do {
                String pid = rs.getString("pid");
                if (pid == null)
                    continue;

                HaTestRunResult result = new HaTestRunResult();
                result.setPid(pid);
                result.setResult(rs.getString("answer_status"));
                result.setResultId(rs.getInt("rid"));
                result.setResponseIndex(rs.getInt("answer_index"));

                testRun.results.add(result);
            } while (rs.next());
            return testRun;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error adding run result: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }
    
    
    /** Update the total_sessions for all test runs
     * 
     * @param as
     */
    static public void main(String as[]) {
        
        __logger.info("Updating HA_TEST_RUN total_sessions");
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("VALID_TEST_RUNS");
            	
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while(rs.next()) {
                int runId = rs.getInt("run_id");
                
                __logger.info("Processing: " + runId);
                HaTest.updateTestRunSessions(conn, runId);
            }
            __logger.info("Completed updating HA_TEST_RUN");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        SqlUtilities.releaseResources(null,null,conn);
        
    }
}
