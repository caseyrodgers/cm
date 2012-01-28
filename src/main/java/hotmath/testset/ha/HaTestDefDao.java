package hotmath.testset.ha;

import static hotmath.cm.util.CmCacheManager.CacheName.TEST_DEF;
import hotmath.HotMathException;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Provide DAO functionality for HaTestDefs
 * 
 * @author casey
 * 
 */
public class HaTestDefDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(HaTestDefDao.class);

    static private HaTestDefDao __instance;

    static public HaTestDefDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (HaTestDefDao) SpringManager.getInstance().getBeanFactory().getBean("haTestDefDao");
        }
        return __instance;
    }

    private HaTestDefDao() {
        /** Empty */
    }

    public List<String> getTestNames(final Connection conn) throws CmException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> names = new ArrayList<String>();
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("ALL_TEST_NAMES");
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString(1));
            }

            return names;
        } catch (Exception e) {
            __logger.error(e);
            throw new CmException(e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Look up existing HaTestDef based on the name
     * 
     * @param conn
     *            The connection to use
     * @param name
     *            the name of the test def
     * 
     * @throws HotMathException
     */
    public HaTestDef getTestDef(final String name) throws Exception {
        // try cache first
        HaTestDef td = (HaTestDef) CmCacheManager.getInstance().retrieveFromCache(TEST_DEF, name);
        if (td != null) {
            return td;
        }

        td = getJdbcTemplate().queryForObject(
                CmMultiLinePropertyReader.getInstance().getProperty("TEST_FOR_TEST_NAME"), new Object[] { name },
                new TestDefMapper());

        return td;
    }

    /**
     * Look up HaTestDef based on the testDefId
     * 
     * @param conn
     *            The connection to use
     * @param name
     *            the id of the test def
     * 
     * @throws HotMathException
     */
    public HaTestDef getTestDef(final int testDefId) throws Exception {
        // try cache first
        HaTestDef td = (HaTestDef) CmCacheManager.getInstance().retrieveFromCache(TEST_DEF, String.valueOf(testDefId));
        if (td != null) {
            return td;
        }

        td = this.getJdbcTemplate().queryForObject(
                CmMultiLinePropertyReader.getInstance().getProperty("TEST_FOR_TEST_DEF_ID"),
                new Object[] { testDefId }, new TestDefMapper());

        return td;
    }

    static class TestDefMapper implements RowMapper<HaTestDef> {
        @Override
        public HaTestDef mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                String testName = rs.getString("test_name");
                HaTestDef testDef = null;
                if (testName.toLowerCase().indexOf(CmProgram.AUTO_ENROLL.getTitle().toLowerCase()) > -1) {
                    testDef = new HaTestDefPlacement();
                } else {
                    testDef = new HaTestDef();
                }

                testDef.name = rs.getString("test_name");
                testDef.textCode = rs.getString("textcode");
                testDef.chapter = rs.getString("chapter");
                testDef.testDefId = rs.getInt("test_def_id");
                testDef.config = new HaTestConfig(rs.getString("test_config_json"));
                testDef.subjectId = rs.getString("subj_id");
                testDef.progId = rs.getString("prog_id");
                testDef.stateId = rs.getString("state_id");
                testDef.numAlternateTests = rs.getInt("num_alt_tests");

                CmCacheManager.getInstance().addToCache(CmCacheManager.CacheName.TEST_DEF, testDef.getName(), testDef);
                CmCacheManager.getInstance().addToCache(CmCacheManager.CacheName.TEST_DEF,
                        String.valueOf(testDef.getTestDefId()), testDef);

                return testDef;
            } catch (Exception e) {
                __logger.error("Error reading HaTestDef object", e);
                throw new SQLException("Error reading HaTestDef", e);
            }
        }
    }

    public HaTestDef getTestDef(final Connection conn, String progType, String subject) throws Exception {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_TEST_DEF_BY_PROG_SUBJECT");

            pstat = conn.prepareStatement(sql);

            pstat.setString(1, progType);
            pstat.setString(2, subject);
            rs = pstat.executeQuery();
            if (rs.first()) {
                return getTestDef(rs.getInt("test_def_id"));
            }

            return null;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    public List<HaTestDef> getTestDefs(final Connection conn, List<Integer> testDefIds) throws Exception {
        List<HaTestDef> list = new ArrayList<HaTestDef>();

        try {
            for (Integer testDefId : testDefIds) {
                list.add(getTestDef(testDefId));
            }
        } catch (Exception e) {
            __logger.error("Error getting test defs for test_def_ids: " + testDefIds, e);
            throw new HotMathException(e, "Error getting test defs for testDefIds: " + testDefIds + ", "
                    + e.getMessage());
        }
        return list;
    }

    public List<HaTestDef> getTestDefs(final Connection conn, String progId) throws Exception {
        List<Integer> testDefIds = getTestDefIdsForProgId(conn, progId);
        List<HaTestDef> list = getTestDefs(conn, testDefIds);
        return list;
    }

    public List<Integer> getTestDefIdsForProgId(final Connection conn, String progId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> ids = new ArrayList<Integer>();
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_DEF_IDS_FOR_PROG_ID");
            ps = conn.prepareStatement(sql);
            ps.setString(1, progId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("test_def_id"));
            }
            if (ids.size() < 1) {
                __logger.warn("*** no test_def_ids found for prog_id: " + progId, new Exception());
            }
        } catch (Exception e) {
            __logger.error("Error getting test def ids for prog id: " + progId, e);
            throw new HotMathException(e, "Error getting test def ids for prog id: " + progId + ", " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return ids;
    }

    /**
     * Return list of chapter names for book associated with this program
     * 
     * @return
     * @throws Exception
     */
    public List<String> getProgramChapters(final Connection conn, HaTestDef def) throws Exception {

        PreparedStatement pstat = null;
        ResultSet rs = null;
        List<String> chapters = new ArrayList<String>();
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("PROGRAM_CHAPTERS_FOR_TEST_DEF");

            pstat = conn.prepareStatement(sql);

            pstat.setString(1, def.textCode);

            rs = pstat.executeQuery();
            while (rs.next()) {
                chapters.add(rs.getString("title"));
            }
            return chapters;
        } catch (Exception e) {
            throw new HotMathException(e, "Error getting program chapters: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    /**
     * Each test is made up of 40 questions.
     * 
     * These questions are divided up into 4 segments of 10 questions each.
     * 
     * items are return in normal solution sort order
     * 
     * @TODO: allow setting multiple chapters
     * @TODO: move into factory pattern
     * 
     * @param conn
     * @param conn
     *            Active connection passed in
     * @param segment
     * @param textcode
     * @param chapter
     * @return
     * @throws SQLException
     */
    public List<String> getTestIdsForSegment(StudentUserProgramModel userProgram, int segment, String textcode, String chapter, HaTestConfig config, int segmentSlot) throws Exception {

        assert (segment > 0);

        /**
         * Custom program?
         * 
         * TODO: handle custom program correctly
         */
        if (segment == -1) {
            return new ArrayList<String>();
        }

        if (__logger.isDebugEnabled()) {
            __logger.debug("getTestIdsForSegment(): segment: " + segment + ", textCode: " + textcode + ", chapter: "
                    + chapter + ", segmentSlot: " + segmentSlot);
        }

        int cnt = 0;
        int solsPerSeg = 0;
        int segPnEnd = 0;
        int segPnStart = 0;

        // Use chapter from config if available, otherwise
        // use the default chapter defined for this test_def
        List<String> problemIds = getTestIds(userProgram, textcode, chapter, segmentSlot, 0, 99999, config);

        if (userProgram.getCustomProgramId() > 0) {

            /**
             * if custom program, then all ids are included
             */
            return problemIds;
        } else {

            /** return named segment */

            cnt = problemIds.size();

            // break program into segments?
            solsPerSeg = (config != null) ? solsPerSeg = cnt / config.getSegmentCount() : 0;
            solsPerSeg = (solsPerSeg < 5) ? cnt : solsPerSeg;

            segPnEnd = ((segment) * solsPerSeg);
            segPnStart = (segPnEnd - (solsPerSeg - 1));

            problemIds = getTestIds(userProgram, textcode, chapter, segmentSlot, segPnStart, segPnEnd, config);
            if (problemIds.size() == 0) {
                throw new HotMathException(String.format("No problems for test segment: %s, %s, %d, %d, %d", textcode,
                        chapter, segPnStart, segPnEnd, segmentSlot));
            }
            return problemIds;
        }
    }

    public List<String> getTestIdsForPlacementSegment(int segment, String textcode, String chapter,
            HaTestConfig config, final int segmentSlot) throws Exception {

        List<String> strings = getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("TEST_IDS_FOR_PLACEMENT_SEGMENT"),
                new Object[] { textcode, chapter, segmentSlot }, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("problemindex");
                    }
                });

        return strings;
    }

    /**
     * Return list of problem ids that match this program quiz.
     * 
     * Check to see if is a custom program and process
     * 
     * 
     * @param conn
     * @param textcode
     * @param chapter
     * @param section
     * @param startProblemNumber
     * @param endProblemNumber
     * @param config
     * @return
     * @throws Exception
     */
    public List<String> getTestIds(StudentUserProgramModel userProgram, String textcode,
            String chapter, int section, int startProblemNumber, int endProblemNumber, HaTestConfig config)
            throws Exception {
        if (userProgram.getCustomProgramId() > 0) {
            return getTestIdsCustom(userProgram, section, startProblemNumber, endProblemNumber, config);
        } else {
            return getTestIdsBasic(textcode, chapter, section, startProblemNumber, endProblemNumber, config);
        }
    }

    /**
     * Return the solution ids used by this custom program segment
     * 
     * @param conn
     * @param userProgram
     * @param section
     * @param startProblemNumber
     * @param endProblemNumber
     * @param config
     * @return
     * @throws Exception
     */
    private List<String> getTestIdsCustom(StudentUserProgramModel userProgram, int section, int startProblemNumber,
            int endProblemNumber, HaTestConfig config) throws Exception {
        int customProgramId = userProgram.getCustomProgramId();
        CmList<CustomQuizId> items = CmCustomProgramDao.getInstance().getCustomProgramQuizIds(customProgramId, section);

        List<String> pids = new ArrayList<String>();
        for (CustomQuizId quid : items) {
            pids.add(quid.getPid());
        }
        return pids;
    }

    /**
     * Return the normal default test ids
     * 
     * @param conn
     * @param textcode
     * @param chapter
     * @param section
     * @param startProblemNumber
     * @param endProblemNumber
     * @param config
     * @return
     * @throws Exception
     */
    public List<String> getTestIdsBasic(String textcode, String chapter, int section, int startProblemNumber,
            int endProblemNumber, HaTestConfig config) throws Exception {

        String sql = "";
        if (config != null && config.getChapters().size() > 0) {
            /**
             * Is a chapter program
             * 
             */
            if (chapter == null) {
                chapter = config.getChapters().get(0);
            }

            sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_IDS_FOR_CHAPTER_PROGRAM");
        } else {
            sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_IDS_FOR_PROGRAM");
        }

        List<String> pids = getJdbcTemplate().query(sql,
                new Object[] { textcode, chapter, section + 1, startProblemNumber, endProblemNumber },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("problemindex");
                    }
                });
        return pids;

    }

    /**
     * Return the Chapter info for named program or null
     * 
     * @param conn
     * @param programInfo
     * @return The chapter info or null if no chapter
     * @throws Exception
     */
    public ChapterInfo getChapterInfo(final Connection conn, StudentUserProgramModel programInfo) throws Exception {
        // pass along title and title number
        HaTestConfig config = programInfo.getConfig();

        String chapter = config.getChapters().size() > 0 ? config.getChapters().get(0) : null;
        if (chapter != null) {
            ChapterInfo ci = new ChapterInfo();
            /**
             * If chapter is specified then add the chapter number to the title
             * 
             */
            ci.setChapterTitle(chapter);
            HaTestDefDao tdo = new HaTestDefDao();
            List<String> chapters = tdo.getProgramChapters(conn, tdo.getTestDef(programInfo.getTestDefId()));
            for (int i = 0, t = chapters.size(); i < t; i++) {
                if (chapters.get(i).trim().equals(chapter.trim())) {
                    ci.setChapterNumber(i + 1);
                    return ci;
                }

            }
        }
        return null;
    }


    /**
     * Return list of TestDefs that are of type programType
     * 
     * @param conn
     * @param programType
     * @return
     * @throws Exception
     */
    public List<HaTestDef> getTestDefsForProgramType(final Connection conn, String programType) throws Exception {
        PreparedStatement ps = null;
        try {
            List<HaTestDef> testDefs = new ArrayList<HaTestDef>();
            String sql = "select * from   HA_TEST_DEF where prog_id = ? and is_active = 1 order by load_order";
            ps = conn.prepareStatement(sql);
            ps.setString(1, programType);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                testDefs.add(getTestDef(rs.getInt("test_def_id")));
            }
            return testDefs;
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }
}
