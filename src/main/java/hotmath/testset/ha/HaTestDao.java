package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * defines data access methods for HaTest
 * 
 * @author Bob
 * 
 */

public class HaTestDao {

    static Logger __logger = Logger.getLogger(HaTest.class);

    public HaTestDao() {
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
    public static void saveTestQuestionChange(final Connection conn, final Integer testId, String pid, int answer, boolean isCorrect)
            throws Exception {
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
    static public HaTest createTest(final Connection conn, Integer uid, HaTestDef testDef, int segment)
            throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {

            __logger.debug("creating test: " + uid + ", " + testDef + ", " + segment);

            String sql = "insert into HA_TEST(user_id,user_prog_id,test_def_id,create_time,test_segment,test_segment_slot, total_segments,test_question_count)values(?,?,?,?,?,?,?,?)";

            pstat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // get the program info used to create this test
            StudentUserProgramModel userProgram = new CmUserProgramDao().loadProgramInfoCurrent(conn, uid);

            /**
             * Determine the proper quiz zone to use
             *  @see CreateTestRunCommand.java
             */
            int segmentSlot = new CmStudentDao().loadActiveInfo(conn, uid).getActiveSegmentSlot();

            HaUser user = HaUser.lookUser(conn, uid, null);
            HaTestConfig config = user.getTestConfig();

            List<String> testIds = testDef.getTestIdsForSegment(conn,userProgram, segment, config, segmentSlot);

            pstat.setInt(1, uid);
            pstat.setInt(2, userProgram.getId());
            pstat.setInt(3, testDef.getTestDefId());
            pstat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstat.setInt(5, segment);
            pstat.setInt(6, segmentSlot);
            pstat.setInt(7, config.getSegmentCount());
            pstat.setInt(8, testIds.size());

            // make sure there are not currently defines items for this test
            // Statement stmt = conn.createStatement();
            // stmt.executeUpdate("delete from HA_TEST_IDS where test_id);
            // stmt.close();

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Create not create new test for: " + uid); // but
                                                                                      // why?

            // extract the auto created pk
            int testId = -1;
            try {
                rs = pstat.getGeneratedKeys();
                if (rs.next()) {
                    testId = rs.getInt(1);
                } else {
                    throw new HotMathException("Error creating PK for test");
                }
            } finally {
                SqlUtilities.releaseResources(rs, pstat, null);
            }

            // insert IDS to use for this test
            pstat = conn.prepareStatement("insert into HA_TEST_IDS(test_id, pid) values(?,?)");
            for (String pid : testIds) {
                pstat.setInt(1, testId);
                pstat.setString(2, pid);

                if (pstat.executeUpdate() != 1)
                    throw new Exception("Could not add a problem id to a test");
            }

            // mark this user's record indicating this test as the active
            user.setActiveTest(testId);
            user.setActiveTestRunId(null);
            user.setActiveTestSegment(segment);
            user.update(conn);

            return loadTest(conn, testId);
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error looking up Catchup Math user: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    static public HaTest loadTest(final Connection conn, int testId) throws HotMathException {

        __logger.debug("Loading test: " + testId);

        HaTest testCached = (HaTest) CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST, testId);
        if (testCached != null)
            return testCached;

        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("HA_TEST_LOAD");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, testId);
            rs = pstat.executeQuery();
            if (!rs.first()) {
                __logger.warn("Could not load test for testId: " + testId);
                throw new HotMathException("Could not load test");
            }

            HaTest test = new HaTest();
            test.setTestId(rs.getInt("test_id"));
            test.setUser(HaUser.lookUser(conn, rs.getInt("user_id"), null));
            test.setSegment(rs.getInt("test_segment"));
            test.setNumTestQuestions(rs.getInt("test_question_count"));
            test.setTotalSegments(rs.getInt("total_segments"));

            /**
             * Create program model and add to HaTest object
             * 
             */
            StudentUserProgramModel programInfo = null;
            Integer prgId = rs.getInt("prog_id");
            if (prgId != null && prgId > 0) {
                /**
                 * (new style) use data from CM_USER_PROGRAM attached to this
                 * test record
                 * 
                 */
                __logger.debug("Loading test program info from CM_USER_PROGRAM: " + testId + ", " + prgId);

                programInfo = new StudentUserProgramModel();
                programInfo.setId(prgId);
                programInfo.setAdminId(rs.getInt("admin_id"));
                programInfo.setCreateDate(rs.getDate("prog_create_date"));
                programInfo.setTestDefId(rs.getInt("prog_test_def_id"));
                programInfo.setTestName(rs.getString("prog_test_name"));
                programInfo.setConfig(new HaTestConfig(rs.getString("test_config_json")));
            } else {
                /**
                 * fall back to extracting program info attached to user
                 * 
                 */
                __logger.debug("Loading test program info from current assigned program: " + testId);

                programInfo = new CmUserProgramDao().loadProgramInfoCurrent(conn, test.getUser().getUid());
            }

            test.setProgramInfo(programInfo);
            test.setTestDef(HaTestDefFactory.createTestDef(conn, programInfo.getTestDefId()));

            /**
             * Get all ids defined for test and add to HaTest object
             * 
             */
            List<String> testIds = getTestIdsForTest(conn, test.getTestId());
            for (String pid : testIds) {
                test.addPid(pid);
            }

            // HaTestConfig config = new HaTestConfig(configJson);
            // test.setConfig(config);

            CmCacheManager.getInstance().addToCache(CacheName.TEST, testId, test);

            return test;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error looking up Hotmath Advance user: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
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
            if (list.size() == 0) return list;

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
                if (pids == null) continue;
                
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
    public static HaTestRun createTestRun(final Connection conn, Integer studentUid, Integer testId,int answeredCorrect, int answeredIncorrect, int notAnswered)
            throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {

            /**
             * Determine if user passed this quiz/test
             * 
             * This information is used to move to the next quiz slot.
             * 
             */
            CmUserProgramDao dao = new CmUserProgramDao();
            StudentUserProgramModel pinfo = dao.loadProgramInfoCurrent(conn, studentUid);
            int passPercentRequired = pinfo.getConfig().getPassPercent();
            int testCorrectPercent = GetPrescriptionCommand.getTestPassPercent(answeredCorrect + answeredIncorrect
                    + notAnswered, answeredCorrect);
            boolean passedQuiz = (testCorrectPercent >= passPercentRequired);

            HaTest test = HaTestDao.loadTest(conn, testId);
            String sql = "insert into HA_TEST_RUN(test_id, run_time, answered_correct, answered_incorrect, not_answered,run_session,is_passing)values(?,?,?,?,?,1,?)";
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
            test.getUser().setActiveTest(0); // if test_run is active, test is not
            test.getUser().update(conn);

            updateTestRunSessions(conn, runId);
            
            HaUserExtendedDao.updateUserExtended(conn, studentUid, testRun);

            return testRun;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error looking up Hotmath Advance test: " + e.getMessage());
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
         * now pre-create the prescription, and extract the total number of sessions
         * 
         * NOTE: this is needed because, we need the test_run to create the prescription.
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
    static public List<String> getTestIdsForTest(final Connection conn, int testId) throws Exception {

        List<String> pids = new ArrayList<String>();
        PreparedStatement pstat = null;
        ResultSet rs = null;

        try {
            // now read test's pids
            String sql = "select * from HA_TEST_IDS where test_id = ?";
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, testId);
            rs = pstat.executeQuery();
            if (!rs.first()) {
                __logger.warn("Could not load pids for testId: " + testId);
            } else {
                do {
                    pids.add(rs.getString("pid"));
                } while (rs.next());
            }
            return pids;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    /**
     * Return Map of (testId, pids) that represents all pids in each test for all segments.
     * 
     * @param testIds
     * 
     * @return
     * @throws Exception
     */
    static public Map<Integer,List<String>> getPidsForTestIds(final Connection conn, List<Integer> testIds) throws Exception {

    	Map<Integer, List<String>> testPidMap = new HashMap<Integer, List<String>> ();
    	
		String testIdStr = testIds.toString().substring(1);
    	testIdStr = testIdStr.substring(0, testIdStr.length()-1);
    	
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

            stmt = conn.prepareStatement("select user_prog_id, test_id from HA_TEST where user_prog_id = ? order by create_time");
            stmt.setInt(1, progId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                HaTest test = HaTestDao.loadTest(conn, rs.getInt("user_prog_id"));

                List<HaTestRun> testRuns = new HaTestRunDao().lookupTestRunsForTest(conn, rs.getInt("test_id"));
                test.setTestRuns(testRuns);

                tests.add(test);
            }

            return tests;
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
        }
    }

}