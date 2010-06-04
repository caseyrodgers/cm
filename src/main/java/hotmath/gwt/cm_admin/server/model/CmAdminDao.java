package hotmath.gwt.cm_admin.server.model;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import static hotmath.cm.util.CmCacheManager.CacheName.SUBJECT_CHAPTERS;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelBasic;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.ProgramSegmentData;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.SaveAutoRegistrationAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.UserInfo.AccountType;
import hotmath.gwt.shared.server.service.command.SaveAutoRegistrationCommand;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaAdmin;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * <code>CmAdminDao</code> provides data access methods for CM Admin
 * 
 * @author bob
 * 
 */

public class CmAdminDao {

    private static final Logger logger = Logger.getLogger(CmAdminDao.class);

    public CmAdminDao() {
    }

    // TODO add Subject selection by school type (non-college, college)

    public List<SubjectModel> getSubjectDefinitions(String progId) throws Exception {
        List<SubjectModel> l = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();

            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("SELECT_SUBJECTS_SQL"));
            ps.setString(1, progId);
            // TODO: separate queries for schools and colleges
            ps.setInt(2, 1);

            rs = ps.executeQuery();

            l = loadSubjectDefinitions(rs);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting subject definitions for school type: %s", "any"), e);
            throw new Exception("*** Error getting subject definitions ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
        return l;
    }

    public CmList<GroupInfoModel> getActiveGroups(final Connection conn, Integer adminUid) throws Exception {
        CmList<GroupInfoModel> l = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("SELECT_GROUPS_SQL"));
            ps.setInt(1, adminUid);
            ps.setInt(2, 1);
            ps.setInt(3, 1);
            rs = ps.executeQuery();

            l = loadGroups(rs);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting groups for admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting Group data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }

    private static final String ADD_GROUP_SQL = "insert into CM_GROUP (name, description, is_active, admin_id) "
            + "values( ?, ?, ?, ?)";

    public GroupInfoModel addGroup(final Connection conn, Integer adminUid, GroupInfoModel gm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        checkForReservedGroup(gm.getName());

        Boolean isDuplicate = checkForDuplicateGroup(conn, adminUid, gm);
        if (isDuplicate) {
            throw new Exception("The group you entered already exists, please try again.");
        }

        try {
            ps = conn.prepareStatement(ADD_GROUP_SQL);
            ps.setString(1, gm.getName());
            ps.setString(2, null);
            ps.setInt(3, 1);
            ps.setInt(4, adminUid);

            int count = ps.executeUpdate();
            if (count == 1) {
                int grpId = this.getLastInsertId(conn);
                gm.setId(grpId);
            }
        } catch (Exception e) {
            logger.error(String.format("*** Error adding Group: %s, for adminUid: %d", gm.getName(), adminUid), e);
            throw new Exception(String.format("*** Error adding Group: %s ***", gm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return gm;
    }

    /**
     * Delete named group from CM_GROUP
     * 
     * First clear any assigned students to null group, then remove from table.
     * 
     * NOTE: will not delete default groups also, the passed in adminId is the
     * adminId of the user, not the group .. which could be different.
     * 
     * @param conn
     * @param groupId
     * @throws Exception
     */
    public void deleteGroup(final Connection conn, Integer adminId, Integer groupId) throws Exception {
        Statement ps = null;
        try {
            ps = conn.createStatement();

            PreparedStatement pstat = null;
            try {
                String sql = "delete from HA_USER where group_id = ? and is_auto_create_template = 1";
                pstat = conn.prepareStatement(sql);
                pstat.setInt(1, groupId);
                if (pstat.executeUpdate() > 0) {
                    logger.info("Remove self-registration group: " + groupId);
                }
            } finally {
                SqlUtilities.releaseResources(null, pstat, null);
            }

            /**
             * set to group 'none' the existing students assigned to this group
             * 
             */
            String sql = "update HA_USER set group_id = 1 where group_id = " + groupId;
            ps.executeUpdate(sql);

            /**
             * Do not remove default groups
             * 
             */
            sql = "delete from CM_GROUP where admin_id != 0 and id = " + groupId;
            int cnt = ps.executeUpdate(sql);
            if (cnt != 1)
                logger.warn("No group found to delete: " + groupId);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    public void updateGroup(final Connection conn, Integer groupId, String name) throws Exception {

        checkForReservedGroup(name);

        PreparedStatement ps = null;
        try {
            String sql = "update CM_GROUP set name = ? where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, groupId);

            int cnt = ps.executeUpdate();
            if (cnt != 1)
                logger.warn("could not update group: " + groupId + " to " + name);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * Do not allow groups to be named as already existing default names
     * 
     * @TODO: should be all default groups..?
     * 
     */
    private void checkForReservedGroup(String name) throws Exception {
        if (name.equals("none") || name.equals("All Students")) {
            throw new Exception("The group name '" + name + "' is reserved.");
        }
    }

    // TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_GROUP_SQL = "select 1 from CM_GROUP where name = ? and admin_id in (?, 0)";

    public Boolean checkForDuplicateGroup(final Connection conn, Integer adminUid, GroupInfoModel gm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CHECK_DUPLICATE_GROUP_SQL);
            ps.setString(1, gm.getName());
            ps.setInt(2, adminUid);

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            logger.error(String.format("*** Error checking for group: %s, adminUid: %d", gm.getName(), adminUid), e);
            throw new Exception(String.format("*** Error checking for group: %s ***", gm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    @SuppressWarnings("unchecked")
    public CmList<ChapterModel> getChaptersForProgramSubject(final Connection conn, String progId, String subjId)
            throws Exception {

        String key = new StringBuilder(progId).append(".").append(subjId).toString();
        CmList<ChapterModel> l = (CmList<ChapterModel>) CmCacheManager.getInstance().retrieveFromCache(
                SUBJECT_CHAPTERS, key);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("+++ getChaptersForProgramSubject(): key: %s, retrieved: %s", key,
                    ((l == null) ? "NULL" : l.size())));
        }

        if (l != null)
            return l;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            String sql = CmMultiLinePropertyReader.getInstance().getProperty("SUBJECT_CHAPTERS_SQL");
            ps = conn.prepareStatement(sql);
            ps.setString(1, progId);
            ps.setString(2, subjId);
            rs = ps.executeQuery();
            l = loadChapters(rs);

            CmCacheManager.getInstance().addToCache(SUBJECT_CHAPTERS, key, l);

            return l;
        } catch (Exception e) {
            logger.error(String.format("*** Error getting chapters for progId: %s, subjId: %s", progId, subjId), e);
            throw new Exception("*** Error getting Chapter list ***", e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    public AccountInfoModel getAccountInfo(Integer adminUid) throws Exception {
        Connection conn = null;

        try {
            conn = HMConnectionPool.getConnection();
            return getAccountInfo(conn, adminUid);
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

    public AccountInfoModel getAccountInfo(final Connection conn, Integer adminUid) throws Exception {
        AccountInfoModel ai = new AccountInfoModel();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_INFO_SQL"));
            ps.setInt(1, adminUid);
            ps.setInt(2, adminUid);
            ps.setInt(3, adminUid);
            rs = ps.executeQuery();
            if (rs.next()) {
                ai.setSubscriberId(rs.getString("id"));
                ai.setSchoolName(rs.getString("school_name"));
                ai.setSchoolUserName(rs.getString("responsible_name"));
                ai.setAdminUserName(rs.getString("user_name"));
                ai.setMaxStudents(rs.getInt("max_students"));
                ai.setTotalStudents(rs.getInt("student_count"));
                java.sql.Date dt = rs.getDate("catchup_expire_date");
                String cmDate = (dt != null) ? dt.toString() : "2009-12-31";
                /** @TODO: remove hard-coded value */
                ai.setExpirationDate(cmDate);
                dt = rs.getDate("tutoring_expire_date");
                if (dt != null && dt.after(new java.sql.Date(System.currentTimeMillis()))) {
                    ai.setHasTutoring("Enabled");
                } else {
                    ai.setHasTutoring("Not Enabled");
                }
                // java.sql.Time time = rs.getTime("login_time");
                // DateFormat df = DateFormat.getDateTimeInstance();
                ai.setLastLogin(rs.getString("login_date_time"));
            } else {
                throw new Exception("*** No Account data found ***");
            }
        } catch (Exception e) {
            logger.error(String.format("*** Error getting account info for admin id: %d", adminUid), e);
            throw new Exception("*** Error getting Account data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return ai;
    }

    /**
     * Remove this user from the admin's scope
     * 
     * @TODO: move to archive?
     * 
     * @param sm
     */
    @Deprecated
    public void removeUser(StudentModel sm) {
        final String REMOVE_USER_SQL = "delete from HA_USER where uid = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            logger.info("Removing user: " + sm.getUid());
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(REMOVE_USER_SQL);
            ps.setInt(1, sm.getUid());
            if (ps.executeUpdate() == 0) {
                logger.warn("User was not removed");
            }
        } catch (Exception e) {
            logger.error(String.format("*** Error removing User with UID: %d", sm.getUid()), e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }

    private static final String SELECT_LAST_INSERT_ID_SQL = "select LAST_INSERT_ID()";

    private int getLastInsertId(final Connection conn) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            // Note: this is MySQL specific
            rs = stmt.executeQuery(SELECT_LAST_INSERT_ID_SQL);
            if (rs.next()) {
                int val = rs.getInt(1);
                return val;
            } else {
                throw new Exception("Unable to obtain last auto-increment id");
            }
        } finally {
            SqlUtilities.releaseResources(rs, stmt, null);
        }
    }

    public List<StudyProgramModel> getProgramDefinitions(final Connection conn, int uid) throws Exception {

        List<StudyProgramModel> rval = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty(
                    "ADMIN_PROGRAM_DEFINITIONS"));
            pstmt.setInt(1, uid);
            rs = pstmt.executeQuery();
            rval = loadProgramDefinitions(rs);
            return rval;
        } finally {
            SqlUtilities.releaseResources(rs, pstmt, null);
        }
    }

    /**
     * Return the Admin object currently attached to this SUBSCRIBER record or
     * null
     * 
     * @param subscriberId
     *            The subscriber record (id) to lookup
     * @return HaAdmin record, or null if no such record exists
     * 
     * @throws Exception
     *             on db errors
     */
    public HaAdmin getAdmin(String subscriberId) throws Exception {

        String GET_ADMIN_SQL = "select * from HA_ADMIN where subscriber_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(GET_ADMIN_SQL);
            ps.setString(1, subscriberId);

            rs = ps.executeQuery();
            if (!rs.first())
                return null;

            HaAdmin haAdmin = new HaAdmin();
            haAdmin.setAdminId(rs.getInt("aid"));
            haAdmin.setUserName(rs.getString("user_name"));
            haAdmin.setPassword(rs.getString("passcode"));

            return haAdmin;
        } catch (Exception e) {
            logger.error(String.format("*** Error obtaining admins for subscriberId: %s", subscriberId), e);
            throw new Exception(String.format("*** Error obtaining admins for subscriberId: %s", subscriberId));
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }

    /**
     * Create a new HA_ADMIN record
     * 
     * @param subscriberId
     *            The subscriber to associated with this cm admin
     * @param userName
     *            The username for the admin account
     * @param passcode
     *            The passcode for this admin account
     * 
     * @return The admin_id of the just created HA_ADMIN record
     * 
     * @throws Exception
     */
    public HaAdmin addAdmin(String subscriberId, String userName, String passcode) throws Exception {

        String ADD_ADMIN_SQL = "insert into HA_ADMIN(subscriber_id, user_name, passcode, create_date)"
                + "values(?,?,?,now())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(ADD_ADMIN_SQL);
            ps.setString(1, subscriberId);
            ps.setString(2, userName);
            ps.setString(3, passcode);
            int count = ps.executeUpdate();
            int adminId = 0;
            if (count == 1) {
                adminId = this.getLastInsertId(conn);
            } else {
                throw new Exception("Could not create HA_ADMIN account for unknown reason");
            }

            HaAdmin haAdmin = new HaAdmin();
            haAdmin.setAdminId(adminId);
            haAdmin.setUserName(userName);
            haAdmin.setPassword(passcode);

            return haAdmin;
        } catch (Exception e) {
            logger.error(String.format("*** Error creating admin, name: %s, subscriberId: %s, passcode: %s", userName,
                    subscriberId, passcode), e);
            throw new Exception(String.format("*** Error creating HA_ADMIN, name: %s, subscriberId: %s, passcode: %s",
                    userName, subscriberId, passcode));
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }

    public String getPrintableStudentReportId(List<Integer> studentUids) {
        String reportId = String.format("%d%d%d", studentUids.get(0), System.currentTimeMillis(), studentUids.size());
        CmCacheManager.getInstance().addToCache(REPORT_ID, reportId, studentUids);
        return reportId;
    }

    private List<StudyProgramModel> loadProgramDefinitions(ResultSet rs) throws Exception {
        List<StudyProgramModel> l = new ArrayList<StudyProgramModel>();
        while (rs.next()) {
            StudyProgramModel m = new StudyProgramModel();
            m.setCustomProgramId(rs.getInt("custom_program_id"));
            m.setCustomProgramName(rs.getString("custom_program_name"));
            m.setShortTitle(rs.getString("id"));
            m.setTitle(rs.getString("title"));
            m.setDescr(rs.getString("description"));
            m.setNeedsChapters(rs.getInt("needs_chapter"));
            m.setNeedsSubject(rs.getInt("needs_subject"));
            m.setNeedsPassPercent(rs.getInt("needs_pass_percent"));
            m.setNeedsState(rs.getInt("needs_state"));
            m.setIsTemplate(rs.getInt("is_template") != 0);
            l.add(m);
        }
        return l;
    }

    private CmList<GroupInfoModel> loadGroups(ResultSet rs) throws Exception {
        CmList<GroupInfoModel> l = new CmArrayList<GroupInfoModel>();

        while (rs.next()) {
            GroupInfoModel m = new GroupInfoModel();
            m.setId(rs.getInt("id"));
            m.setGroupName(rs.getString("name"));
            m.setIsActive(rs.getInt("is_active") != 0);
            m.setIsSelfReg(rs.getInt("is_auto_create_template") != 0);
            l.add(m);
        }
        return l;
    }

    private CmList<ChapterModel> loadChapters(ResultSet rs) throws Exception {
        CmList<ChapterModel> l = new CmArrayList<ChapterModel>();

        while (rs.next()) {
            ChapterModel m = new ChapterModel(String.valueOf(rs.getInt("title_number")), rs.getString("title"));
            l.add(m);
        }
        return l;
    }

    private List<SubjectModel> loadSubjectDefinitions(ResultSet rs) throws Exception {

        List<SubjectModel> l = new ArrayList<SubjectModel>();

        while (rs.next()) {
            SubjectModel sm = new SubjectModel(rs.getString("title"), rs.getString("id"));
            l.add(sm);
        }
        return l;
    }

    /**
     * Remove any Auto Registration Setup based on this group name
     * 
     * NOTE: this only deletes users marked as is_auto_create_template > 0, not
     * normal user .. each is_auto_create_template account will have a unique
     * password.
     * 
     * @param conn
     * @param adminId
     * @param groupName
     * @throws Exception
     */
    public void removeAutoRegistrationSetupFor(final Connection conn, Integer adminId, String groupName)
            throws Exception {

        String sql = "delete u " + " from   HA_USER u JOIN CM_GROUP g ON u.group_id = g.id " + " where u.admin_id = ? "
                + " and    g.name = ?" + " and is_auto_create_template = 1 ";

        PreparedStatement pstat = null;
        try {
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, adminId);
            pstat.setString(2, groupName);
            int c = pstat.executeUpdate();

            logger.debug("Removed auto setup: " + groupName + ", " + c);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    /**
     * Mark this account as a Auto Registration Setup account.
     * 
     * THis will make this account participate in the auto creation routines
     * during login.
     * 
     * @see CmAutoLoginManager
     * 
     * @param conn
     * @param uid
     * @throws Exception
     */
    public void markAccountAsAutoRegistrationSetup(final Connection conn, Integer uid) throws Exception {

        String sql = "update HA_USER set is_auto_create_template = 1 where uid = " + uid;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            int cnt = stmt.executeUpdate(sql);
            if (cnt == 0) {
                throw new CmException("Could not setup auto creation account: " + uid);
            }
        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    /**
     * Return the account type for this admin
     * 
     * @throws Exception
     */
    public AccountType getAccountType(final Connection conn, Integer adminId) throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_TYPE_LOOKUP");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);

            ResultSet rs = ps.executeQuery();
            if (!rs.first())
                throw new CmException("Admin record does not have an associated SUBSCRIBER record");

            String type = rs.getString("type");
            if (type.equals(AccountType.SCHOOL_TEACHER.getTag()))
                return AccountType.SCHOOL_TEACHER;
            else if (type.equals(AccountType.PARENT_STUDENT.getTag()))
                return AccountType.PARENT_STUDENT;
            else
                return AccountType.OTHER;

        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    public void createSelfRegistrationGroup(final Connection conn, Integer aid, String groupName, CmProgram program,
            Boolean tutoringEnabled, Boolean showWorkRequired) throws Exception {
        try {
            StudentModelI sm = new StudentModelBasic();
            sm.setName(groupName);
            sm.setGroup(groupName);
            sm.setAdminUid(aid);
            sm.setGroupId("1");

            StudentProgramModel studentProgram = new StudentProgramModel();
            studentProgram.setProgramType(program.getProgramType());
            studentProgram.setSubjectId(program.getSubject());
            sm.setProgram(studentProgram);

            sm.setPassPercent("70%");

            sm.getSettings().setTutoringAvailable(tutoringEnabled);
            sm.getSettings().setShowWorkRequired(showWorkRequired);
            new SaveAutoRegistrationCommand().execute(conn, new SaveAutoRegistrationAction(aid, sm));
        } catch (Exception e) {
            throw new CmException("The self-registration group could not be created", e);
        }
    }

    /**
     * return a Map that can be used to replace UID_LIST in properties
     * 
     * @param students
     * @return
     */
    private List<Integer> createInListReplacements(List<StudentModelExt> students) {
        List<Integer> studentIds = new ArrayList<Integer>();
        for (int i = 0, t = students.size(); i < t; i++) {
            studentIds.add(students.get(i).getUid());
        }
        return studentIds;
    }

    /**
     * Return list of TrendingData objects that represent distinct program
     * assigned to group.
     * 
     * @param conn
     * @param aid
     * @param studentPool
     * @param useActiveOnly
     *            If true, then limit programs to active only.
     * @return
     * @throws Exception
     */
    public CmList<TrendingData> getTrendingData(final Connection conn, Integer aid, List<StudentModelExt> studentPool,
            boolean useActiveOnly) throws Exception {
        CmList<TrendingData> tdata = new CmArrayList<TrendingData>();

        PreparedStatement ps = null;
        logger.debug("aid=" + aid + " getting trending data");
        try {
            String sqlToken = (useActiveOnly ? "TRENDING_DATA_SQL_FROM_UIDS_ACTIVE_ONLY"
                    : "TRENDING_DATA_SQL_FROM_UIDS_FULL_HISTORY");
            String sqlTemplate = CmMultiLinePropertyReader.getInstance().getProperty(sqlToken);

            List<Integer> uidList = new ArrayList<Integer>();
            for (StudentModelExt sme : studentPool) {
                if (useActiveOnly) { /* TODO StudentModelExt isActive */
                }
                uidList.add(sme.getUid());
            }
            String sql = QueryHelper.createInListSQL(sqlTemplate, uidList, "u.uid");

            ps = conn.prepareStatement(sql);
            ps.setInt(1, aid);
            logger.debug("+++ getTrendingData(): SQL: " + ps.toString());

            SqlOutWriter.write(ps.toString());
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tdata.add(new TrendingData(rs.getString("lesson_name"), rs.getInt("count_assigned")));
            }
            return tdata;
        } finally {
            logger.debug("aid=" + aid + " trending data retrieved");
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * return assessment/trending data for the given admin_id limiting data to
     * uids found in studentPool
     * 
     * @param conn
     * @param aid
     * @param studentPool
     * @return
     * @throws Exception
     */
    public CmList<ProgramData> getTrendingData_ForProgram(final Connection conn, Integer aid,
            List<StudentModelExt> studentPool, boolean useActiveOnly) throws Exception {

        logger.debug("aid=" + aid + " getting trending data for program");
        CmList<ProgramData> tdata = new CmArrayList<ProgramData>();
        if (true) {
            logger.info("SKIPPING GETTRENDINGDATA_FORPROGRAM");
            return tdata;
        }
        PreparedStatement ps = null;
        try {
            List<Integer> studentUids = createInListReplacements(studentPool);
            String sqlToken = (useActiveOnly ? "TRENDING_DATA_FOR_PROGRAMS_SQL_FROM_UIDS_ACTIVE_ONLY"
                    : "TRENDING_DATA_FOR_PROGRAMS_SQL_FROM_UIDS_FULL_HISTORY");
            String sql = CmMultiLinePropertyReader.getInstance().getProperty(sqlToken);
            sql = QueryHelper.createInListSQL(sql, studentUids, "u.uid");
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProgramData pd = createProgramData(conn, studentUids, rs.getInt("test_def_id"), useActiveOnly);
                tdata.add(pd);
            }
            return tdata;
        } catch (Exception e) {
            throw e;
        } finally {
            logger.debug("aid=" + aid + " trending data for program retrieved: " + tdata.size());
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * return students who have been assigned to the given program/quiz at some
     * time.
     * 
     * @param conn
     * @param aid
     * @param studentPool
     * @return
     * @throws Exception
     */
    public CmList<StudentModelExt> getStudentsWhoHaveBeenAssignedProgramSegment(final Connection conn,
            List<StudentModelExt> studentPool, Integer testDefId, Integer quizSegment, Boolean useActiveOnly)
            throws Exception {
        CmList<StudentModelExt> students = new CmArrayList<StudentModelExt>();
        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty(
                    "TRENDING_DATA_DETAIL_FOR_PROGRAM_SEGMENT_FROM_UIDS");
            sql = QueryHelper.createInListSQL(sql, createInListReplacements(studentPool), "u.uid");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, testDefId);
            ps.setInt(2, quizSegment);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentModelExt parialStudent = new StudentModelExt();
                parialStudent.setName(rs.getString("user_name"));
                parialStudent.setUid(rs.getInt("uid"));
                students.add(parialStudent);
            }
            return students;
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * Return list of students who have been assigned the name lesson
     * 
     * @param conn
     * @param studentPool
     * @param lessonName
     * @return
     * @throws Exception
     */
    public CmList<StudentModelExt> getStudentsWhoHaveBeenAssignedLesson(final Connection conn,
            List<StudentModelExt> studentPool, String lessonName, boolean useActiveOnly) throws Exception {
        CmList<StudentModelExt> students = new CmArrayList<StudentModelExt>();
        PreparedStatement ps = null;
        try {

            String sql = CmMultiLinePropertyReader.getInstance().getProperty(
                    "TRENDING_DATA_DETAIL_FOR_LESSON_FROM_UIDS");
            sql = QueryHelper.createInListSQL(sql, createInListReplacements(studentPool), "u.uid");
            ps = conn.prepareStatement(sql);
            ps.setString(1, lessonName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentModelExt parialStudent = new StudentModelExt();
                String name = rs.getString("user_name");
                int timesAssigned = rs.getInt("times_assigned");
                if (timesAssigned > 1)
                    name = name + " (" + timesAssigned + " times)";
                parialStudent.setName(name);
                parialStudent.setUid(rs.getInt("uid"));
                students.add(parialStudent);
            }
            return students;
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * Return array of counts indicating the count of users in each segment.
     * 
     * The array is sequential from 1, and only contains values for seqments
     * that have data. So, if checking test with no users the array would be
     * zero lenght.
     * 
     * 
     * @param conn
     * @param replacements
     * @param testDef
     * @param useActiveOnly
     * @return
     * @throws Exception
     */
    private int[] getCountsOfUsersWhoHaveVisitedQuizSegment(final Connection conn, List<Integer> studentIds,
            HaTestDef testDef, boolean useActiveOnly) throws Exception {
        PreparedStatement ps = null;
        try {

            int counts[] = new int[testDef.getTestConfig().getSegmentCount()];

            String sqlToken = (useActiveOnly ? "TRENDING_DATA_FOR_TEST_SEGMENTS_SQL_FROM_UIDS_ACTIVE_ONLY"
                    : "TRENDING_DATA_FOR_TEST_SEGMENTS_SQL_FROM_UIDS_FULL_HISTORY");
            String sql = CmMultiLinePropertyReader.getInstance().getProperty(sqlToken);
            sql = QueryHelper.createInListSQL(sql, studentIds, "u.uid");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, testDef.getTestDefId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int segNum = rs.getInt("test_segment");
                try {
                    counts[segNum - 1] = rs.getInt("count_users");
                } catch (Exception e) {
                    /** could be change segment count */
                    // silent
                    // logger.debug(String.format("*** Error getting quiz segment user count for testDefId: %d, UIDs: %s",
                    // testDef.getTestDefId(), replacements), e);
                }
            }
            return counts;
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    private ProgramData createProgramData(final Connection conn, List<Integer> studentIds, int testDefId,
            boolean useActiveOnly) throws Exception {
        PreparedStatement ps = null;
        try {
            HaTestDef testDef = new HaTestDefDao().getTestDef(conn, testDefId);

            String name = testDef.getName();
            ProgramData pd = new ProgramData(name, testDef.getTestDefId());

            /**
             * for number of segments defined for this test
             * 
             */
            int segmentCount = testDef.getTestConfig().getSegmentCount();
            int segUserCount[] = getCountsOfUsersWhoHaveVisitedQuizSegment(conn, studentIds, testDef, useActiveOnly);
            for (int i = 0; i < segmentCount; i++) {
                ProgramSegmentData psd = new ProgramSegmentData(i, segUserCount[i]);
                pd.getSegments().add(psd);
            }
            return pd;
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

}



class SqlOutWriter {
    synchronized static public void write(String sql) {
        FileWriter fout=null;
        try {
            fout = new FileWriter("/tmp/cm_sql.txt",true);
            fout.write(sql + ";");
            fout.write("\n\n");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fout.close();
            }
            catch(Exception ee) {
                ee.printStackTrace();
            }
        }
}
}