package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.DbcpException;

public class HaTestRun {

    Integer runId;
    Long runTime;
    HaTest haTest;
    int answeredCorrect, answeredIncorrect;
    int sessionNumber;

    public int getSessionNumber() {
        return this.sessionNumber;
    }

    public void setSessionNumber(int sn) throws Exception {
        this.sessionNumber = sn;

        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "update HA_TEST_RUN set run_session = ? where run_id = ?";
            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, sn);
            pstat.setInt(2, this.runId);
            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Could not update session_number for run_id = " + this.runId);
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
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
    public void transferCurrentToTestRun() throws Exception {
        removeAllQuizResponses();

        List<HaTestRunResult> currentSelections = getHaTest().getTestCurrentResponses();
        for (HaTestRunResult tr : currentSelections) {
            addRunResult(tr.getPid(), tr.getResult(), tr.getResponseIndex());
        }
    }

    private void removeAllQuizResponses() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = HMConnectionPool.getConnection();
            stmt = conn.createStatement();
            stmt.execute("delete from HA_TEST_RUN_RESULTS where run_id = " + getRunId());
        } finally {
            SqlUtilities.releaseResources(null, stmt, conn);
        }
    }

    public HaTestRunResult addRunResult(String pid, String answerStatus, int answerIndex) throws HotMathException {
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "insert into HA_TEST_RUN_RESULTS(run_id, pid, answer_status, answer_index)values(?,?,?,?)";
            conn = HMConnectionPool.getConnection();
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
            ResultSet rs = null;
            try {
                rs = pstat.getGeneratedKeys();
                if (rs.next()) {
                    autoIncKeyFromApi = rs.getInt(1);
                } else {
                    throw new HotMathException("Error creating PK for test");
                }
            } finally {
                rs.close();
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
            SqlUtilities.releaseResources(null, pstat, conn);
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

    static public HaTestRun lookupTestRun(int runId) throws HotMathException {
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            String sql = "select t.*, r.*, s.pid, s.answer_status, s.answer_index, s.rid "
                    + " from   HA_TEST_RUN r INNER JOIN HA_TEST t on r.test_id = t.test_id "
                    + " LEFT JOIN HA_TEST_RUN_RESULTS s on s.run_id = r.run_id " + " where r.run_id = ? ";

            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);

            ResultSet rs = pstat.executeQuery();
            if (!rs.first())
                throw new Exception("No such test run: " + runId);

            HaTestRun testRun = new HaTestRun();
            testRun.setRunId(runId);
            testRun.setRunTime(rs.getTimestamp("run_time").getTime());
            testRun.setSessionNumber(rs.getInt("run_session"));
            testRun.setAnsweredCorrect((rs.getInt("answered_correct")));

            testRun.setHaTest(HaTest.loadTest(rs.getInt("test_id")));
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
            SqlUtilities.releaseResources(null, pstat, conn);
        }
    }
}
