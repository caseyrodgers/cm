package hotmath.gwt.cm_admin.server.model;

import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_LAST_LOGIN_KEY;
import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_LAST_QUIZ_KEY;
import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_PASSING_COUNT_KEY;
import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_TUTORING_USE_KEY;
import hotmath.assessment.InmhItemData;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.ClientInfo;
import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.CmUserException;
import hotmath.gwt.cm_rpc.client.model.CmProgramAssign;
import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.server.service.ClientInfoHolder;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite.Type;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelBase;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.shared.client.model.UserProgramIsNotActiveException;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestDefDescription;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUserExtendedDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import sb.util.SbUtilities;

public class CmStudentDao extends SimpleJdbcDaoSupport {

    private static final Logger __logger = Logger.getLogger(CmStudentDao.class);

    public static final int GROUP_NONE_ID = 1;

    static private CmStudentDao __instance;
    static public CmStudentDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (CmStudentDao)SpringManager.getInstance().getBeanFactory().getBean("cmStudentDao");
        }
        return __instance;
    }

    private CmStudentDao() {
    }

    enum StudentSqlType {
        SINGLE_STUDENT, ALL_STUDENTS_FOR_ADMIN, SELECTED_STUDENTS_FOR_ADMIN
    };

    /**
     * Return the StudentSummary sql for either a single student or all Students
     * under a given Admin.
     *
     * @TODO: move this to a view to allow easy reuse.
     *
     * @param sqlType
     * @param includeSelfRegistrationTemplates  If true, then is_auto_create_template records are included
     *
     * @return
     */
    private String getStudentSql(StudentSqlType sqlType, Boolean includeSelfRegistrationTemplates) throws Exception {
        String studentSql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_SUMMARY");

        switch (sqlType) {
        case ALL_STUDENTS_FOR_ADMIN:
                studentSql += " WHERE a.aid = ? ";
                break;
        case SINGLE_STUDENT:
                studentSql += " WHERE h.uid = ? ";
                break;
        case SELECTED_STUDENTS_FOR_ADMIN:
                studentSql += " WHERE a.aid = ? AND h.uid in (XXX) ";
        }

        // to filter the Self Registration Setup records
        if(!includeSelfRegistrationTemplates)
            studentSql += " AND h.is_auto_create_template = 0 ";


        studentSql += " and h.is_active = ? " +
                "ORDER by h.user_name asc";

        return studentSql;
    }

    /**
     * Return the StudentSummary sql for either a single student or all Students
     * under a given Admin.
     *
     * @TODO: move this to a view to allow easy reuse.
     *
     * @param sqlType
     * @param includeSelfRegistrationTemplates  If true, then is_auto_create_template records are included
     *
     * @return
     */
    private String getStudentSummarySql(StudentSqlType sqlType, Boolean includeSelfRegistrationTemplates) throws Exception {
        String studentSql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_SUMMARY_BASE");

        if (sqlType.equals(StudentSqlType.ALL_STUDENTS_FOR_ADMIN)) {
            studentSql += " WHERE a.aid = ? ";
        } else {
            // single student
            studentSql += " WHERE h.uid = ? ";
        }


        // to filter the Self Registration Setup records
        if(!includeSelfRegistrationTemplates)
            studentSql += " AND h.is_auto_create_template = 0 ";


        studentSql += " and h.is_active = ? " +
                "ORDER by h.user_name asc";

        return studentSql;
    }

    public List<StudentModelI> getSummariesForActiveStudents(final Connection conn, Integer adminUid) throws Exception {
        return getStudentSummaries(conn, adminUid, true);
    }

    public List<StudentModelI> getSummariesForInactiveStudents(final Connection conn, Integer adminUid) throws Exception {
        return getStudentSummaries(conn, adminUid, false);
    }

    public List<StudentModelI> getStudentSummaries(final Connection conn, Integer adminUid, Boolean isActive) throws Exception {
        List<StudentModelI> l = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(getStudentSql(StudentSqlType.ALL_STUDENTS_FOR_ADMIN, false));
            ps.setInt(1, adminUid);
            ps.setInt(2,adminUid);
            ps.setInt(3, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentSummaries(rs);

            loadChapterInfo(conn, l);
        } catch (Exception e) {
            __logger.error(String.format("*** Error getting student summaries for Admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting student summary data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }

    public List<StudentModelI> getStudentSummaries(final Connection conn, Integer adminUid, List<Integer> uids, Boolean isActive) throws Exception {
        List<StudentModelI> l = null;

        String uidStr = getUidString(uids);

        String sql = getStudentSql(StudentSqlType.SELECTED_STUDENTS_FOR_ADMIN, false);
        String sqlWithUids = sql.replaceAll("XXX", uidStr);

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
                ps = conn.prepareStatement(sqlWithUids);
                ps.setInt(1, adminUid);
                ps.setInt(2, adminUid);
                ps.setInt(3, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentSummaries(rs);

            loadChapterInfo(conn, l);
        }
        catch (Exception e) {
            __logger.error(String.format("*** Error getting selected student summaries for Admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting student summary data ***");
        }
        finally {
                SqlUtilities.releaseResources(rs, ps, null);
        }

        return l;
    }


    public List<StudentModelI> getBaseSummariesForActiveStudents(final Connection conn, Integer adminUid) throws Exception {
        return getStudentBaseSummaries(conn, adminUid, true);
    }

    public List<StudentModelI> getBaseSummariesForInactiveStudents(final Connection conn, Integer adminUid) throws Exception {
        return getStudentBaseSummaries(conn, adminUid, false);
    }

    public List<StudentModelI> getStudentBaseSummaries(final Connection conn, Integer adminUid, Boolean isActive) throws Exception {
        List<StudentModelI> l = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Boolean tutoringEnabledForAdmin = isTutoringEnabledForAdmin(conn, adminUid);

            ps = conn.prepareStatement(getStudentSummarySql(StudentSqlType.ALL_STUDENTS_FOR_ADMIN, false));
            ps.setInt(1, adminUid);
            ps.setInt(2,adminUid);
            ps.setInt(3, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentBaseSummaries(rs, tutoringEnabledForAdmin);

            loadChapterInfo(conn, l);
        } catch (Exception e) {
            __logger.error(String.format("*** Error getting student base summaries for Admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting student base summary data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }

    public List<StudentModelI> getStudentBaseSummaries(final Connection conn, Integer adminUid, List<Integer> uids, Boolean isActive) throws Exception {
        List<StudentModelI> l = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
                Boolean tutoringEnabledForAdmin = isTutoringEnabledForAdmin(conn, adminUid);

                String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_SUMMARY_BASE");
                StringBuilder sb = new StringBuilder(sql);
                sb.append(" WHERE a.aid = ? ");
                sb.append(" AND h.uid in (").append(getUidString(uids)).append(")");
            sb.append(" AND h.is_active = ? ");
            sb.append(" ORDER by h.user_name asc");

            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, adminUid);
            ps.setInt(2, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentBaseSummaries(rs, tutoringEnabledForAdmin);

            loadChapterInfo(conn, l);
        } catch (Exception e) {
            __logger.error(String.format("*** Error getting student base summaries for Admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting student base summary data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }

    public List<StudentModelI> getStudentExtendedSummaries(final Connection conn, List<Integer> studentUids) throws Exception {

        List<StudentModelI> l = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_SUMMARY_EXTENDED");

        String uidStr = getUidString(studentUids);

        String sqlWithUids = sql.replaceAll("XXX", uidStr);

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
                ps = conn.prepareStatement(sqlWithUids);
                rs = ps.executeQuery();
                l = loadStudentExtendedSummaries(rs);
        } catch (Exception e) {
            __logger.error(String.format("*** Error getting student extended summary data for uids: %s", uidStr), e);
                        throw new Exception("*** Error getting student extended summary data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }

        private String getUidString(List<Integer> studentUids) {
                if (studentUids == null) return " ";
                String uidStr = studentUids.toString().substring(1);
        uidStr = uidStr.substring(0, uidStr.length()-1);
                return uidStr;
        }

        private String getUidString(Set<Integer> studentUids) {
                String uidStr = studentUids.toString().substring(1);
        uidStr = uidStr.substring(0, uidStr.length()-1);
                return uidStr;
        }

    private static Map<String, String> queryKeyMap = new HashMap<String, String>();

    static {
        queryKeyMap.put(HAS_LAST_LOGIN_KEY,    "STUDENT_LAST_LOGIN");
        queryKeyMap.put(HAS_LAST_QUIZ_KEY,     "STUDENT_LAST_QUIZ");
        queryKeyMap.put(HAS_PASSING_COUNT_KEY, "STUDENT_PASSING_COUNT");
        queryKeyMap.put(HAS_TUTORING_USE_KEY,  "STUDENT_TUTORING_USE");
    }

    public List<StudentModelI> getStudentExtendedData(final Connection conn, String hasFieldKey, List<Integer>uids) throws Exception {

        List<StudentModelI> l = null;

        String queryKey = queryKeyMap.get(hasFieldKey);

        if (queryKey == null) throw new IllegalStateException("extended data requested for: " + hasFieldKey);

        String sql = CmMultiLinePropertyReader.getInstance().getProperty(queryKey);

        String uidStr = getUidString(uids);

        String sqlWithUids = sql.replaceFirst("XXX", uidStr);

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
                ps = conn.prepareStatement(sqlWithUids);
                rs = ps.executeQuery();
                l = loadStudentExtendedData(hasFieldKey, rs);
        } catch (Exception e) {
            __logger.error(String.format("*** Error getting student extended data for uids: %s, sql: ", uidStr, sqlWithUids), e);
                        throw new Exception("*** Error getting student extended data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }


/*
        private void loadChapInfo(final Connection conn, List<StudentModelBaseI> l) throws Exception {

                CmAdminDao dao = CmAdminDao.getInstance();

                for (StudentModelBaseI sm : l) {
                        StudentUserProgramModelBase upm = sm.getUserProgramModel();
                        String chapter = upm.getChapter();
                        if (chapter != null) {
                                String subjId = upm.getTestDefBase().getSubjId();
                        List <ChapterModel> cmList = dao.getChaptersForProgramSubject(conn, "Chap", subjId);
                        for (ChapterModel cm : cmList) {
                                if (cm.getTitle().equals(chapter)) {
                                        upm.setChapterNumber(Integer.valueOf(cm.getNumber()));
                                        break;
                                }
                        }
                        }
                }
        }
*/


    /** Load chapter information in the the program description and
     *  set a value on the Studen Model?
     *
     *  Question: should the model be immutable?  Why would'nt the
     *  program model be responsible for the program?
     *
     */
        private void loadChapterInfo(final Connection conn, List<StudentModelI> l) throws Exception {
                for (StudentModelI sm : l) {
                        loadChapterInfo(sm);
                }
        }

        private void loadChapterInfo(StudentModelI sm) throws Exception {
            String chapter = sm.getChapter();
        if (chapter != null) {
            String subjId = sm.getProgram().getSubjectId();
            List <ChapterModel> cmList = CmAdminDao.getInstance().getChaptersForProgramSubject("Chap", subjId);
            for (ChapterModel cm : cmList) {
                if (cm.getTitle().equals(chapter)) {
                    sm.getProgram().setProgramDescription(new StringBuilder(sm.getProgram().getProgramDescription()).append(" ").append(cm.getNumber()).toString());
                    break;
                }
            }
        }
        }

        private List<StudentModelI> loadStudentExtendedData(String hasFieldKey, ResultSet rs) throws Exception {
                List<StudentModelI> l = new ArrayList<StudentModelI>();

        while (rs.next()) {
            StudentModelI sm = new StudentModel();

            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_uid"));

            sm.setTotalUsage(0);

            if (HAS_LAST_LOGIN_KEY.equals(hasFieldKey)) {
                sm.setLastLogin(rs.getString("last_use_date"));
            }

            else if (HAS_LAST_QUIZ_KEY.equals(hasFieldKey)) {
                sm.setLastQuiz(rs.getString("last_quiz"));
            }

            else if (HAS_PASSING_COUNT_KEY.equals(hasFieldKey)) {
                sm.setNotPassingCount(rs.getInt("not_passing_count"));
                sm.setPassingCount(rs.getInt("passing_count"));
            }

            else if (HAS_TUTORING_USE_KEY.equals(hasFieldKey)) {
                sm.setTutoringUse(rs.getInt("tutoring_use"));
            }

            l.add(sm);
        }

                return l;
        }

        public Boolean isTutoringEnabledForAdmin(final Connection conn, Integer aid) throws Exception {
            AccountInfoModel aim = CmAdminDao.getInstance().getAccountInfo(aid);
            String hasTutoring = aim.getHasTutoring();
            if(hasTutoring != null && hasTutoring.equals("Enabled")) {
                return true;
            }
            else {
                return false;
            }
        }

    /** Return list of LessionItemModels that represent the unique
     *  list of lessons applied to this runid
     *
     * @param conn
     * @param runId
     * @return
     * @throws Exception
     */
    public List <LessonItemModel> getLessonItemsForTestRun(final Connection conn, Integer runId) throws Exception {
            List <LessonItemModel> l = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("TEST_RESULTS"));
                ps.setInt(1, runId);
                rs = ps.executeQuery();

                if (rs.next()) {
                        l = loadLessonItems(conn, runId, rs);
                        sortLessonItems(l);
                }
        }
        catch (Exception e) {
                __logger.error(String.format("*** Error getting lesson items for runId:  %d", runId), e);
                throw new Exception("*** Error getting Lesson Items ***");
        }
        finally {
                SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }


    public StudentModelI addStudent(final Connection conn, StudentModelI sm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        Boolean isDuplicate = checkForDuplicatePasscode(conn, sm);
        if (isDuplicate) {
            throw new CmUserException("The passcode you entered is already in use, please try again.");
        }
        isDuplicate = checkForDuplicateName(conn, sm);
        if (isDuplicate) {
            throw new CmUserException("The name you entered is already in use, please try again.");
        }

        try {
            sm.setBackgroundStyle(getBackgroundImageRamdom());

            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("ADD_STUDENT_SQL"));
            ps.setString(1, sm.getName());
            ps.setString(2, sm.getPasscode());
            ps.setInt(3, sm.getSectionNum());

            //TODO: getGroupId() returns null sometimes - need to determine why/how
            if (sm.getGroupId() == null) {
                __logger.warn(String.format("+++ addStudent(): with null group; Admin ID: %d, isDemoUser: %d",
                                sm.getAdminUid(), (sm.getIsDemoUser() != null && sm.getIsDemoUser()) ? 1 : 0));
            }

            int groupId;
            try {
                groupId = (sm.getGroupId() == null) ? GROUP_NONE_ID : Integer.parseInt(sm.getGroupId());
            }
            catch (NumberFormatException e) {
                //TODO: getGroupId() returns non-numeric sometimes - need to determine why/how
                groupId = GROUP_NONE_ID;
                __logger.warn(String.format("+++ addStudent(): with invalid group; Admin ID: %d, groupId: %s, isDemoUser: %d",
                                sm.getAdminUid(), sm.getGroupId(), (sm.getIsDemoUser() != null && sm.getIsDemoUser()) ? 1 : 0));
            }

            ps.setInt(4, groupId);
            ps.setString(5, sm.getProgram().getProgramType().getType());
            ps.setString(6, sm.getProgram().getSubjectId());
            ps.setInt(7, sm.getAdminUid());
            ps.setInt(8, (sm.getIsDemoUser() != null && sm.getIsDemoUser()) ? 1 : 0);
            ps.setString(9, sm.getBackgroundStyle());

            int count = ps.executeUpdate();
            if (count == 1) {
                int stuUid = SqlUtilities.getLastInsertId(conn);
                sm.setUid(stuUid);
                addStudentProgram(conn, sm);
                updateStudent(conn, sm);
                HaUserExtendedDao.resetUserExtendedLessonStatusForUid(conn, sm.getProgram(), sm.getUid());
            }
        } catch (Exception e) {
                String msg = String.format("Error adding Student: %s, Passcode: %s ***", sm.getName(), sm.getPasscode());
                __logger.error(msg, e);
            throw new Exception(msg, e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return sm;
    }

    public StudentModelI addStudentTemplate(StudentModelI sm, String templateType) throws Exception {

    	__logger.info("+++ progId: " + sm.getProgram().getProgramId());

        int count = getJdbcTemplate().update(
    	        "insert into HA_USER_TEMPLATE (name, password, admin_id, group_id, prog_inst_id, type, limit_games, show_work_required, stop_at_program_end, create_date) " +
    	        " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? now())", 
    	        new Object[] {sm.getName(), sm.getPasscode(), sm.getAdminUid(), sm.getGroupId(), sm.getProgram().getProgramId(), templateType, sm.getSettings().getLimitGames()?1:0,
    	        		      sm.getSettings().getShowWorkRequired()?1:0, sm.getSettings().getStopAtProgramEnd()?1:0,
    	        		      sm.getSettings().getDisableCalcAlways()?1:0, sm.getSettings().getDisableCalcQuizzes()?1:0});

        String sql = "select id from HA_USER_TEMPLATE where admin_id = ? and password = ?";
        Integer uid = getJdbcTemplate().queryForObject(sql, new Object[] { sm.getAdminUid(), sm.getPasscode() },
                new RowMapper<Integer>() {
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            Integer id = rs.getInt("id");
                            return id;
                        }
                        catch (Exception e) {
                            __logger.error("Error loading new ID", e);
                            throw new SQLException(e.getMessage(), e);
                        }
                    }
        });
        
        if (count == 1) {
            sm.setUid(uid);
        }
        
    	return sm;
    }

    /** Return two dim array showing registered
     *  and available background wallpapers.
     * @return
     */
    private String[][] getBackgrounds() {
         String bgs[][] = {
                {"Catchup Math","resource-container"},
                {"Clouds","resource-container-clouds"},
                {"Forest","resource-container-forest"},
                {"Meadow","resource-container-sunrise"},
                {"Mountain Bike","resource-container-bike1"},
                {"Snowman","resource-container-snowman"},
                {"Sunfield","resource-container-sunfield"},
                {"Tulips","resource-container-tulips"},
     };
     return bgs;
    }

    private String getBackgroundImageRamdom() {
        String bgMap[][] = getBackgrounds();
        int which = SbUtilities.getRandomNumber(bgMap.length);
        return bgMap[which][1];
    }

    public StringHolder unregisterStudents(final Connection conn, List<StudentModelI> smList) {
        int removeCount = 0;
        int removeErrorCount = 0;
        int deactivateCount = 0;
        int deactivateErrorCount = 0;
        PreparedStatement ps = null;

        for (StudentModelI sm : smList) {
            Statement stmt = null;
            ResultSet rsCheck = null;
            try {
                /*
                 *  Remove from DB if user has never used account
                 */
                stmt = conn.createStatement();
                Map<String,String> map = new HashMap<String, String>();
                map.put("uid", sm.getUid().toString());

                String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CAN_BE_DELETED",map);
                rsCheck = stmt.executeQuery(sql);
                if(!rsCheck.first()) {
                    removeUser(conn, sm);
                    removeCount++;
                    continue;
                }
            }
            catch (Exception e) {
                removeErrorCount++;
                __logger.error(String.format("*** Error removing student with uid: %d", sm.getUid()), e);
            }
            finally {
                SqlUtilities.releaseResources(rsCheck, stmt, null);
            }

            /*
             * account has been used, deactivate
             */
            try {
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("DEACTIVATE_STUDENT"));
                StringBuilder sb = new StringBuilder();
                sb.append(sm.getUid()).append(".").append(System.currentTimeMillis());
                ps.setString(1, sb.toString());
                ps.setInt(2, sm.getUid());
                if (ps.executeUpdate() < 1) {
                        deactivateErrorCount++;
                        __logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()));
                }
                deactivateCount++;
            }
            catch (Exception e) {
                deactivateErrorCount++;
                __logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()), e);
            }
            finally {
                SqlUtilities.releaseResources(null, ps, null);
            }
        }
        StringHolder sh = new StringHolder();
        StringBuilder sb = new StringBuilder();
        sb.append("{ deactivateCount: '").append(deactivateCount).append("', deactivateErrorCount: '").append(deactivateErrorCount).append("'");
        sb.append(", removeCount: '").append(removeCount).append("', removeErrorCount: '").append(removeErrorCount).append("' }");
        sh.setResponse(sb.toString());
        return sh;
    }

    /**
     * The student's passcode is set to their uid + '.' + current time in msec
     * to avoid "locking up" up the previous passcode and to prevent passcode
     * uniqueness collisions.
     *
     *
     * NOTE: if student has no history (no entry in HA_TEST), then delete
     *       from DB instead of deactivating.
     *
     */
    public StudentModel deactivateUser(StudentModel sm) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();

            /** Remove from DB if user has never used account
             *
             */
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                ResultSet rsCheck = stmt.executeQuery("select 'x' from HA_TEST where user_id = " + sm.getUid());
                if(!rsCheck.first()) {
                    removeUser(conn, sm);
                    return sm;
                }

            }
            finally {
                SqlUtilities.releaseResources(null,stmt,null);
            }

            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("DEACTIVATE_STUDENT"));
            StringBuilder sb = new StringBuilder();
            sb.append(sm.getUid()).append(".").append(System.currentTimeMillis());
            ps.setString(1, sb.toString());
            ps.setInt(2, sm.getUid());
            if (ps.executeUpdate() < 1) {
                __logger.error(String.format("user deactivation failed; SQL: %s", ps.toString()));
                throw new Exception(String.format("*** Error deactivating student with uid: %d", sm.getUid()));
            }
        } catch (Exception e) {
            __logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error deactivating student with uid: %d", sm.getUid()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
        return sm;

    }

    /**
     * Remove this user from the admin's scope
     *
     * @TODO: move to archive?
     *
     * @param sm
     *
     */
    public void removeUser(final Connection conn, StudentModelI sm) {
        final String REMOVE_USER_SQL = "delete from HA_USER where uid = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            __logger.info("Removing user: " + sm.getUid());
            ps = conn.prepareStatement(REMOVE_USER_SQL);
            ps.setInt(1, sm.getUid());
            if (ps.executeUpdate() == 0) {
                __logger.warn("User was not removed");
            }
        } catch (Exception e) {
                __logger.error(String.format("*** Error removing user with Uid: %d", sm.getUid()), e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    public StudentModelI updateStudent(final Connection conn, StudentModelI sm, Boolean studentChanged, Boolean programChanged,
            Boolean progIsNew, Boolean passcodeChanged, Boolean passPercentChanged) throws Exception {
    	return updateStudent(conn, sm, studentChanged, programChanged, progIsNew, passcodeChanged, passPercentChanged, true, false);
    }

    public StudentModelI updateStudent(final Connection conn, StudentModelI sm, Boolean studentChanged, Boolean programChanged,
            Boolean progIsNew, Boolean passcodeChanged, Boolean passPercentChanged, boolean resetMainProgram,
            boolean continueParallelProgram) throws Exception {
        if (passcodeChanged) {
            Boolean isDuplicate = checkForDuplicatePasscode(conn, sm);
            if (isDuplicate) {
                throw new CmUserException("The passcode you entered is already in use, please try again.");
            }
        }
        if (progIsNew) {
        	// if Student is in Parallel Program, save Active Info before changing Program
        	//TODO: handle Auto-Enroll Parallel Program transition
        	ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
        	
        	if (ppDao.isStudentInParallelProgram(sm.getUid()) == true) {
        		ppDao.updateProgramAssign(sm.getUid(), continueParallelProgram);

                if (continueParallelProgram) {
        			hotmath.gwt.cm_rpc.client.model.CmProgram cp = ppDao.getCmProgramForUserId(sm.getUid());

        			CmProgramAssign cmProgAssign = ppDao.getProgramAssignForUserIdAndUserProgId(sm.getUid(), sm.getProgram().getProgramId());
        			cmProgAssign.setCmProgram(cp);
        			cmProgAssign.setUserProgId(sm.getProgram().getProgramId());
        			cmProgAssign.setParallelProg(true);
        			cmProgAssign.setCurrentMainProg(false);
        			ppDao.addProgramAssignment(cmProgAssign);
                }

        	}
        	// if Student has Main Program in CM_PROGRAM_ASSIGN, conditionally reset it
        	if (resetMainProgram)
        		ppDao.resetMainProgram(sm.getUid());
        	
            addStudentProgram(conn, sm);
        }
        if (studentChanged)
            updateStudent(conn, sm);
        if (programChanged)
            updateStudentProgram(conn, sm);

        if (programChanged || progIsNew)
            HaUserExtendedDao.getInstance().resetUserExtendedLessonStatusForUid(conn, sm.getUid());

        if (passPercentChanged) {
                Integer passPercent = getPercentFromString(sm.getPassPercent());

                CmUserProgramDao.getInstance().setProgramPassPercent(conn, sm.getProgram().getProgramId(), passPercent);
        }
        return sm;
    }

    // TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_PASSCODE_SQL = "CHECK_DUPLICATE_PASSCODE";

    public Boolean checkForDuplicatePasscode(final Connection conn, StudentModelI sm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(CHECK_DUPLICATE_PASSCODE_SQL));
            ps.setString(1, sm.getPasscode());
            ps.setInt(2, (sm.getUid() != null) ? sm.getUid() : -1);
            ps.setInt(3, sm.getAdminUid());
            ps.setInt(4, sm.getAdminUid());
            ps.setString(5, sm.getPasscode());
            ps.setInt(6, sm.getAdminUid());
            ps.setInt(7, sm.getAdminUid());
            ps.setString(8, sm.getPasscode());

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            __logger.error(String.format("*** Error checking passcode for student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error checking passcode for student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    // TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_NAME_SQL = "CHECK_DUPLICATE_NAME";

    public Boolean checkForDuplicateName(final Connection conn, StudentModelI sm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(CHECK_DUPLICATE_NAME_SQL));
            ps.setString(1, sm.getName().trim());
            ps.setInt(2, (sm.getUid() != null) ? sm.getUid() : -1);
            ps.setInt(3, sm.getAdminUid());

            if (__logger.isDebugEnabled()) {
                __logger.debug(String.format("+++ checkForDuplicateName(): query: %s", ps.toString()));
            }

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            __logger.error(String.format("*** Error checking name for student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error checking name for student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /** Return true if this admin has a user with password
     *
     * @param conn
     * @param adminId
     * @param password
     * @return
     */
    public Boolean checkPasswordInUse(final Connection conn, Integer adminId, String password) throws Exception {

        Statement stmt=null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1 from HA_USER where admin_id = " + adminId + " and user_passcode = '" + password + "'");
            return rs.first();
        }
        finally {
            SqlUtilities.releaseResources(null,stmt, null);
        }

    }


    /** Update the StudentModel
     *
     * @TODO: move the sectionNum to segmentNum and then move to ActiveInfo
     *
     * @param sm
     * @return
     * @throws Exception
     */
    public StudentModelI updateStudent(final Connection conn, StudentModelI sm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        long startTime = System.currentTimeMillis();

        try {
            StudentProgramModel spm = sm.getProgram();

            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_STUDENT_SQL"));
            ps.setString(1, sm.getName());
            ps.setString(2, sm.getPasscode());
            // ps.setString(3, sm.getEmail());

            // if group Id has not been set default to GROUP_NONE_ID
            ps.setInt(3, (sm.getGroupId() != null) ? Integer.parseInt(sm.getGroupId()):GROUP_NONE_ID);

            ps.setString(4, spm.getProgramType().getType());
            ps.setString(5, spm.getSubjectId());
            ps.setInt(6, spm.getProgramId());
            ps.setInt(7, sm.getSectionNum());
            ps.setString(8, sm.getBackgroundStyle());
            ps.setInt(9, sm.getUid());
            int result = ps.executeUpdate();

            updateStudentSettings(conn, sm, null);

        } catch (Exception e) {
            __logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error occurred while updating student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
            ClientInfo ci = ClientInfoHolder.get();
            if (ci == null) {
                ci = new ClientInfo();
                ci.setUserId(0);
                ci.setUserType(ClientInfo.UserType.UNKNOWN);
            }
            __logger.info(String.format("+++ updateStudent(): (userId:%d,userType:%s) elapsed time: %d msec",
                ci.getUserId(), ci.getUserType(), System.currentTimeMillis() - startTime));
        }
        return sm;
    }


    /** Update the main properties for this user.  The main properties
     * are the ones that directly control the users current program.
     *
     * @TODO: would be nice to have a way to update only the 'set' fields in a StudentModel
     *
     * @param conn
     * @param uid
     * @param showWorkRequired
     * @param tutoringAvailable
     * @param passPercent
     * @throws Exception
     */
    public void updateStudentMainProperties(final Connection conn, Integer uid, Boolean showWorkRequired, Boolean tutoringAvailable, Integer passPercent) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "update HA_USER set is_show_work_required = ?, is_tutoring_available = ? where uid = ?";
            ps = conn.prepareStatement(sql);
            ps.setBoolean(1, showWorkRequired);
            ps.setBoolean(2, tutoringAvailable);
            ps.setInt(3, uid);
            int cnt = ps.executeUpdate();

            // TODO: should we return if single user not found?
            if(cnt != 1)
                __logger.warn("user not found to update: " + uid);

            /** update the current program information with new pass percentages
             *
             */
            StudentModelI sm = getStudentModelBase(conn, uid);
            CmUserProgramDao.getInstance().setProgramPassPercent(conn, sm.getProgram().getProgramId(),passPercent);

        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }


    public StudentSettingsModel getStudentSettings(final Connection conn, Integer uid) throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;

        StudentSettingsModel mdl = new StudentSettingsModel();

        try {
            ps = conn.prepareStatement("SETTINGS_SELECT_SQL");
            ps.setInt(1, uid);
            rs = ps.executeQuery();
            if (rs.next()) {
                    mdl.setLimitGames(rs.getInt("limit_games") > 0);
                    mdl.setShowWorkRequired(rs.getInt("show_work_required") > 0);
                    mdl.setStopAtProgramEnd(rs.getInt("stop_at_program_end") > 0);
                    mdl.setTutoringAvailable(rs.getInt("tutoring_available") > 0);
                    mdl.setDisableCalcAlways(rs.getInt("diasable_calculator_always") > 0);
                    mdl.setDisableCalcQuizzes(rs.getInt("disable_calculator_quizzes") > 0);
            }
        }
        finally {
                SqlUtilities.releaseResources(rs, ps, null);
        }
        return mdl;
    }


    public void updateStudentSettings(final Connection conn, StudentModelI sm, Integer passPercent) throws Exception {
        StudentSettingsModel ssm = sm.getSettings();
        updateStudentSettings(conn, sm.getUid(), ssm.getShowWorkRequired(), ssm.getTutoringAvailable(),
                        ssm.getLimitGames(), ssm.getStopAtProgramEnd(), passPercent, ssm.getDisableCalcAlways(),
                        ssm.getDisableCalcQuizzes());
    }

    /** Update settings for the specified user.
     *
     * @param conn
     * @param uid
     * @param showWorkRequired
     * @param tutoringAvailable
     * @param limitGames
     * @param stopAtProgramEnd
     * @param passPercent
     * @throws Exception
     */
    public void updateStudentSettings(final Connection conn, Integer uid, Boolean showWorkRequired, Boolean tutoringAvailable,
        Boolean limitGames, Boolean stopAtProgramEnd, Integer passPercent, Boolean disableCalcAlways, Boolean disableCalcQuizzes) throws Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

                String insertSql = "insert into HA_USER_SETTINGS (limit_games, show_work_required, stop_at_program_end, tutoring_available, disable_calculator_always, disable_calculator_quizzes, user_id) values (?, ?, ?, ?, ?, ?, ?)";
                String updateSql = "update HA_USER_SETTINGS set limit_games=?, show_work_required=?, stop_at_program_end=?, tutoring_available=?, disable_calculator_always=?, disable_calculator_quizzes=? where user_id=?";

                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("SETTINGS_SELECT_SQL"));
                ps.setInt(1, uid);
                rs = ps.executeQuery();

                if (rs.next()) {
                        // perform update
                ps = conn.prepareStatement(updateSql);
                }
                else {
                        // perform insert
                ps = conn.prepareStatement(insertSql);
                }
                ps.setInt(1, (limitGames)?1:0);
                ps.setInt(2, (showWorkRequired)?1:0);
                ps.setInt(3, (stopAtProgramEnd)?1:0);
                ps.setInt(4, (tutoringAvailable)?1:0);
                int disable = (disableCalcAlways == null || disableCalcAlways == false)?0:1;
                ps.setInt(5, disable);
                disable = (disableCalcQuizzes == null || disableCalcQuizzes == false)?0:1;
                ps.setInt(6, disable);
                ps.setInt(7, uid);

            int cnt = ps.executeUpdate();

            if (cnt != 1) {
                __logger.warn(String.format("updateStudentOptions(): update failed for uid: %d, sql: %s", uid, ps.toString()));
            }

            /**
             *  update the current program information with new pass percentage (if not null)
             */
            if (passPercent != null) {
                StudentModelI sm = getStudentModelBase(conn, uid, true);  /** always include templates */
                CmUserProgramDao.getInstance().setProgramPassPercent(conn, sm.getProgram().getProgramId(), passPercent);
            }

        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    private static final String UPDATE_STUDENT_PROGRAM_SQL =
            "update CM_USER_PROGRAM " +
            "set pass_percent = ? " +
            "where id = ?";

    private static final String UPDATE_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL =
            "update CM_USER_PROGRAM " +
            "set pass_percent = null " +
            "where id = ?";

    public StudentModelI updateStudentProgram(final Connection conn, StudentModelI sm) throws Exception {
        PreparedStatement ps = null;

        long startTime = System.currentTimeMillis();
        try {
            String s = sm.getPassPercent();

            if (s != null) {
                int passPcnt = getPercentFromString(s);
                ps = conn.prepareStatement(UPDATE_STUDENT_PROGRAM_SQL);
                ps.setInt(1, passPcnt);
                ps.setInt(2, sm.getProgram().getProgramId());
            } else {
                ps = conn.prepareStatement(UPDATE_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL);
                ps.setInt(1, sm.getProgram().getProgramId());
            }

            int result = ps.executeUpdate();

            StudentActiveInfo activeInfo = new StudentActiveInfo();
            if (sm.getSectionNum() != null) activeInfo.setActiveSegment(sm.getSectionNum());
            setActiveInfo(conn, sm.getUid(), activeInfo);

        } catch (Exception e) {
            __logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error occurred while updating Student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
            __logger.info(String.format("+++ updateStudentProgram(): (userId:%d, userType:%s), elapsed time: %d msec",
                ClientInfoHolder.get().getUserId(), ClientInfoHolder.get().getUserType(), System.currentTimeMillis()-startTime));
        }
        return sm;
    }



    /**
     *
     * @param conn
     * @param sm
     * @return
     * @throws Exception
     */
    public StudentModelI addStudentProgram(final Connection conn, StudentModelI sm) throws Exception {

        /** check for setting of StudyProgram, and implement override
         *  We should deprecate using instance vars to identify the program
         */
        __logger.debug("addStudentProgram()");
        if(sm.getProgram().isCustom()) {
            return addStudentProgramCustom(conn, sm);
        }

        PreparedStatement ps = null;
        long startTime = System.currentTimeMillis();
        try {

            StudentProgramModel sp = sm.getProgram();

            setTestConfig(conn, sm);
            String pp = sm.getPassPercent();
            if (pp != null) {
                int passPcnt = getPercentFromString(pp);
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, sp.getProgramType().getType());
                ps.setString(4, sp.getSubjectId());
                ps.setInt(5, passPcnt);
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                ps.setString(7, sm.getJson());
                ps.setNull(8, Types.INTEGER);  /** custom program is null */
            } else {
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, sp.getProgramType().getType());
                ps.setString(4, sp.getSubjectId());
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setString(6, sm.getJson());
                ps.setNull(7, Types.INTEGER);  /** custom program is null */
            }
            __logger.info(ps);
            
            int result = ps.executeUpdate();
            if (result == 1) {
                // now get value of auto-increment id from CM_USER_PROGRAM
                int newKey = SqlUtilities.getLastInsertId(conn);
                sp.setProgramId(newKey);
            }

            if (sm.getSectionNum() == null) sm.setSectionNum(0);

            /** Setup random first quiz, if supported
             *
             */
            StudentActiveInfo info = new StudentActiveInfo();
            HaTestDef testDef = HaTestDefDao.getInstance().getTestDef(conn,sp.getProgramType().getType(), sp.getSubjectId() );
            if(testDef.getNumAlternateTests() > 0) {
                int randStartSeg = SbUtilities.getRandomNumber(testDef.getNumAlternateTests());
                info.setActiveSegmentSlot(randStartSeg);
            }
            __logger.info("Setting Active Info: " + info);
            setActiveInfo(conn, sm.getUid(), info);
        } catch (Exception e) {
            String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
            __logger.error(m, e);
            throw new Exception(m, e);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
            ClientInfo ci = ClientInfoHolder.get();
            if (ci == null) {
                ci = new ClientInfo();
                ci.setUserId(0);
                ci.setUserType(UserType.UNKNOWN);
            }
            __logger.info(String.format("+++ addStudentProgram(): (userId:%d,userType:%s), elapsed time: %d",
                        ci.getUserId(), ci.getUserType(), System.currentTimeMillis()-startTime));
        }
        return sm;
    }

    /**
    *
    * @param sm
    * @return
    * @throws Exception
    */
   public StudentModelI addStudentProgramForTemplate(StudentModelI sm) throws Exception {
	   
       Connection conn = null;
       PreparedStatement ps = null;
       long startTime = System.currentTimeMillis();

       try {
           conn = HMConnectionPool.getConnection();

           /** check for setting of StudyProgram, and implement override
            *  We should deprecate using instance vars to identify the program
            */
           if (sm.getProgram().isCustom()) {
               return addStudentProgramCustomForTemplate(conn, sm);
           }

           StudentProgramModel sp = sm.getProgram();

           setTestConfig(conn, sm);
           String pp = sm.getPassPercent();
           if (pp != null) {
               int passPcnt = getPercentFromString(pp);
               ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PARALLEL_PROGRAM_DEF_SQL"));
               ps.setInt(1, sm.getUid());
               ps.setInt(2, sm.getAdminUid());
               ps.setString(3, sp.getProgramType().getType());
               ps.setString(4, sp.getSubjectId());
               ps.setInt(5, passPcnt);
               ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
               ps.setString(7, sm.getJson());
               ps.setNull(8, Types.INTEGER);  /** custom program is null */
           } else {
               ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PARALLEL_PROGRAM_DEF_NULL_PASS_PERCENT_SQL"));
               ps.setInt(1, sm.getUid());
               ps.setInt(2, sm.getAdminUid());
               ps.setString(3, sp.getProgramType().getType());
               ps.setString(4, sp.getSubjectId());
               ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
               ps.setString(6, sm.getJson());
               ps.setNull(7, Types.INTEGER);  /** custom program is null */
           }
           __logger.info("--- addStudentProgramForTemplate(): SQL: " + ps.toString());
           
           int result = ps.executeUpdate();
           if (result == 1) {
               // now get value of auto-increment id from CM_USER_PROGRAM
               int newKey = SqlUtilities.getLastInsertId(conn);
               sp.setProgramId(newKey);
           }

           if (sm.getSectionNum() == null) sm.setSectionNum(0);

           /** Setup random first quiz, if supported
            *
            */
           StudentActiveInfo info = new StudentActiveInfo();
           HaTestDef testDef = HaTestDefDao.getInstance().getTestDef(conn,sp.getProgramType().getType(), sp.getSubjectId() );
           if(testDef.getNumAlternateTests() > 0) {
               int randStartSeg = SbUtilities.getRandomNumber(testDef.getNumAlternateTests());
               info.setActiveSegmentSlot(randStartSeg);
           }
           __logger.info("Setting Active Info: " + info);
           setActiveInfo(conn, sm.getUid(), info);
       } catch (Exception e) {
           String m = String.format("*** Error adding student program for student template with id: %d", sm.getUid());
           __logger.error(m, e);
           throw new Exception(m, e);
       } finally {
           SqlUtilities.releaseResources(null, ps, null);
           ClientInfo ci = ClientInfoHolder.get();
           if (ci == null) {
               ci = new ClientInfo();
               ci.setUserId(0);
               ci.setUserType(UserType.UNKNOWN);
           }
           __logger.info(String.format("+++ addStudentProgramForTemplate(): (userId:%d,userType:%s), elapsed time: %d",
                       ci.getUserId(), ci.getUserType(), System.currentTimeMillis()-startTime));
       }
       return sm;
   }

   /** add a new custom program based on contents of StudyProgram in StudentModel
     *
     *
     * A custom program is defined in HA_CUSTOM_PROGRAM with a 'id' that represents
     * a distinct custom program.  The test_config_json points to this id via its configuration:
     *
     * {custom_program_id:XXX}
     *
     * Where XXX is the actual custom program id.
     *
     *
     *
     * @param conn
     * @param sm
     * @return
     * @throws Exception
     */
    public StudentModelI addStudentProgramCustom(final Connection conn, StudentModelI sm) throws Exception {
        if(sm.getProgram().getCustom().getType() == Type.QUIZ) {
            return addStudentProgramCustomQuiz(conn, sm);
        }


        StudentProgramModel studyProgram = sm.getProgram();
        if(studyProgram == null)
            throw new CmException("StudentProgramModel cannot be null: " + sm);


        PreparedStatement ps = null;
        try {
            String pp = sm.getPassPercent();
            if (pp != null) {
                int passPcnt = getPercentFromString(pp);
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, studyProgram.getProgramType().getType());
                ps.setString(4, studyProgram.getSubjectId());
                ps.setInt(5, passPcnt);
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                ps.setString(7, sm.getJson());
                ps.setInt(8, sm.getProgram().getCustom().getCustomProgramId());
            } else {
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, studyProgram.getProgramType().getType());
                ps.setString(4, studyProgram.getSubjectId());
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setString(6, sm.getJson());
                ps.setInt(7, sm.getProgram().getCustom().getCustomProgramId());
            }
            int result = ps.executeUpdate();
            if (result == 1) {
                // now get value of auto-increment id from CM_USER_PROGRAM
                int newKey = SqlUtilities.getLastInsertId(conn);
                studyProgram.setProgramId(newKey);
            }

            sm.setSectionNum(0);
            setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());
        } catch (Exception e) {
            String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
            __logger.error(m, e);
            throw new Exception(m, e);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return sm;
    }
    

    /** add a new custom program based on contents of StudyProgram in StudentModel
    *
    *
    * A custom program is defined in HA_CUSTOM_PROGRAM with a 'id' that represents
    * a distinct custom program.  The test_config_json points to this id via its configuration:
    *
    * {custom_program_id:XXX}
    *
    * Where XXX is the actual custom program id.
    *
    *
    *
    * @param conn
    * @param sm
    * @return
    * @throws Exception
    */
   public StudentModelI addStudentProgramCustomForTemplate(final Connection conn, StudentModelI sm) throws Exception {

       StudentProgramModel studyProgram = sm.getProgram();
       if(studyProgram == null)
           throw new CmException("StudentProgramModel cannot be null: " + sm);

	   if(sm.getProgram().getCustom().getType() == Type.QUIZ) {
           return addStudentProgramCustomQuiz(conn, sm);
       }

       PreparedStatement ps = null;
       try {
           String pp = sm.getPassPercent();
           if (pp != null) {
               int passPcnt = getPercentFromString(pp);
               ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_SQL"));
               ps.setInt(1, sm.getUid());
               ps.setInt(2, sm.getAdminUid());
               ps.setString(3, studyProgram.getProgramType().getType());
               ps.setString(4, studyProgram.getSubjectId());
               ps.setInt(5, passPcnt);
               ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
               ps.setString(7, sm.getJson());
               ps.setInt(8, sm.getProgram().getCustom().getCustomProgramId());
           } else {
               ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL"));
               ps.setInt(1, sm.getUid());
               ps.setInt(2, sm.getAdminUid());
               ps.setString(3, studyProgram.getProgramType().getType());
               ps.setString(4, studyProgram.getSubjectId());
               ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
               ps.setString(6, sm.getJson());
               ps.setInt(7, sm.getProgram().getCustom().getCustomProgramId());
           }
           int result = ps.executeUpdate();
           if (result == 1) {
               // now get value of auto-increment id from CM_USER_PROGRAM
               int newKey = SqlUtilities.getLastInsertId(conn);
               studyProgram.setProgramId(newKey);
           }

           sm.setSectionNum(0);
           setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());
       } catch (Exception e) {
           String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
           __logger.error(m, e);
           throw new Exception(m, e);
       } finally {
           SqlUtilities.releaseResources(null, ps, null);
       }
       return sm;
   }


    public StudentModelI addStudentProgramCustomQuiz(final Connection conn, StudentModelI sm) throws Exception {


        StudentProgramModel studyProgram = sm.getProgram();
        if(studyProgram == null)
            throw new CmException("StudentProgramModel cannot be null: " + sm);


        PreparedStatement ps = null;
        try {
            String pp = sm.getPassPercent();
            int passPcnt = getPercentFromString(pp);
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_CUSTOM_QUIZ"));
            ps.setInt(1, sm.getUid());
            ps.setInt(2, sm.getAdminUid());
            ps.setString(3, studyProgram.getProgramType().getType());
            ps.setString(4, studyProgram.getSubjectId());
            ps.setInt(5, passPcnt);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setString(7, sm.getJson());
            ps.setInt(8, sm.getProgram().getCustom().getCustomQuizId());

            int result = ps.executeUpdate();
            if (result == 1) {
                // now get value of auto-increment id from CM_USER_PROGRAM
                int newKey = SqlUtilities.getLastInsertId(conn);
                studyProgram.setProgramId(newKey);
            }

            sm.setSectionNum(0);
            setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());
        } catch (Exception e) {
            String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
            __logger.error(m, e);
            throw new Exception(m, e);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return sm;

    }


    public StudentModelI addStudentProgramCustomQuizForTemplate(final Connection conn, StudentModelI sm) throws Exception {


        StudentProgramModel studyProgram = sm.getProgram();
        if(studyProgram == null)
            throw new CmException("StudentProgramModel cannot be null: " + sm);


        PreparedStatement ps = null;
        try {
            String pp = sm.getPassPercent();
            int passPcnt = getPercentFromString(pp);
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_CUSTOM_QUIZ"));
            ps.setInt(1, sm.getUid());
            ps.setInt(2, sm.getAdminUid());
            ps.setString(3, studyProgram.getProgramType().getType());
            ps.setString(4, studyProgram.getSubjectId());
            ps.setInt(5, passPcnt);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setString(7, sm.getJson());
            ps.setInt(8, sm.getProgram().getCustom().getCustomQuizId());

            int result = ps.executeUpdate();
            if (result == 1) {
                // now get value of auto-increment id from CM_USER_PROGRAM
                int newKey = SqlUtilities.getLastInsertId(conn);
                studyProgram.setProgramId(newKey);
            }

            sm.setSectionNum(0);
            setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());
        } catch (Exception e) {
            String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
            __logger.error(m, e);
            throw new Exception(m, e);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return sm;

    }

    /** Set the test config json for this student by reading
     *  the template json from HA_TEST_DEF and parsing/setting.
     *
     *  This actually will set the json field in the StudentModel.
     *
     *   Format the test config json that
     *   will provide custom configuration
     *   for this program.
     *
     *
     *  @TODO:  get chapter out of StudentModel, it should be in StudentModelProgramInfo
     *
     * @param conn
     * @param sm
     * @throws Exception
     */
    public void setTestConfig(final Connection conn, StudentModelI sm) throws Exception {

        ResultSet rs = null;
        PreparedStatement ps2 = null;
        String sql = "select test_config_json from HA_TEST_DEF where subj_id = ? and prog_id = ? and is_active = 1";
        String json = "";
        try {
            ps2 = conn.prepareStatement(sql);
            ps2.setString(1, sm.getProgram().getSubjectId());
            String progId = sm.getProgram().getProgramType().getType();
            ps2.setString(2, progId);
            rs = ps2.executeQuery();
            if (rs.next()) {
                json = rs.getString(1);
                // save JSON if chap program set chapter
                // @TODO: support multiple chapters
                if (progId.equalsIgnoreCase("chap")) {
                    // Delimit the chapter in quotes
                    // otherwise the parsing of the JSON fails.
                    json = json.replaceFirst("XXX", "'" + sm.getChapter() + "'");
                }
                sm.setJson(json);
            }

        } catch (Exception e) {
        	logger.error(String.format("Could not configure test for subj_id: %s, prog_id: %s",
        			sm.getProgram().getSubjectId(), sm.getProgram().getProgramType().getType()), e);
            throw new CmException("Could not configure test",e);
        } finally {
            SqlUtilities.releaseResources(rs, ps2, null);
        }
    }

    /**
     * Return list of StudentShowWorkModel that represent distinct list of
     * problems that actually have show work.
     *
     *
     * If run_id is passed, then limit to only run_id
     *
     * @TODO: modify SQL to restrict on run_id if passed. (if run_id == null,
     *        then return all, if run_id != null restrict)?
     *
     *
     *        viewed al
     * @param uid
     * @return
     * @throws Exception
     */
    public CmList<StudentShowWorkModel> getStudentShowWork(final Connection conn, int uid, Integer runId) throws Exception {

        CmList<StudentShowWorkModel> swModels = new CmArrayList<StudentShowWorkModel>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("WHITEBOARD_STUDENT_LIST");

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            ps.setInt(2,uid);
            rs = ps.executeQuery();

            SimpleDateFormat dteForat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            while (rs.next()) {

                /**
                 * Quick hack to restrict to runId if specified
                 *
                 * If runId is not set, then all records are returned.
                 *
                 */
                if (runId != null) {
                    if (rs.getInt("run_id") != runId)
                        continue;
                }

                String pid = rs.getString("pid");
                long timeMills = rs.getLong("insert_time_mills");

                Date dte = new Date(timeMills);
                String dteStr = dteForat.format(dte);

                StudentShowWorkModel s = new StudentShowWorkModel();
                s.setPid(pid);
                s.setInsertTimeMills(timeMills);
                s.setViewTime(dteStr);
                s.setLabel("");

                swModels.add(s);
            }
        } catch (Exception e) {
                __logger.error(String.format("*** Error getting show work for Uid: %d", uid), e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }

        return swModels;
    }

    /**
     * Create a complete StudentModel for the named student with user_id.
     *
     * @param uid
     *            The user_id of student
     * @return a new StudentModel
     *
     * @throws Exception
     *             if student not found
     */
    public StudentModelI getStudentModel(Integer uid) throws Exception {
        // default is without templates
        return getStudentModel(uid, false);
    }

    /** return list of StudentModelI objects matching list of uids.
     *
     * @param conn
     * @param uids
     * @return
     * @throws Exception
     */
    public List<StudentModelI> getStudentModels(final Connection conn, List<Integer> uids) throws Exception {
       List<StudentModelI> students = new ArrayList<StudentModelI>();
       for(Integer uid: uids) {
           students.add(getStudentModelBase(conn, uid));
       }
       return students;
    }

    /**
     * Return a StudentModel identified by uid.
     *
     * @param conn
     * @param uid
     * @return
     *
     * @throws Exception
     */
    public StudentModelI getStudentModel(final Connection conn, Integer uid) throws Exception {
        // default is without templates
        return getStudentModel(conn, uid, false);
    }


    /** Return student model named by uid
     *
     * @param uid
     * @param includeSelfRegTemplate  If true, then is_auto_create_template will be considered
     * @return
     *
     *
     * @throws Exception
     */
    public StudentModelI getStudentModel(Integer uid, Boolean includeSelfRegTemplate) throws Exception {

        Connection conn = null;

        try {
            conn = HMConnectionPool.getConnection();
            return getStudentModel(conn, uid, includeSelfRegTemplate);
        }
        catch (Exception e) {
            __logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

    /** Return student model named by uid
     *
     * @param conn
     * @param uid
     * @param includeSelfRegTemplate  If true, then is_auto_create_template will be considered
     * @return
     *
     *
     * @throws Exception
     */
    public StudentModelI getStudentModel(final Connection conn, final Integer uid, Boolean includeSelfRegTemplate)
            throws Exception {

        String sql = getStudentSql(StudentSqlType.SINGLE_STUDENT, includeSelfRegTemplate);
        StudentModelI studentModel = this.getJdbcTemplate().queryForObject(sql, new Object[] { uid,uid, 1 },
                new RowMapper<StudentModelI>() {
                    public StudentModelI mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            StudentModel sm = new StudentModel();
                            sm.setUid(rs.getInt("uid"));
                            sm.setAdminUid(rs.getInt("admin_uid"));
                            sm.setName(rs.getString("name"));
                            sm.setPasscode(rs.getString("passcode"));
                            sm.setEmail(rs.getString("email"));
                            sm.setTutoringUse(rs.getInt("tutoring_use"));

                            int groupId = rs.getInt("group_id");
                            sm.setGroupId(String.valueOf(groupId));
                            sm.setGroup(rs.getString("group_name"));

                            StudentProgramModel sprm = sm.getProgram();
                            sprm.setProgramDescription(rs.getString("program"));
                            sprm.setProgramId(rs.getInt("user_prog_id"));
                            sprm.setProgramType(rs.getString("prog_id"));
                            sprm.setSubjectId(rs.getString("subj_id"));
                            sprm.setCustom(new CustomProgramComposite(rs.getInt("custom_program_id"), rs
                                    .getString("custom_program_name"), rs.getInt("custom_quiz_id"), rs
                                    .getString("custom_quiz_name")));
                            sprm.setIsParallelProgram(rs.getInt("main_user_prog_id") != rs.getInt("user_prog_id"));

                            StudentSettingsModel mdl = sm.getSettings();
                            mdl.setLimitGames(rs.getInt("limit_games") > 0);
                            mdl.setShowWorkRequired(rs.getInt("show_work_required") > 0);
                            mdl.setStopAtProgramEnd(rs.getInt("stop_at_program_end") > 0);
                            mdl.setTutoringAvailable(rs.getInt("tutoring_available") > 0);
                            mdl.setDisableCalcAlways(rs.getInt("disable_calculator_always") > 0);
                            mdl.setDisableCalcQuizzes(rs.getInt("disable_calculator_quizzes") > 0);

                            sm.setLastQuiz(rs.getString("last_quiz"));
                            sm.setChapter(getChapter(rs.getString("test_config_json")));
                            sm.setLastLogin(rs.getString("last_use_date"));
                            sm.setTotalUsage(0);
                            sm.setNotPassingCount(rs.getInt("not_passing_count"));
                            sm.setPassingCount(rs.getInt("passing_count"));
                            String passPercent = rs.getString("pass_percent");
                            sm.setPassPercent(passPercent);
                            sm.setBackgroundStyle(rs.getString("gui_background_style"));
                            sm.setSectionNum(rs.getInt("active_segment"));

                            int activeTestId = rs.getInt("active_test_id");
                            boolean isComplete = rs.getDate("date_completed") != null;

                            setupSectionCount(sm, rs.getString("test_config_json"));

                            setupProgramStatus(sm, rs.getString("program"),
                                    rs.getInt("current_lesson"), rs.getInt("lesson_count"),
                                    rs.getInt("cnt_lessons_completed"),
                                    activeTestId, isComplete);

                            return sm;
                        } catch (Exception e) {
                            __logger.error("Error creating StudentUserProgramModel: " + uid, e);
                            throw new SQLException(e.getMessage(),e);
                        }
                    }
                });

        loadChapterInfo(studentModel);

        if (studentModel.getSettings().getTutoringAvailable()) {
            /**
             * make sure the admin has tutoring enabled too
             *
             */
            studentModel.getSettings().setTutoringAvailable(isTutoringEnabledForAdmin(conn, studentModel.getAdminUid()));
        }
        return studentModel;
    }

    /** Return list of students with specified admin_id and password
     *
     * @param conn
     * @param aid
     * @param password
     * @return
     * @throws Exception
     */
    public List<StudentModelI> getStudentModelByPassword(final Connection conn, Integer aid, String password) throws Exception {
        List<StudentModelI> students = new ArrayList<StudentModelI>();

        ResultSet rs = null;
        try {
            rs = conn.createStatement().executeQuery("select uid from HA_USER where admin_id = " + aid + " and user_passcode = '" + password + "'");
            while(rs.next()) {
                students.add( getStudentModelBase(conn, rs.getInt(1)));
            }
            return students;
        } finally {
            SqlUtilities.releaseResources(rs, null, null);
        }
    }

    /** Return list of students with specified admin_id and user name
     *
     * @param conn
     * @param aid
     * @param userName
     * @return
     * @throws Exception
     */
    public List<StudentModelI> getStudentModelByUserName(final Connection conn, Integer aid, String userName) throws Exception {
        List<StudentModelI> students = new ArrayList<StudentModelI>();

        ResultSet rs = null;
        try {
            rs = conn.createStatement().executeQuery("select uid from HA_USER where admin_id = " + aid + " and user_name = '" + userName + "'");
            while(rs.next()) {
                students.add( getStudentModelBase(conn, rs.getInt(1), true));
            }
            return students;
        } finally {
            SqlUtilities.releaseResources(rs, null, null);
        }
    }

    /** Return student model named by uid
     *
     * @param conn
     * @param uid
     * @return
     *
     * @throws Exception
     */
    public StudentModelI getStudentModelBase(final Connection conn, Integer uid) throws Exception {
        return getStudentModelBase(conn, uid, false);
    }

    /** Return student model named by uid
     *
     * @param conn
     * @param uid
     * @param includeSelfRegTemplate  If false, then records with is_auto_create_template=1 will be excluded
     * @return
     *
     *
     * @throws Exception
     */
    public StudentModelI getStudentModelBase(final Connection conn, Integer uid, Boolean includeSelfRegTemplate) throws Exception {

        long timeStart = System.currentTimeMillis();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(getStudentSummarySql(StudentSqlType.SINGLE_STUDENT, includeSelfRegTemplate));
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            ps.setInt(3, 1);
            rs = ps.executeQuery();

            List<StudentModelI> l = null;
            l = loadStudentBaseSummaries(rs, null);
            if (l.size() == 0)
                throw new Exception(String.format("Student with UID: %d was not found", uid));
            if (l.size() > 1)
                throw new Exception(String.format("Student with UID: %d matches more than one row", uid));

            loadChapterInfo(conn, l);

            StudentModelI sm = l.get(0);

            if(sm.getSettings().getTutoringAvailable()) {
                /**
                 * make sure the admin has tutoring enabled
                 */
                //TODO: incorporate Admin tutoring value in Summary SQL
                sm.getSettings().setTutoringAvailable(isTutoringEnabledForAdmin(conn, sm.getAdminUid()));
            }
            if (sm.getSectionNum() == null) sm.setSectionNum(0);
            return sm;
        } catch (Exception e) {
            __logger.error(String.format("*** Error obtaining data for student UID: %d, includeSelfRegTemplate: %s", uid, includeSelfRegTemplate), e);
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
            __logger.debug(String.format("End getStudentModelBase(), UID: %d, elapsed msecs: %d", uid, (System.currentTimeMillis() - timeStart)));
        }
    }

    /** Return student model for template named by uid
    *
    * @param conn
    * @param uid
    * @return
    *
    * @throws Exception
    */
   public StudentModelI getStudentModelBaseForTemplate(final Connection conn, Integer uid) throws Exception {

       long timeStart = System.currentTimeMillis();
       PreparedStatement ps = null;
       ResultSet rs = null;

       try {
           ps = conn.prepareStatement(getStudentSummarySql(StudentSqlType.SINGLE_STUDENT, false));
           ps.setInt(1, uid);
           ps.setInt(2, uid);
           ps.setInt(3, 1);
           rs = ps.executeQuery();

           List<StudentModelI> l = null;
           l = loadStudentBaseSummaries(rs, null);
           if (l.size() == 0)
               throw new Exception(String.format("Student with UID: %d was not found", uid));
           if (l.size() > 1)
               throw new Exception(String.format("Student with UID: %d matches more than one row", uid));

           loadChapterInfo(conn, l);

           StudentModelI sm = l.get(0);

           if(sm.getSettings().getTutoringAvailable()) {
               /**
                * make sure the admin has tutoring enabled
                */
               //TODO: incorporate Admin tutoring value in Summary SQL
               sm.getSettings().setTutoringAvailable(isTutoringEnabledForAdmin(conn, sm.getAdminUid()));
           }
           if (sm.getSectionNum() == null) sm.setSectionNum(0);
           return sm;
       } catch (Exception e) {
           __logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
           throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
       } finally {
           SqlUtilities.releaseResources(rs, ps, null);
           __logger.info(String.format("End getStudentModelBaseForTemplate(), UID: %d, elapsed seconds: %d", uid, ((System.currentTimeMillis() - timeStart)/1000)));
       }
   }

    private List<StudentModelI> loadStudentSummaries(ResultSet rs) throws Exception {

        List<StudentModelI> l = new ArrayList<StudentModelI>();

        while (rs.next()) {
            StudentModel sm = new StudentModel();
            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_uid"));
            sm.setName(rs.getString("name"));
            sm.setPasscode(rs.getString("passcode"));
            sm.setEmail(rs.getString("email"));
            sm.setTutoringUse(rs.getInt("tutoring_use"));

            int groupId = rs.getInt("group_id");
            sm.setGroupId(String.valueOf(groupId));
            sm.setGroup(rs.getString("group_name"));

            StudentProgramModel sprm = sm.getProgram();
            sprm.setProgramDescription(rs.getString("program"));
            sprm.setProgramId(rs.getInt("user_prog_id"));
            sprm.setIsActiveProgram(rs.getInt("user_prog_id") == rs.getInt("main_user_prog_id"));

            String progId = rs.getString("prog_id").toUpperCase();
            progId = (progId != null) ? progId.replaceAll(" ", "").replaceAll("-", "") : progId;

            /** What to do if CmProgramType.valueOf() does not exist?   It throws nasty exception!
                TODO:   need better way to determine CmProgramType.
             */
            CmProgramType progType = (progId != null) ? CmProgramType.lookup(progId) : null;


            sprm.setProgramType(progType);

            sprm.setSubjectId(rs.getString("subj_id"));
            sprm.setCustom(new CustomProgramComposite(
            		rs.getInt("custom_program_id"), rs.getString("custom_program_name"),
            		rs.getInt("custom_quiz_id"), rs.getString("custom_quiz_name")));


            StudentSettingsModel mdl = sm.getSettings();
            mdl.setLimitGames(rs.getInt("limit_games") > 0);
            mdl.setShowWorkRequired(rs.getInt("show_work_required") > 0);
            mdl.setStopAtProgramEnd(rs.getInt("stop_at_program_end") > 0);
            mdl.setTutoringAvailable(rs.getInt("tutoring_available") > 0);
            mdl.setDisableCalcAlways(rs.getInt("disable_calculator_always") > 0);
            mdl.setDisableCalcQuizzes(rs.getInt("disable_calculator_quizzes") > 0);

            sm.setLastQuiz(rs.getString("last_quiz"));
            sm.setChapter(getChapter(rs.getString("test_config_json")));
            sm.setLastLogin(rs.getString("last_use_date"));
            sm.setTotalUsage(0);
            sm.setNotPassingCount(rs.getInt("not_passing_count"));
            sm.setPassingCount(rs.getInt("passing_count"));
            String passPercent = rs.getString("pass_percent");
            sm.setPassPercent(passPercent);
            sm.setBackgroundStyle(rs.getString("gui_background_style"));
            sm.setSectionNum(rs.getInt("active_segment"));

            int activeTestId = rs.getInt("active_test_id");
            boolean isComplete = rs.getDate("date_completed") != null;

            setupSectionCount(sm, rs.getString("test_config_json"));

            setupProgramStatus(sm, rs.getString("program"), rs.getInt("current_lesson"), rs.getInt("lesson_count"), rs.getInt("cnt_lessons_completed"), activeTestId, isComplete);

            l.add(sm);
        }
        return l;
    }


    /** Format status and or custom program label for display.
     *
     *  For custom programs, add the CP:PROG_NAME and looks up the status info
     *
     */
    private void setupProgramStatus(StudentModelI student, String programName, int currentLesson,int lessonCount, int countLessonsCompleted, int activeTestId, boolean isCompleted) {

        StudentProgramModel program = student.getProgram();
        if (program.isCustom() == false) {
            program.setProgramDescription(programName);
            student.setStatus(getStatus(program.getProgramId(), student.getSectionNum(), student.getSectionCount()));
        }
        else if(program.getCustom().getType() == Type.LESSONS) {
            program.setProgramDescription("CP: " + program.getCustom().getCustomProgramName());
            student.setStatus(getStatusCustomProgram(countLessonsCompleted, currentLesson, lessonCount));
        }
        else if(program.getCustom().getType() == Type.QUIZ) {
            program.setProgramDescription("CQ: " + program.getCustom().getCustomQuizName());
            student.setStatus(getStatusCustomQuiz(activeTestId, isCompleted));
        }
    }

    private List<StudentModelI> loadStudentBaseSummaries(ResultSet rs, Boolean tutoringEnabledForAdmin) throws Exception {

        List<StudentModelI> l = new ArrayList<StudentModelI>();

        while (rs.next()) {
            StudentModelI sm = new StudentModelBase();

            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_uid"));
            sm.setName(rs.getString("name"));
            sm.setPasscode(rs.getString("passcode"));
            sm.setEmail(rs.getString("email"));
            sm.setIsDemoUser(rs.getInt("is_demo") > 0);

            StudentSettingsModel mdl = sm.getSettings();
            mdl.setLimitGames(rs.getInt("limit_games") > 0);
            mdl.setShowWorkRequired(rs.getInt("show_work_required") > 0);
            mdl.setStopAtProgramEnd(rs.getInt("stop_at_program_end") > 0);
            mdl.setTutoringAvailable(rs.getInt("tutoring_available") > 0 && ((tutoringEnabledForAdmin == null) || tutoringEnabledForAdmin));
            mdl.setDisableCalcAlways(rs.getInt("disable_calculator_always") > 0);
            mdl.setDisableCalcQuizzes(rs.getInt("disable_calculator_quizzes") > 0);

            sm.setBackgroundStyle(rs.getString("gui_background_style"));

            sm.setGroupId(String.valueOf(rs.getInt("group_id")));
            sm.setGroup(rs.getString("group_name"));

            sm.setChapter(getChapter(rs.getString("test_config_json")));
            sm.setJson(rs.getString("test_config_json"));
            sm.setSectionNum(rs.getInt("active_segment"));
            sm.setPassPercent(rs.getInt("pass_percent") + "%");

            StudentProgramModel program = sm.getProgram();
            program.setProgramId(rs.getInt("user_prog_id"));
            program.setSubjectId(rs.getString("subj_id"));
            program.setProgramType(rs.getString("prog_id"));

            program.setCustom(new CustomProgramComposite(
                    rs.getInt("custom_program_id"),rs.getString("custom_program_name"),
                    rs.getInt("custom_quiz_id"), rs.getString("custom_quiz_name")
                    ));

            sm.setProgram(program);

            int activeTestId = rs.getInt("active_test_id");
            boolean isCompleted = rs.getDate("date_completed") != null;

            setupSectionCount(sm, rs.getString("test_config_json"));

            setupProgramStatus(sm, rs.getString("program"),
                rs.getInt("current_lesson"), rs.getInt("lesson_count"), rs.getInt("cnt_lessons_completed"), activeTestId, isCompleted);

            l.add(sm);
        }
        return l;
    }

        private void setupSectionCount(StudentModelI student, String testConfigJson) throws JSONException {
                int sectionCount = 0;
                try {
                        StudentProgramModel prog = student.getProgram();
                        if (! prog.isCustom()) {
                            if(testConfigJson != null) {
                                JSONObject jo = new JSONObject(testConfigJson);
                                sectionCount = jo.getInt("segments");
                                prog.setSectionCount(sectionCount);
                            }
                        }
                }
                catch (Exception e) {
            __logger.error(String.format("*** Error getting section count for user_id: %d, user_prog_id: %d, test_config_json: %s",
                        student.getUid(), student.getProgram().getProgramId(), testConfigJson), e);
                }
                student.setSectionCount(sectionCount);
    }

    private String getStatus(Integer userProgId, Integer activeSegment, Integer segmentCount) {
        if (activeSegment > 0) {
            StringBuilder sb = new StringBuilder("Section ").append(activeSegment).append(" of ").append(segmentCount);
            return sb.toString();
        }
        return "Not started";
    }

    private String getStatusCustomProgram(int countLessonsCompleted, int currentLesson, int lessonCount) {
        if (currentLesson > 0) {
            if (countLessonsCompleted < lessonCount) {
                StringBuilder sb = new StringBuilder();
                return sb.append(countLessonsCompleted).append(" of " ).append(lessonCount).append(" completed").toString();
            }
            return "Completed";
        }
        return "Not started";
    }

    private String getStatusCustomQuiz(int currentTestId, boolean isCompleted) {
        if(!isCompleted && currentTestId > 0) {
            return "Started";
        }
        else if(isCompleted) {
            return "Completed";
        }
        else {
            return "Not Started";
        }
    }

    private List<StudentModelI> loadStudentExtendedSummaries(ResultSet rs) throws Exception {

        List<StudentModelI> l = new ArrayList<StudentModelI>();

        while (rs.next()) {
            StudentModelI sm = new StudentModel();

            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_uid"));

            sm.setTotalUsage(0);

            sm.setLastQuiz(rs.getString("last_quiz"));
            sm.setLastLogin(rs.getString("last_use_date"));
            sm.setNotPassingCount(rs.getInt("not_passing_count"));
            sm.setPassingCount(rs.getInt("passing_count"));
            sm.setTutoringUse(rs.getInt("tutoring_use"));

            // logger.debug("+++ passing count: " + sm.getPassingCount() + ", not passing count: " + sm.getNotPassingCount());

            l.add(sm);
        }
        return l;
    }

    /** Load all lessons referenced by this runId, including any chapter programs
     *
     * @param runId
     * @param rs
     * @return
     * @throws Exception
     */
    private List<LessonItemModel> loadLessonItems(final Connection conn, Integer runId, ResultSet rs) throws Exception {

        List<LessonItemModel> l = new ArrayList<LessonItemModel>();

            HaTestDefDescription tdDesc = null;
            HaTestRun testRun = HaTestRunDao.getInstance().lookupTestRun(runId);
            tdDesc = HaTestDefDescription.getHaTestDefDescription(testRun);

                // identify incomplete topics
                Set <String> assignedTopics = new HashSet<String>();
                do {
                        if (!"correct".equalsIgnoreCase(rs.getString("answer_status"))) {
                            assignedTopics.add(rs.getString("file"));
                        }
                } while (rs.next());

        for (InmhItemData item : tdDesc.getLessonItems()) {
                LessonItemModel mdl = new LessonItemModel();
                mdl.setName(item.getInmhItem().getTitle());
                String file = item.getInmhItem().getFile();
                mdl.setFile(file);
                mdl.setPrescribed((assignedTopics.contains(file))?"Prescribed":"");
                l.add(mdl);
        }
        return l;
    }

    private void sortLessonItems(List<LessonItemModel> list) {
        Collections.sort(list, new Comparator<LessonItemModel>() {
            public int compare(LessonItemModel o1, LessonItemModel o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private String getChapter(String json) {
        if (json == null || json.trim().length() == 0)
            return null;

        String chap = null;
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.has("chapters")) {
                JSONArray ja = jo.getJSONArray("chapters");
                chap = ja.getString(0);
            }
        } catch (Exception e) {
            __logger.error("*** Error extracting Chapter from JSON", e);
        }

        return chap;
    }

    /**
     * get auto-create template for specified Group ID
     *
     * @param conn
     * @param groupId
     * @return
     * @throws Exception
     */
    public StudentModelI getTemplateForSelfRegGroup(final Connection conn, Integer groupId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "select uid from HA_USER where group_id = ? and is_auto_create_template = 1";

        try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, groupId);
                rs = ps.executeQuery();
                if (rs.first()) {
                        int uid = rs.getInt(1);
                        return getStudentModelBase(conn, uid, true);
                }
                else {
                throw new Exception(String.format("Auto Reg Template for Group ID: %d was not found", groupId));
                }
        }
        finally {
                SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Load this user's currently active state information. This shows the
     * current test/run and session the user is currently viewing.
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentActiveInfo loadActiveInfo(final Integer userId) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("LOAD_ACTIVE_INFO");
        StudentActiveInfo activeInfo = null;
        try {
            activeInfo = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<StudentActiveInfo>() {
                    public StudentActiveInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentActiveInfo activeInfo = new StudentActiveInfo();
                        try {
                            activeInfo.setActiveRunId(rs.getInt("active_run_id"));
                            activeInfo.setActiveRunSession(rs.getInt("active_run_session"));
                            activeInfo.setActiveSegment(rs.getInt("active_segment"));
                            activeInfo.setActiveTestId(rs.getInt("active_test_id"));
                            activeInfo.setActiveSegmentSlot(rs.getInt("active_segment_slot"));

                            return activeInfo;
                        }
                        catch(Exception e) {
                            __logger.error("Error creating StudentActiveInfo: " + userId, e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        }
        catch (Exception e) {
        	__logger.error(String.format("Error querying for StudentActiveInfo; userId: %d, SQL; %s", userId, sql), e);
        	throw e;
        }

        /** Check to see if this program supports alternate tests, if so
         *  pass along the segment_slot to use .. otherwise, make sure it
         *  is zero to always use the same slot
         */
        if (activeInfo != null && activeInfo.getActiveSegmentSlot() > 0) {
                CmUserProgramDao upDao = CmUserProgramDao.getInstance();
            if(upDao.loadProgramInfoCurrent(userId).getTestDef().getNumAlternateTests() == 0)
               activeInfo.setActiveSegmentSlot(0);
        }
        return activeInfo;
    }


    /** Move this user to the next quiz slot or back to zero
     *
     *  A quiz slot acts like a circular data structure, rolling over back to zero.
     * @param conn
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentActiveInfo moveToNextQuizSegmentSlot(final Connection conn, Integer userId, int numSlotsInProgram) throws Exception {
        StudentActiveInfo activeInfo = loadActiveInfo(userId);

        int segmentSlot = activeInfo.getActiveSegmentSlot();
        segmentSlot++;

        if(segmentSlot > (numSlotsInProgram-1))
            segmentSlot = 0; // roll over, back to zero

        activeInfo.setActiveSegmentSlot(segmentSlot);
        setActiveInfo(conn, userId, activeInfo);

        return activeInfo;
    }

    /**
     * Set the active information for the named user
     *
     * @TODO: Move to 1-to-1 table HA_USER_ACTIVE, or set is_active flag in CM_USER_PROGRAM
     * @TODO: move all active info to here (including the studentModel.getSection)
     * @param conn
     * @param userId
     * @param activeInfo
     * @throws Exception If record cannot be updated
     *
     */
    public void setActiveInfo(final Connection conn, Integer userId, StudentActiveInfo activeInfo) throws Exception {

        getSimpleJdbcTemplate().update(
                CmMultiLinePropertyReader.getInstance().getProperty("SAVE_ACTIVE_INFO"),
                new Object[]{
                    activeInfo.getActiveRunId(),
                    activeInfo.getActiveTestId(),
                    activeInfo.getActiveSegment(),
                    activeInfo.getActiveSegmentSlot(),
                    activeInfo.getActiveRunSession(),
                    userId});
        
    }

    /**
     * Set the active information for the named user
     *
     * @TODO: Move to 1-to-1 table HA_USER_ACTIVE, or set is_active flag in CM_USER_PROGRAM
     * @TODO: move all active info to here (including the studentModel.getSection)
     * @param conn
     * @param userId
     * @param activeInfo
     * @throws Exception If record cannot be updated
     *
     */
    public void setActiveInfoAndUserProgId(Integer userId, StudentActiveInfo activeInfo, int userProgId) throws Exception {

        getSimpleJdbcTemplate().update(
                CmMultiLinePropertyReader.getInstance().getProperty("SAVE_ACTIVE_INFO_AND_USER_PROG_ID"),
                new Object[]{
                    activeInfo.getActiveRunId(),
                    activeInfo.getActiveTestId(),
                    activeInfo.getActiveSegment(),
                    activeInfo.getActiveSegmentSlot(),
                    activeInfo.getActiveRunSession(),
                    userProgId,
                    userId});
    }

    /**
     * Set the user program ID for the named user
     *
     * @param userId
     * @param userProgId
     * @throws Exception If record cannot be updated
     *
     */
    public void setUserProgId(Integer userId, Integer userProgId) throws Exception {

        getSimpleJdbcTemplate().update(
                CmMultiLinePropertyReader.getInstance().getProperty("SAVE_USER_PROG_ID"),
                new Object[]{
                    userProgId,
                    userId});
    }


    /** Helper function to assign the named program/subject to the student identified by uid
     *
     * @param uid
     * @param program
     * @param chapter
     * @throws Exception
     */
    public void assignProgramToStudent(final Connection conn, Integer uid, CmProgram program, String chapter) throws Exception {
        StudentProgramModel progToAssign = new StudentProgramModel();
        progToAssign.setProgramType(program.getProgramType());
        progToAssign.setSubjectId(program.getSubject());
        assignProgramToStudent(conn, uid, progToAssign, chapter, null);
    }

    public void assignProgramToStudent(final Connection conn, Integer uid, StudentUserProgramModel userProgram) throws Exception {
        StudentProgramModel progToAssign = new StudentProgramModel();
        HaTestDef testDef = HaTestDefDao.getInstance().getTestDef(userProgram.getTestDefId());
        progToAssign.setProgramType(testDef.getProgId());
        progToAssign.setSubjectId(testDef.getSubjectId());
        assignProgramToStudent(conn, uid, progToAssign, testDef.getChapter(), null);
    }

    public void assignProgramToStudent(final Connection conn, Integer uid, StudentUserProgramModel userProgram, String chapter) throws Exception {
        StudentProgramModel progToAssign = new StudentProgramModel();
        HaTestDef testDef = HaTestDefDao.getInstance().getTestDef(userProgram.getTestDefId());
        progToAssign.setProgramType(testDef.getProgId());
        progToAssign.setSubjectId(testDef.getSubjectId());
        assignProgramToStudent(conn, uid, progToAssign, chapter, null);
    }

    public void assignProgramToStudent(final Connection conn, Integer uid, StudentProgramModel program, String chapter, String passPercent) throws Exception {
        assignProgramToStudent(conn, uid, program, chapter, passPercent, null, false, 0, true, false);
    }

    public void assignProgramToStudent(final Connection conn, Integer uid, StudentProgramModel program, String chapter, String passPercent,
    		StudentSettingsModel settings, boolean includeSelfRegGroups, Integer sectionNum) throws Exception {
        assignProgramToStudent(conn, uid, program, chapter, passPercent, settings, includeSelfRegGroups, sectionNum, true, false);
    }
    
    public void assignProgramToStudent(final Connection conn, Integer uid, StudentProgramModel program, String chapter, String passPercent, boolean resetMainProgram) throws Exception {
        assignProgramToStudent(conn, uid, program, chapter, passPercent, null, false, 0, resetMainProgram, false);
    }

    /** Assign program to student.
     *
     * @param conn
     * @param uid
     * @param program
     * @param chapter
     * @param passPercent
     * @param settings
     * @param includeSelfRegGroups  (must be set to true if updating self-reg user template)
     * @param sectionNum
     * @param resetMainProgram
     * @param continueParallelProgram
     * @throws Exception
     */
    public void assignProgramToStudent(final Connection conn, Integer uid, StudentProgramModel program, String chapter, String passPercent,
    		StudentSettingsModel settings, boolean includeSelfRegGroups, Integer sectionNum, boolean resetMainProgram,
    		boolean continueParallelProgram) throws Exception {

        StudentModelI sm = getStudentModelBase(conn, uid,includeSelfRegGroups);

        sm.getProgram().setProgramType(program.getProgramType());
        sm.getProgram().setSubjectId(program.getSubjectId());
        CustomProgramComposite cpComp = sm.getProgram().getCustom();
        if(program.getCustom().getCustomQuizId() > 0) {
            cpComp.setCustomQuiz(program.getCustom().getCustomQuizId(), program.getCustom().getCustomQuizName());
        }
        else if(program.getCustom().getCustomProgramId() > 0) {
            cpComp.setCustomProgram(program.getCustom().getCustomProgramId(), program.getCustom().getCustomProgramName());
        }
        else {
            /** make sure no custom assigned */
            sm.getProgram().setCustom(new CustomProgramComposite());
        }
        sm.setProgramChanged(true);
        sm.setChapter(chapter);
        sm.setPassPercent(passPercent);
        sm.setSectionNum(sectionNum);

        setTestConfig(conn, sm);
        updateStudent(conn, sm, true, false, true, false, false, resetMainProgram, continueParallelProgram);
        
        // need to set user Prog Id for CM_PROGRAM_ASSIGN
        program.setProgramId(sm.getProgram().getProgramId());

        int percent = getPercentFromString(passPercent);
        if(settings != null) {
            sm.setSettings(settings);
            updateStudentSettings(conn, sm, percent);
        }
        HaUserExtendedDao.resetUserExtendedLessonStatusForUid(conn, program, uid);
    }

    /** Assign program to student template.
    *
    * @param conn
    * @param uid
    * @param program
    * @param chapter
    * @param passPercent
    * @param settings
    * @param sectionNum
    * @throws Exception
    */
   public void assignProgramToStudentTemplate(final Connection conn, Integer uid, StudentProgramModel program, String chapter, String passPercent,
   		StudentSettingsModel settings, Integer sectionNum) throws Exception {

       StudentModelI sm = getStudentModelBaseForTemplate(conn, uid);

       sm.getProgram().setProgramType(program.getProgramType());
       sm.getProgram().setSubjectId(program.getSubjectId());
       CustomProgramComposite cpComp = sm.getProgram().getCustom();
       if(program.getCustom().getCustomQuizId() > 0) {
           cpComp.setCustomQuiz(program.getCustom().getCustomQuizId(), program.getCustom().getCustomQuizName());
       }
       else if(program.getCustom().getCustomProgramId() > 0) {
           cpComp.setCustomProgram(program.getCustom().getCustomProgramId(), program.getCustom().getCustomProgramName());
       }
       else {
           /** make sure no custom assigned */
           sm.getProgram().setCustom(new CustomProgramComposite());
       }
       sm.setProgramChanged(true);
       sm.setChapter(chapter);
       sm.setPassPercent(passPercent);
       sm.setSectionNum(sectionNum);

       setTestConfig(conn, sm);

       int percent = getPercentFromString(passPercent);
       
       if (passPercent != null) {
           CmUserProgramDao.getInstance().setProgramPassPercent(conn, sm.getProgram().getProgramId(), percent);
       }

   }

    public int getPercentFromString(String passPercent) {
        try {
            if(passPercent != null) {
                if(passPercent.endsWith("%")) {
                    passPercent = passPercent.substring(0, passPercent.length()-1);
                }
                return Integer.parseInt(passPercent);
            }
        }
        catch(Exception e) {
                __logger.error(String.format("*** Error getting percent from passPercent: %s", passPercent), e);
        }
        return 0;
    }

    /** Return total count of INMH items by this user
     *
     * @param conn
     * @param uid
     * @return
     * @throws Exception
     */
    public Integer getTotalInmHViewCount(final Connection conn,int uid) throws Exception {
        PreparedStatement pstat = null;
        try {
            String sql = "select count(*) from v_HA_USER_INMH_VIEWS_TOTAL " +
                          " where item_type in ('practice','cmextra') " +
                          " and uid = ?";

            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, uid);
            ResultSet rs = pstat.executeQuery();
            if (!rs.first())
                throw new Exception("Could not get count of viewed items");
            return rs.getInt(1);

        } catch (Exception e) {
                __logger.error(String.format("*** Error getting INMH view count for Uid: %d", uid), e);
            throw new CmRpcException("Error adding test run item view: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    public void setBackgroundStyle(final Connection conn, int uid, String style) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "update HA_USER set gui_background_style = ? where uid = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, style);
            ps.setInt(2, uid);
            int c = ps.executeUpdate();
            if(c != 1)
                __logger.warn("Could not update background style for user: " + uid);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }


    /**
     * Verify that this is the currently active
     * program for this user. This is to make sure
     * we do not update any state information after it
     * has been set while this program has been loaded.
     *
     *
     * Given a testId, make sure that its CM_PROGRAM
     * is the currently active CM_PROGRAM for this user.
     *
     * If this testId is not from the currently active
     * program for the test's user, then an exception is
     * thrown.
     *
     *
     * If not throw exception.
     * @param testId
     */
    public void verifyActiveProgram(int testId) throws Exception {

        if(testId == 0)
            return ;

        Integer count = getJdbcTemplate().queryForObject(
                CmMultiLinePropertyReader.getInstance().getProperty("VERIFY_IS_ACTIVE_PROGRAM"),
                new Object[]{testId},
                new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt(1);
                    }
                });

        if(count != 1) {
            throw new UserProgramIsNotActiveException();
        }
    }

    /**
     * Verify that this is the currently active
     * program for the specified student (uid).
     *
     * Given a testId and uid, make sure that its CM_PROGRAM
     * is the currently active CM_PROGRAM for the student.
     *
     * If this testId is not from the currently active
     * program for the test's user, return false; otherwise
     * return true.
     *
     * @param testId
     * @param uid
     */
    public void verifyActiveProgram(final Connection conn, int testId, int uid) throws Exception {

        if(testId == 0)
            return;

        if (uid == 0) {
                verifyActiveProgram(testId);
                return;
        }

        PreparedStatement ps=null;
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("VERIFY_IS_ACTIVE_PROGRAM_FOR_STUDENT");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            ps.setInt(2, testId);
            rs = ps.executeQuery();
            if(rs.next()) {
                /** value will equal 1 if program is active
                 *  otherwise, testId is not
                 *  created from the active program.
                 */
                if(rs.getInt(1) != 1) {
                        throw new UserProgramIsNotActiveException();
                }
            }
        }
        catch (Exception e) {
                __logger.warn(String.format(">>> verifyActiveProgram(): program is not active uid: %d, testId: %d", uid, testId),e);
                throw e;
        }
        finally {
            SqlUtilities.releaseResources(rs,ps,null);
        }
    }
}
