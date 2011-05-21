package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.HotMathProperties;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * defines data access methods for HaTest
 * 
 * @author Bob
 * 
 */

public class HaTestDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(HaTest.class);

    static private HaTestDao __instance;

    static public HaTestDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (HaTestDao) SpringManager.getInstance().getBeanFactory().getBean("haTestDao");
        }
        return __instance;
    }

    private HaTestDao() {
        /** empty */
    }

    /**
     * Save an answer for this test question.
     * 
     * Only one answer per question.
     * 
     * @param pid
     * @param answer
     * @throws Exception
     */
    public static void saveTestQuestionChange(final Connection conn, final Integer testId, String pid, int answer,
            boolean isCorrect) throws Exception {
        PreparedStatement pstat = null;
        try {
            String sql = "insert into HA_TEST_CURRENT(test_id, pid, response_number, is_correct)value(?,?,?,?)";
            // remove the current selected value;
            Statement smtt = conn.createStatement();
            String sql2 = "delete from HA_TEST_CURRENT where test_id = '" + testId + "' and pid = '" + pid + "'";
            smtt.executeUpdate(sql2);
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, testId);
            pstat.setString(2, pid);
            pstat.setInt(3, answer);
            pstat.setBoolean(4, isCorrect);

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Create not update current test answer: " + pid); // but
                                                                                             // why?
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Return list of objects representing each pid and the current answer
     * selected.
     * 
     * @return
     * @throws Exception
     */
    public static List<HaTestRunResult> getTestCurrentResponses(final Connection conn, final Integer testId)
            throws Exception {

        List<HaTestRunResult> results = new ArrayList<HaTestRunResult>();

        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "select * from v_HA_TEST_CURRENT_STATUS where test_id = ?";
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, testId);

            rs = pstat.executeQuery();
            while (rs.next()) {
                HaTestRunResult res = new HaTestRunResult();
                res.setPid(rs.getString("pid"));
                String re = rs.getString("is_correct");
                if (re != null) {
                    res.setResponseIndex(rs.getInt("response_number"));
                    if (re.equals("1"))
                        res.setResult("Correct");
                    else
                        res.setResult("Incorrect");
                } else {
                    res.setResult("Unanswered");
                }

                results.add(res);

            }
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }

        return results;
    }

    /**
     * Remove from DB all current answer selections
     * 
     * @throws Exception
     */
    private static void clearCurrentResults(final Connection conn, Integer testId) throws Exception {

        PreparedStatement pstat = null;
        try {
            String sql = "delete from HA_TEST_CURRENT where test_id = ?";
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, testId);
            pstat.executeUpdate();
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Convenience method used to create a new HaTest from raw data
     * 
     * @param uid
     * @param textCode
     * @param chapter
     * @param startPn
     * @param endPn
     * @return
     * @throws HotMathException
     */
    static public final int EMPTY_TEST = -1;
    public HaTest createTest(final Integer uid, final HaTestDef testDef, final int segment)
            throws Exception {

        assert (segment != 0);
        String msg = String.format("creating test: uid: %d, segment: %d, testDef: %s", uid, segment, testDef);
        __logger.debug(msg);

        final StudentUserProgramModel userProgram = CmUserProgramDao.getInstance().loadProgramInfoCurrent(uid);

        /**
         * Determine determine which alternate test to use
         * 
         * @see CreateTestRunCommand.java
         */
        final int segmentSlot = CmStudentDao.getInstance().loadActiveInfo(uid).getActiveSegmentSlot();

        HaUser user = HaUserDao.getInstance().lookUser(uid, false);
        final HaTestConfig config = user.getTestConfig();

        final List<String> testIds = (List<String>)(segment == EMPTY_TEST?new ArrayList<String>():testDef.getTestIdsForSegment(userProgram, segment, config, segmentSlot));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_NEW_TEST");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, uid);
                    ps.setInt(2, userProgram.getId());
                    ps.setInt(3, testDef.getTestDefId());
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(5, segment);
                    ps.setInt(6, segmentSlot);

                    /**
                     * If custom program ask program how many segments
                     * 
                     * TODO: why not a dynamic config object?
                     * 
                     */
                    int segmentCount = 0;
                    if (userProgram.getCustomProgramId() > 0) {
                        segmentCount = CmCustomProgramDao.getInstance().getTotalSegmentCount(connection,userProgram.getCustomProgramId());
                    } else if (userProgram.getCustomQuizId() > 0)
                        segmentCount = 1;
                    else {
                        segmentCount = config.getSegmentCount();
                    }
                    ps.setInt(7, segmentCount);

                    ps.setInt(8, testIds.size());

                    return ps;
                } catch (Exception e) {
                    __logger.error(e.getMessage(), e);
                    throw new SQLException("Error creating test run", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int testId = keyHolder.getKey().intValue();

        for (final String pid : testIds) {
            // insert IDS for use with this test
            int count = getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement("insert into HA_TEST_IDS(test_id, pid) values(?,?)");
                    ps.setInt(1, testId);
                    ps.setString(2, pid);

                    return ps;
                }
            });
            if (count != 1)
                throw new Exception("Could not add a problem id to a test");
        }

        // mark this user's record indicating this test as the active
        user.setActiveTest(testId);
        user.setActiveTestRunId(null);
        user.setActiveTestSegment(segment);
        user.update();

        return loadTest(testId);
    }

    public HaTest loadTest(final int testId) throws Exception {

        __logger.debug("Loading test: " + testId);

        HaTest testCached = (HaTest) CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST, testId);
        if (testCached != null)
            return testCached;

        
            StudentUserProgramModel programInfo = new StudentUserProgramModel(); 
            List<HaTest> tests = getJdbcTemplate().query(
                    CmMultiLinePropertyReader.getInstance().getProperty("HA_TEST_LOAD"),
                    new Object[]{testId},
                    new RowMapper<HaTest>() {
                        @Override
                        public HaTest mapRow(ResultSet rs, int rowNum) throws SQLException {
                            try {
                                HaTest test = new HaTest();
                                test.setTestId(rs.getInt("test_id"));
                                test.setUser(HaUserDao.getInstance().lookUser(rs.getInt("user_id"), false));
                                test.setSegment(rs.getInt("test_segment"));
                                test.setSegmentSlot(rs.getInt("test_segment_slot"));
                                test.setNumTestQuestions(rs.getInt("test_question_count"));
                                test.setTotalSegments(rs.getInt("total_segments"));
                                
            
                                test.setProgramInfo(new StudentUserProgramModel());
                                Integer prgId = rs.getInt("prog_id");
                                if (prgId != null && prgId > 0) {
                                    /**
                                     * (new style) use data from CM_USER_PROGRAM attached to this
                                     * test record
                                     * 
                                     */
                                    __logger.debug("Loading test program info from CM_USER_PROGRAM: " + testId + ", " + prgId);
                                    StudentUserProgramModel programInfo = test.getProgramInfo();
                                    programInfo.setId(prgId);
                                    programInfo.setAdminId(rs.getInt("admin_id"));
                                    programInfo.setCreateDate(rs.getDate("prog_create_date"));
                                    programInfo.setTestDefId(rs.getInt("prog_test_def_id"));
                                    programInfo.setTestName(rs.getString("prog_test_name"));
                                    programInfo.setConfig(new HaTestConfig(rs.getString("test_config_json")));
                                }
                                else {
                                        /**
                                         * fall back to extracting program info attached to user
                                         * 
                                         */
                                        __logger.debug("Loading test program info from current assigned program: " + testId);
                                        test.setProgramInfo(CmUserProgramDao.getInstance().loadProgramInfoCurrent(test.getUser().getUid()));                
                                }                                    
                                
                                // todo: remove connection
                                Connection conn=HotMathProperties.getInstance().getDataSourceObject().getSbDBConnection().getConnection();
                                test.setTestDef(HaTestDefFactory.createTestDef(conn, test.getProgramInfo().getTestDefId()));

                                
                                return test;
                            } catch (Exception e) {
                                __logger.error("Error loading test: " + testId);
                                throw new SQLException(e.getMessage(), e);
                            }
                        }
                    });


            
            /** There should be only one test for a given test_id
             *  
             *  If multiple, then is in error. 
             *  
             *  Report error and return first test found.
             *  
             *  
             */
            if(tests.size() == 0) {
                throw new Exception("No such test found: " + testId);
            }
            else if(tests.size() > 1) {
                __logger.warn("More than one test found for test_id " + testId);
            }
            
            HaTest test = tests.get(0);
            
            /**
             * Get all ids defined for test and add to HaTest object
             * 
             */
            List<String> testIds = getTestIdsForTest(test.getTestId());
            for (String pid : testIds) {
                test.addPid(pid);
            }

            // HaTestConfig config = new HaTestConfig(configJson);
            // test.setConfig(config);

            CmCacheManager.getInstance().addToCache(CacheName.TEST, testId, test);

            return test;
    }

    /**
     * Return list of HaTest records for given program, or empty list if no such
     * tests exist.
     * 
     * @param conn
     * @param progList
     * @return
     * @throws HotMathException
     */
    static public List<HaTest> loadTestsForProgramList(final Connection conn, List<StudentUserProgramModel> progList)
            throws HotMathException {

        String progIds = getProgIdList(progList);

        Map<Integer, StudentUserProgramModel> spmMap = new HashMap<Integer, StudentUserProgramModel>();
        for (StudentUserProgramModel m : progList) {
            spmMap.put(m.getId(), m);
        }

        if (__logger.isDebugEnabled())
            __logger.debug("loadTestsForProgramList(): progIds: " + progIds);

        PreparedStatement pstat = null;
        ResultSet rs = null;
        List<HaTest> list = new ArrayList<HaTest>();
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("HA_TEST_LOAD_FOR_PROGRAMS");
            pstat = conn.prepareStatement(sql.replaceFirst("XXX", progIds));

            if (__logger.isDebugEnabled())
                __logger.debug("loadTestsForProgramList(): pstat: " + pstat.toString());

            rs = pstat.executeQuery();

            List<Integer> testIds = new ArrayList<Integer>();

            while (rs.next()) {
                HaTest test = new HaTest();
                test.setTestId(rs.getInt("test_id"));
                test.setUser(HaUser.lookUser(conn, rs.getInt("user_id"), null, true));
                test.setSegment(rs.getInt("test_segment"));
                test.setNumTestQuestions(rs.getInt("test_question_count"));
                test.setTotalSegments(rs.getInt("total_segments"));
                test.setCreateTime(rs.getTimestamp("create_time"));

                /*
                 * Create program model and add to HaTest object
                 */
                Integer prgId = rs.getInt("prog_id");
                StudentUserProgramModel programInfo = spmMap.get(prgId);

                test.setProgramInfo(programInfo);
                test.setTestDef(HaTestDefFactory.createTestDef(conn, programInfo.getTestDefId()));

                // collect testIds
                testIds.add(test.getTestId());

                list.add(test);
            }
            if (list.size() == 0)
                return list;

            /*
             * Get pids for all tests in list
             */
            Map<Integer, List<String>> testPidMap = getPidsForTestIds(conn, testIds);

            /*
             * add pids to each test
             */
            for (HaTest test : list) {
                Integer testId = test.getTestId();
                List<String> pids = testPidMap.get(testId);
                if (pids == null)
                    continue;

                for (String pid : pids) {
                    test.addPid(pid);
                }
            }
            return list;

        } catch (HotMathException hme) {
            __logger.warn("*** no tests found for pstat: " + pstat.toString());
            throw hme;
        } catch (Exception e) {
            __logger.error("Error looking up Student Test data for Prog Ids: " + progIds, e);
            throw new HotMathException(e, "Error looking up Student Test data: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    int TEST_SIZE = 10;

    /**
     * Create a new Prescription based on test answers
     * 
     * @param wrongGids
     * @param answeredCorrect
     * @param answeredIncorrect
     * @param totalSessions
     * @return
     * @throws HotMathException
     */
    public HaTestRun createTestRun(final Connection conn, Integer studentUid, Integer testId,
            int answeredCorrect, int answeredIncorrect, int notAnswered) throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        
        /** There should not be any existing test run for this test_id
         *  
         *  if there is then, it is a duplicate and is an exception.
         *  
         *  Throw exception to help find this bug!
         */
        Integer count = getJdbcTemplate().queryForObject(
                "select count(*) from HA_TEST_RUN where test_id = ?",
                new Object[]{testId},
                new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt(1);
                    }
                });

        
        try {

            if(count > 0) {
                throw new Exception("This test has already been checked");
            }
                        
            
            /**
             * Determine if user passed this quiz/test
             * 
             * This information is used to move to the next quiz slot.
             * 
             */
            CmUserProgramDao dao = CmUserProgramDao.getInstance();
            StudentUserProgramModel pinfo = dao.loadProgramInfoCurrent(studentUid);
            int passPercentRequired = pinfo.getConfig().getPassPercent();
            int testCorrectPercent = GetPrescriptionCommand.getTestPassPercent(answeredCorrect + answeredIncorrect
                    + notAnswered, answeredCorrect);

            /**
             * if user passed quiz or if the custom program which always is
             * passing
             */
            boolean passedQuiz = pinfo.getCustomProgramId() > 0 || (testCorrectPercent >= passPercentRequired);

            HaTest test = HaTestDao.getInstance().loadTest(testId);
            String sql = "insert into HA_TEST_RUN(test_id, run_time, answered_correct, " +
                         " answered_incorrect, not_answered,run_session,is_passing)values(?,?,?,?,?,1,?)";
            pstat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            HaTestRun testRun = new HaTestRun();

            pstat.setInt(1, testId);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            pstat.setTimestamp(2, ts);
            pstat.setInt(3, answeredCorrect);
            pstat.setInt(4, answeredIncorrect);
            pstat.setInt(5, notAnswered);
            pstat.setInt(6, passedQuiz ? 1 : 0);

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Could not create new test for: " + testId);

            int runId = -1;
            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                runId = rs.getInt(1);
            } else {
                throw new HotMathException("Error creating PK for test");
            }

            testRun.setRunId(runId);
            testRun.setRunTime(ts.getTime());
            testRun.setHaTest(test);
            testRun.setAnsweredCorrect(answeredCorrect);
            testRun.setAnsweredIncorrect(answeredIncorrect);
            testRun.setNotAnswered(notAnswered);
            testRun.setPassing(passedQuiz);

            /**
             * transfer current selections to this test run
             * 
             */
            testRun.transferCurrentToTestRun(conn);

            /**
             * Clear all existing selections for this test
             * 
             */
            clearCurrentResults(conn, testId);

            /**
             * update this User's row to indicate new action test run
             */
            test.getUser().setActiveTestRunId(testRun.getRunId());
            test.getUser().setActiveTest(0); // if test_run is active, test is
                                             // not
            test.getUser().update();

            updateTestRunSessions(conn, runId);

            HaUserExtendedDao.updateUserExtended(conn, studentUid, testRun);

            return testRun;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error looking up test: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    /**
     * Update the total sessions for a given test_run.
     * 
     * 
     * This will create the prescription and extract the session count and
     * update appropriate HA_TEST_RUN.
     * 
     * @param conn
     * @param runId
     * @throws Exception
     */
    static public void updateTestRunSessions(Connection conn, Integer runId) throws Exception {
        /**
         * now pre-create the prescription, and extract the total number of
         * sessions
         * 
         * NOTE: this is needed because, we need the test_run to create the
         * prescription.
         */
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, runId);
        int totalSessions = pres.getSessions().size();

        // update the HA_TEST_RUN record
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            int c = stmt.executeUpdate("update HA_TEST_RUN set total_sessions = " + totalSessions + " where run_id = "
                    + runId);
            if (c != 1) {
                throw new HotMathException("Could not update test_run with total_sessions: " + runId);
            }
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * Return list of pids that represent all ids currently in test for all
     * segments.
     * 
     * Print warning if zero found.
     * 
     * @param testId
     * 
     * @return
     * @throws Exception
     */
    public List<String> getTestIdsForTest(int testId) throws Exception {
        
        return getJdbcTemplate().query(
                "select * from HA_TEST_IDS where test_id = ?",
                new Object[]{testId},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("pid");
                    }
                });
    }

    /**
     * Return Map of (testId, pids) that represents all pids in each test for
     * all segments.
     * 
     * @param testIds
     * 
     * @return
     * @throws Exception
     */
    static public Map<Integer, List<String>> getPidsForTestIds(final Connection conn, List<Integer> testIds)
            throws Exception {

        Map<Integer, List<String>> testPidMap = new HashMap<Integer, List<String>>();

        String testIdStr = testIds.toString().substring(1);
        testIdStr = testIdStr.substring(0, testIdStr.length() - 1);

        List<String> pids = new ArrayList<String>();
        PreparedStatement pstat = null;
        ResultSet rs = null;

        try {
            // read tests' pids
            String sql = "select test_id, pid from HA_TEST_IDS where test_id in (XXX) order by test_id";
            String sqlWithTestIds = sql.replaceFirst("XXX", testIdStr);

            pstat = conn.prepareStatement(sqlWithTestIds);
            rs = pstat.executeQuery();
            if (!rs.first()) {
                __logger.warn("Could not load pids for testIds: " + testIds);
            } else {
                int lastId = -1;
                while (rs.next()) {
                    int tid = rs.getInt("test_id");
                    if (tid != lastId) {
                        lastId = tid;
                        pids = new ArrayList<String>();
                        testPidMap.put(tid, pids);
                    }
                    pids.add(rs.getString("pid"));
                }
            }
            return testPidMap;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    private static String getProgIdList(List<StudentUserProgramModel> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (StudentUserProgramModel m : list) {
            if (first)
                first = false;
            else
                sb.append(",");
            sb.append(m.getId());
        }
        return sb.toString();
    }

    static public List<HaTest> getProgramTests(final Connection conn, Integer progId) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            List<HaTest> tests = new ArrayList<HaTest>();

            stmt = conn
                    .prepareStatement("select user_prog_id, test_id from HA_TEST where user_prog_id = ? order by create_time");
            stmt.setInt(1, progId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                HaTest test = HaTestDao.getInstance().loadTest(rs.getInt("user_prog_id"));

                List<HaTestRun> testRuns = HaTestRunDao.getInstance().lookupTestRunsForTest(conn, rs.getInt("test_id"));
                test.setTestRuns(testRuns);

                tests.add(test);
            }

            return tests;
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
        }
    }

}