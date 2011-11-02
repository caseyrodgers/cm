package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.RppWidget;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class HaTestRunDao extends SimpleJdbcDaoSupport {

    static private HaTestRunDao __instance;

    static public HaTestRunDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (HaTestRunDao) SpringManager.getInstance().getBeanFactory().getBean("haTestRunDao");
        }
        return __instance;
    }

    private HaTestRunDao() {
        /** empty */
    }

    final static Logger __logger = Logger.getLogger(AssessmentPrescriptionSession.class);

    /**
     * Add all lesson names assigned to this prescription to the HaTestRunLesson
     * object. This table is used as a quick method of accessing the lessons
     * assigned to a testRun without having to recreate the prescription.
     * 
     * Serialize prescription information.
     * 
     */
    public void addLessonsToTestRun(final Connection conn, HaTestRun testRun,
            List<AssessmentPrescriptionSession> sessions) throws Exception {

        if (testRun.getRunId() == null)
            return;

        PreparedStatement pstat = null;
        try {
            conn.createStatement().executeUpdate("delete from HA_TEST_RUN_LESSON where run_id = " + testRun.getRunId());

            String sql = "insert into HA_TEST_RUN_LESSON(run_id, lesson_name, lesson_number, lesson_file) values(?, ?, ?, ?)";
            pstat = conn.prepareStatement(sql);
            for (int sn = 0, t = sessions.size(); sn < t; sn++) {
                AssessmentPrescriptionSession s = sessions.get(sn);

                if (s.getSessionItems().size() == 0)
                    continue;

                pstat.setInt(1, testRun.getRunId());
                pstat.setString(2, s.getTopic());
                pstat.setInt(3, sn);
                pstat.setString(4, s.getSessionCategories().get(0).getFile());

                if (pstat.executeUpdate() != 1)
                    throw new Exception("Could not save lesson record for unknown reason: " + testRun);

                int lid = SqlUtilities.getLastInsertId(conn);

                // write out each pid lesson references
                PreparedStatement pstat2 = null;
                try {
                    pstat2 = conn
                            .prepareStatement("insert into HA_TEST_RUN_LESSON_PID(pid, lid,config,run_id)values(?,?,?,?)");

                    for (SessionData sd : s.getSessionItems()) {
                        pstat2.setString(1, sd.getRpp().getFile());
                        pstat2.setInt(2, lid);
                        pstat2.setString(3, sd.getRpp().getWidgetJsonArgs());
                        pstat2.setInt(4, testRun.getRunId());

                        if (pstat2.executeUpdate() != 1)
                            throw new Exception("Could not add to HA_TEST_RUN_LESSON_PID for unknown reasons");
                    }
                } finally {
                    SqlUtilities.releaseResources(null, pstat2, null);
                }

            }
            /**
             * update HA_USER_EXTENDED
             * 
             * current_lesson = 0, lesson_count = sessions.size(),
             * lessons_completed = 0
             */
            HaUserExtendedDao.updateUserExtendedLessonStatus(conn, testRun.getRunId(), sessions.size());

        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Return the list of lessons in this test run along with their statues
     * 
     * @param runId
     * @return
     * @throws Exception
     */
    public List<SessionTopic> getLessonStatuses(final int runId) throws Exception {
        String sql = "select lesson_name,date_completed FROM HA_TEST_RUN_LESSON where run_id = ?";
        List<SessionTopic> list = getJdbcTemplate().query(sql, new Object[] { runId }, new RowMapper<SessionTopic>() {
            @Override
            public SessionTopic mapRow(ResultSet rs, int rowNum) throws SQLException {
                Date dc = rs.getDate("date_completed");
                SessionTopic st = new SessionTopic(rs.getString("lesson_name"), dc != null);
                st.setTopicStatus(getTopicStatus(runId, st.getTopic()));
                return st;
            }
        });

        return list;
    }

    private String getTopicStatus(int runId, String topic) {

        try {
            String sql = "select count(*) " + " from   HA_TEST_RUN_LESSON l "
                    + " JOIN HA_TEST_RUN_LESSON_PID p on (p.run_id = l.run_id and p.lid = l.id) " + " where l.run_id = ? "
                    + " and   l.lesson_name = ? " + " ";
            
            Integer rppTotal = getJdbcTemplate().queryForObject(sql, new Object[] { runId, topic },
                    new RowMapper<Integer>() {
                        @Override
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            // TODO Auto-generated method stub
                            return rs.getInt(1);
                        }
                    });
            
            sql = 
            " select lesson_name, l.id, u.item_file, count(u.use_id) " +
            " from HA_TEST_RUN_LESSON l " +
            " JOIN HA_TEST_RUN_LESSON_PID p on p.lid = l.id " +
            " JOIN HA_TEST_RUN_INMH_USE u on u.run_id = l.run_id and u.item_file = p.pid " +
            " where l.run_id = ? " +  
            " and l.lesson_name = ? " +
            " group by lesson_name, l.id, u.item_file ";
            
            List<Integer> rppCompletedList = getJdbcTemplate().query(sql, new Object[] { runId, topic },
                    new RowMapper<Integer>() {
                        @Override
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            // TODO Auto-generated method stub
                            return rs.getInt(2);
                        }
                    });
            
            

            int rppCompleted = rppCompletedList.size(); 

            return rppCompleted + " of " + rppTotal;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Return the complete current definition of this test run
     * 
     * This is created and stored during the prescription creation.
     * 
     * Can be used to recreate the prescription.
     * 
     * @see AssessmentPrescription
     * 
     * @return
     * @throws Exception
     * 
     */
    public List<TestRunLesson> loadTestRunLessonsAndPids(final Connection conn, Integer runId) throws Exception {

        List<TestRunLesson> lessons = new ArrayList<TestRunLesson>();
        PreparedStatement pstat = null;
        try {

            __logger.debug("Reading session data for run_id: " + runId);

            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LOAD_PRESCRIPTION");

            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);
            ResultSet rs = pstat.executeQuery();

            TestRunLesson trl = null;
            String lastLesson = "";
            while (rs.next()) {
                String lesson = rs.getString("lesson_name");
                String pid = rs.getString("pid");
                String config = rs.getString("config");

                if (!lastLesson.equals(lesson)) {
                    String file = rs.getString("lesson_file");
                    trl = new TestRunLesson(lesson, file);
                    lessons.add(trl);
                    lastLesson = lesson;
                }

                RppWidget rppWidget = null;
                if (config != null && config.length() > 0) {
                    rppWidget = new RppWidget(config);
                } else {
                    rppWidget = new RppWidget();
                    rppWidget.setFile(pid);
                }
                trl.getPids().add(rppWidget);
            }

            return lessons;
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
            __logger.debug("Session data read run_id: " + runId);
        }
    }

    public void setLessonViewed(final Connection conn, Integer runId, Integer lessonViewed) throws Exception {

        PreparedStatement pstat = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSON_SET_LESSON_VIEWED");

            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);
            pstat.setInt(2, lessonViewed);

            int updated = pstat.executeUpdate();
            if (updated != 1) {
                __logger.info("Could not update lesson viewed: " + pstat);
            }

            HaUserExtendedDao.updateUserExtendedCurrentLesson(conn, runId, lessonViewed + 1);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Return all lessons assigned to this run
     * 
     * @param conn
     * @param runId
     * @return
     * @throws Exception
     */
    public List<TestRunLessonModel> getTestRunLessons(final Connection conn, Integer runId) throws Exception {

        List<TestRunLessonModel> lessons = new ArrayList<TestRunLessonModel>();
        PreparedStatement pstat = null;
        PreparedStatement pstat2 = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSONS");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, runId);

            String currLesson = null;
            ResultSet rs = pstat.executeQuery();
            TestRunLessonModel im = null;
            while (rs.next()) {
                String lesson = rs.getString("lesson_name");
                if (currLesson == null || !currLesson.equals(lesson)) {
                    im = new TestRunLessonModel(lesson, rs.getString("lesson_file"), rs.getDate("lesson_viewed"),
                            rs.getDate("date_completed"));
                    lessons.add(im);
                    currLesson = lesson;
                }

                im.getPids().add(rs.getString("pid"));
            }
            return lessons;
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
            SqlUtilities.releaseResources(null, pstat2, null);
        }
    }

    /**
     * return the count of viewed lessons for the specified test run
     * 
     * @param conn
     * @param runId
     * @return
     * @throws Exception
     */
    public int getLessonsViewedCount(final Connection conn, Integer runId, Date fromDate) throws Exception {
        PreparedStatement pstat = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_ASSIGNED_LESSONS_VIEWED_COUNT");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, runId);
            pstat.setDate(2, new java.sql.Date(fromDate.getTime()));
            ResultSet rs = pstat.executeQuery();
            rs.first();
            return rs.getInt(1);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Mark this lesson as being viewed.
     * 
     * set the date completed, but only set if currently unset.
     * 
     * @param conn
     * @param runId
     * @param lesson
     * @throws Exception
     */
    public void setLessonCompleted(final Connection conn, Integer runId, Integer lessonNumber) throws Exception {

        PreparedStatement pstat = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSON_SET_DATE_COMPLETED");
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);
            pstat.setInt(2, lessonNumber);

            int count = pstat.executeUpdate();

            if (count > 0)
                HaUserExtendedDao.updateUserExtendedLessonCompleted(conn, runId, lessonNumber);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Mark this lesson as being viewed.
     * 
     * set the date completed, but only set if currently unset.
     * 
     * @param conn
     * @param runId
     * @param lesson
     * @throws Exception
     */
    public void markLessonAsCompleted(final Connection conn, Integer runId, String lesson) throws Exception {

        PreparedStatement pstat = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSON_SET_DATE_COMPLETED_ORIG");
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, runId);
            pstat.setString(2, lesson);

            pstat.executeUpdate();
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Look up and load existing test run named by runId
     * 
     * @param conn
     * @param runId
     * @return
     * @throws HotMathException
     */
    public HaTestRun lookupTestRun(final int runId) throws Exception {

        final HaTestRun testRun = new HaTestRun();
        testRun.setRunId(runId);

        List<HaTestRunResult> testRunResult = this.getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LOOKUP"), new Object[] { runId },
                new RowMapper<HaTestRunResult>() {
                    public HaTestRunResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            if (rowNum == 0) {
                                testRun.setRunTime(rs.getTimestamp("run_time").getTime());
                                testRun.setSessionNumber(rs.getInt("run_session"));
                                testRun.setAnsweredCorrect((rs.getInt("answered_correct")));
                                testRun.setAnsweredIncorrect((rs.getInt("answered_incorrect")));
                                testRun.setNotAnswered((rs.getInt("not_answered")));
                                testRun.setPassing(rs.getInt("is_passing") == 0 ? false : true);
                            }

                            HaTestRunResult testResult = new HaTestRunResult();

                            testRun.setHaTest(HaTestDao.getInstance().loadTest(rs.getInt("test_id")));

                            String pid = rs.getString("pid");
                            if (pid != null) {
                                testResult.setPid(pid);
                                testResult.setResult(rs.getString("answer_status"));
                                testResult.setResultId(rs.getInt("rid"));
                                testResult.setResponseIndex(rs.getInt("answer_index"));

                                testRun.results.add(testResult);
                            }

                            return testResult;

                        } catch (Exception e) {
                            __logger.error("Error getting test run: " + runId);
                            throw new SQLException(e.getMessage(), e);
                        }
                    }
                });

        return testRun;
    }

    /**
     * Return all test runs created against test_id
     * 
     * @param conn
     * @param testId
     * @return
     * @throws Exception
     */
    public List<HaTestRun> lookupTestRunsForTest(final Connection conn, int testId) throws Exception {

        PreparedStatement pstat = null;
        ResultSet rs = null;

        List<HaTestRun> runs = new ArrayList<HaTestRun>();
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LOOK_FOR_TESTS");

            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, testId);
            rs = pstat.executeQuery();
            while (rs.next()) {
                runs.add(lookupTestRun(rs.getInt("run_id")));
            }

            return runs;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    /**
     * Remove any current results for the specified test run
     * 
     * @param conn
     * @param runId
     * @throws Exception
     */
    public void removeAllQuizResponses(final Connection conn, int runId) throws Exception {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("delete from HA_TEST_RUN_RESULTS where run_id = " + runId);
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    public HaTestRunResult addRunResult(final Connection conn, int runId, String pid, String answerStatus,
            int answerIndex) throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_RESULT_INSERT");
            pstat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            HaTestRunResult testRunResult = new HaTestRunResult();

            pstat.setInt(1, runId);
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

            testRunResult.setResultId(autoIncKeyFromApi);
            testRunResult.setResult(answerStatus);
            testRunResult.setPid(pid);

            return testRunResult;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error adding run result: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    public void setSessionNumber(int runId, int sn) throws Exception {
        getSimpleJdbcTemplate().update(
                CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_SET_SESSION_NUMBER"),
                new Object[] { sn, runId });
    }

    /**
     * determine if all Lessons for TestRun have been completed
     * 
     * @param conn
     * @param runId
     * @throws Exception
     */
    public boolean testRunLessonsCompleted(final Connection conn, int runId) throws Exception {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_ASSIGNED_LESSON_STATUS");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, runId);
            pstat.setInt(2, runId);
            rs = pstat.executeQuery();
            if (rs.next()) {
                int totalSessions = rs.getInt("total_sessions");
                int sessionsCompleted = rs.getInt("sessions_completed");
                return (totalSessions == sessionsCompleted);
            }
        } catch (Exception e) {
            __logger.error("*** Error getting TestRun Lesson status", e);
            throw new HotMathException("Could not get assigned lesson status");
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
        return false;
    }

    /**
     * holds a single lesson and the associated pids
     * 
     * @author casey
     * 
     */
    public class TestRunLesson {
        String lesson;
        String file;
        List<RppWidget> rpps = new ArrayList<RppWidget>();

        public TestRunLesson(String lesson, String file) {
            this.lesson = lesson;
            this.file = file;
        }

        public String getLesson() {
            return lesson;
        }

        public void setLesson(String lesson) {
            this.lesson = lesson;
        }

        public List<RppWidget> getPids() {
            return rpps;
        }

        public void setPids(List<RppWidget> rpps) {
            this.rpps = rpps;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }
}
