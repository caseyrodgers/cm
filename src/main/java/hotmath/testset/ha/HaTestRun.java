package hotmath.testset.ha;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class HaTestRun {

    static Logger __logger = Logger.getLogger(HaTestRun.class);
    
    Integer runId;
    Long runTime;
    HaTest haTest;
    int answeredCorrect, answeredIncorrect, notAnswered;
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
    	HaTestRunDao dao = HaTestRunDao.getInstance();
    	dao.setSessionNumber(conn, runId, sn);
    	this.sessionNumber = sn;
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
    
    

    public int getNotAnswered() {
        return notAnswered;
    }

    public void setNotAnswered(int notAnswered) {
        this.notAnswered = notAnswered;
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
     * @param ignoreUnanswered is true, then only actually answered questions are used 
     *        to create the prescription.
     * @throws Exception
     */
    public void transferCurrentToTestRun(final Connection conn) throws Exception {
        HaTestRunDao dao = HaTestRunDao.getInstance();

        // @TODO: why is this needed?  Is this not a new (empty) run?
        dao.removeAllQuizResponses(conn, this.runId);

        List<HaTestRunResult> currentSelections = HaTestDao.getTestCurrentResponses(conn, getHaTest().getTestId());
 
        for (HaTestRunResult tr : currentSelections) {
            this.results.add(dao.addRunResult(conn, this.runId, tr.getPid(), tr.getResult(), tr.getResponseIndex()));
        }
    }

    /**
     * Return comma separated list of pids that represent the incorrect answers
     * for this test run
     * 
     * @return
     */
    public String getPidList() {
    	StringBuilder sb = new StringBuilder();
        for (HaTestRunResult r : results) {
            if(r.isCorrect())
                continue;
            
            if (sb.length() > 0)
                sb.append(",");
            sb.append(r.getPid());
        }
        return sb.toString();
    }

    public String toString() {
        return runId + "," + runTime + ", " + haTest + ", pidList: " + getPidList();
    }
}
