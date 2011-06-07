package hotmath.gwt.cm_admin.server.model;

import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import static hotmath.cm.util.CmCacheManager.CacheName.SUBJECT_CHAPTERS;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
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
import hotmath.gwt.shared.client.util.CmUserException;
import hotmath.gwt.shared.server.service.command.SaveAutoRegistrationCommand;
import hotmath.spring.SpringManager;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * <code>CmAdminDao</code> provides data access methods for CM Admin
 * 
 * @author bob
 * 
 */

public class CmAdminDao extends SimpleJdbcDaoSupport {

    private static final Logger logger = Logger.getLogger(CmAdminDao.class);

    private static CmAdminDao __instance;
    public static CmAdminDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (CmAdminDao)SpringManager.getInstance().getBeanFactory().getBean("cmAdminDao");
        }
        return __instance;
    }
    
    private CmAdminDao() { /** empty */}

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
    
    public void setAdminPassword(final Connection conn, int adminId, String email) throws Exception {
    	PreparedStatement ps=null;
    	try {
    		String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADMIN_EMAIL_UPDATE");
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, email);
    		ps.setInt(2, adminId);
    		int cnt = ps.executeUpdate();
    		if(cnt != 1) {
    			throw new CmException("Could not update HA_ADMIN email in SUBSCRIBERS");
    		}
    	}
    	finally {
    		SqlUtilities.releaseResources(null,ps,null);
    	}
    }

    public GroupInfoModel getGroup(final Connection conn, int adminId, String groupName) throws Exception {
    	List<GroupInfoModel> groups = getActiveGroups(adminId);
    	for(GroupInfoModel g: groups) {
    		if(g.getName().equals(groupName))
    			return g;
    	}
    	return null;
    }

    public List<GroupInfoModel> getActiveGroups(Integer adminUid) throws Exception {
        return getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("SELECT_GROUPS_SQL"),
                new Object[]{adminUid,adminUid,1,1},
                new RowMapper<GroupInfoModel>() {
                    @Override
                    public GroupInfoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        GroupInfoModel m = new GroupInfoModel();
                        m.setId(rs.getInt("id"));
                        m.setGroupName(rs.getString("name"));
                        m.setIsActive(rs.getInt("is_active") != 0);
                        m.setIsSelfReg(rs.getInt("is_auto_create_template") != 0);
                        return m;
                    }
                }
        );
    }
    
    
    /** Return basic student info (name, uid) for each student
     *  currently assigned to group.
     *  
     *  
     * @return
     * @throws Exception
     */
    public CmList<StudentModelExt> getGroupStudents(final Connection conn, GroupInfoModel gim) throws Exception {
    	
    	CmList<StudentModelExt> students = new CmArrayList<StudentModelExt>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_IN_GROUP");
    		ps = conn.prepareStatement(sql);
    		ps.setInt(1, gim.getAdminId());
    		ps.setString(2, gim.getName());
    		
    		rs = ps.executeQuery();
    		while(rs.next()) {
    			StudentModelExt sm = new StudentModelExt();
    			sm.setUid(rs.getInt("uid"));
    			sm.setName(rs.getString("user_name"));
    			students.add(sm);
    		}
    		return students;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps,null);
    	}
    	
    }
    
    /** Return list of students not in current group
     * 
     * @param conn
     * @param gim
     * @return
     * @throws Exception
     */
    public CmList<StudentModelExt> getGroupStudentsNotIn(final Connection conn,GroupInfoModel gim) throws Exception {
    	CmList<StudentModelExt> students = new CmArrayList<StudentModelExt>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_NOT_IN_GROUP");
    		ps = conn.prepareStatement(sql);
    		ps.setInt(1, gim.getAdminId());
    		ps.setString(2, gim.getName());
    		
    		rs = ps.executeQuery();
    		while(rs.next()) {
    			StudentModelExt sm = new StudentModelExt();
    			sm.setUid(rs.getInt("uid"));
    			sm.setName(rs.getString("user_name"));
    			sm.setGroup(rs.getString("group_name"));
    			students.add(sm);
    		}
    		return students;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps,null);
    	}
    } 
    
    /** Set the students in named group to match exactly list of uids in students
     * 
     * NOTE: avoid changing any self-registration groups.
     * 
     * 
     * @param conn
     * @param gim
     * @param students
     * @throws Exception
     */
    public void setGroupStudents(final Connection conn, GroupInfoModel gim, List<StudentModelExt> students) throws Exception {
    	PreparedStatement ps=null;
    	try {
    		String inList="";
    		for(StudentModelExt se: students) {
    			if(inList.length() > 0)
    				inList += ",";
    			inList += se.getUid();
    		}
    		String sql1 = "update HA_USER set group_id = 1 where is_auto_create_template != 1 and admin_id = " + gim.getAdminId() + " and group_id = " + gim.getId();
    		conn.createStatement().executeUpdate(sql1);
    		
    		if(students.size() > 0) {
	    		String sql = "update HA_USER set group_id = " + gim.getId() + " where uid in (" + inList + ")";
	    		ps = conn.prepareStatement(sql);
	    		ps.executeUpdate();
    		}
    	}
    	finally {
    		SqlUtilities.releaseResources(null,ps,conn);
    	}
    }
    

    public GroupInfoModel addGroup(final Connection conn, Integer adminUid, GroupInfoModel gm) throws Exception {
        PreparedStatement ps = null;

        performGroupNameChecks(conn, adminUid, gm.getName(), "addGroup()");

        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("ADD_GROUP_SQL"));
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
            SqlUtilities.releaseResources(null, ps, null);
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

    public void updateGroup(final Connection conn, Integer adminUid, Integer groupId, String name) throws Exception {
    	
    	performGroupNameChecks(conn, adminUid, name, "updateGroup()");

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

    private void performGroupNameChecks(final Connection conn, Integer adminUid, String name, String methodName) throws Exception {

    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("+++ performGroupNameChecks(): adminUid: %d, name: %s, methodName(): %s",
    				adminUid, name, methodName));
    	}
        if (checkForReservedGroup(name)) {
            throw new CmUserException(String.format("The group name you entered, %s, is reserved, please try again", name));	
        }

        if (name == null) {
        	if (adminUid != null)
            	logger.warn(String.format("%s: adminId: %d; NULL group name passed", methodName, adminUid));
        	else
        		logger.warn("addGroup(): NULL admin ID and group name passed");
            throw new CmUserException("The group name you entered is NULL, please try again.");
        }

        Boolean isDuplicate = checkForDuplicateGroup(conn, adminUid, name);
        if (isDuplicate) {
            throw new CmUserException(String.format("The group name you entered, %s, already exists, please try again.", name));	
        }

    }
    /**
     * Do not allow groups to be named as already existing default names
     * 
     * @TODO: should be all default groups..?
     * 
     * @TODO: obtain from DB?
     * 
     */
    private boolean checkForReservedGroup(String name) {
        return (name != null && (name.equals("none") || name.equals("All Students")));
    }

    // TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_GROUP_SQL = "select 1 from CM_GROUP where name = ? and admin_id in (?, 0)";

    public Boolean checkForDuplicateGroup(final Connection conn, Integer adminUid, String name) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CHECK_DUPLICATE_GROUP_SQL);
            ps.setString(1, name);
            ps.setInt(2, adminUid);

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            logger.error(String.format("*** Error checking for group: %s, adminUid: %d", name, adminUid), e);
            throw new Exception(String.format("*** Error checking for group: %s ***", name));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws Exception {

        String key = new StringBuilder(progId).append(".").append(subjId).toString();
        List<ChapterModel> list = (List<ChapterModel>) CmCacheManager.getInstance().retrieveFromCache(SUBJECT_CHAPTERS, key);
        
        if (logger.isDebugEnabled())
        	logger.debug("key: + " + key + " size: " + ((list!=null) ? list.size() : 0));
        
        if (list != null)
            return list;
        
        list = getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("SUBJECT_CHAPTERS_SQL"),
                new Object[]{progId,subjId},
                new RowMapper<ChapterModel>() {
                    @Override
                    public ChapterModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new ChapterModel(String.valueOf(rs.getInt("title_number")), rs.getString("title"));
                    }
                });

         CmCacheManager.getInstance().addToCache(SUBJECT_CHAPTERS, key, list);
         
         return list;
    }
    
    public AccountInfoModel getAccountInfo(final Integer adminUid) throws Exception {
        AccountInfoModel accountInfo = this.getJdbcTemplate().queryForObject(
                CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_INFO_SQL"), 
                new Object[] { adminUid,adminUid,adminUid,adminUid },
                new RowMapper<AccountInfoModel>() {
                    public AccountInfoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            AccountInfoModel ai = new AccountInfoModel();
                            ai.setSubscriberId(rs.getString("id"));
                            ai.setSchoolName(rs.getString("school_name"));
                            ai.setSchoolUserName(rs.getString("responsible_name"));
                            ai.setAdminUserName(rs.getString("user_name"));
                            ai.setMaxStudents(rs.getInt("max_students"));
                            ai.setTotalStudents(rs.getInt("student_count"));
                            java.sql.Date dt = rs.getDate("catchup_expire_date");
                            ai.setAccountCreateDate(rs.getDate("account_create_date"));
                            
                            /** if account create is null (test account), use default
                             * 
                             */
                            if(ai.getAccountCreateDate() == null)
                                ai.setAccountCreateDate(new GregorianCalendar(2010,1,1).getTime());
                                
                            String cmDate = (dt != null) ? dt.toString() : "2011-12-31";
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
                            
                            return ai;

                        } catch (Exception e) {
                            logger.error("Error creating AccountInfoModel: " + adminUid, e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });

        return accountInfo;
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
            SqlUtilities.releaseResources(null, ps, conn);
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
            pstmt.setInt(2, uid);
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
        	String msg = String.format("*** Error creating admin, name: %s, subscriberId: %s, passcode: %s", userName,subscriberId, passcode);
            logger.error(msg, e);
            throw new Exception(String.format("*** Error creating HA_ADMIN, name: %s, subscriberId: %s, passcode: %s", userName, subscriberId, passcode));
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
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
            m.setCustomQuizId(rs.getInt("custom_quiz_id"));
            m.setCustomQuizName(rs.getString("custom_quiz_name"));
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

        PreparedStatement pstat = null;
        try {
        	
            pstat = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("REMOVE_AUTO_REG_SETUP_SQL"));

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
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_TYPE_LOOKUP");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);

            rs = ps.executeQuery();
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
            SqlUtilities.releaseResources(rs, ps, null);
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
        ResultSet rs = null;
        
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
            
            rs = ps.executeQuery();
            while (rs.next()) {
                tdata.add(new TrendingData(rs.getString("lesson_name"), rs.getInt("count_assigned")));
            }
            return tdata;
        } finally {
            logger.debug("aid=" + aid + " trending data retrieved");
            SqlUtilities.releaseResources(rs, ps, null);
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
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<Integer> studentUids = createInListReplacements(studentPool);
            String sqlToken = (useActiveOnly ? "TRENDING_DATA_FOR_PROGRAMS_SQL_FROM_UIDS_ACTIVE_ONLY"
                    : "TRENDING_DATA_FOR_PROGRAMS_SQL_FROM_UIDS_FULL_HISTORY");
            String sql = CmMultiLinePropertyReader.getInstance().getProperty(sqlToken);
            sql = QueryHelper.createInListSQL(sql, studentUids, "u.uid");
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                ProgramData pd = createProgramData(conn, studentUids, rs.getInt("test_def_id"), useActiveOnly);
                tdata.add(pd);
            }
            return tdata;
        } catch (Exception e) {
            throw e;
        } finally {
            logger.debug("aid=" + aid + " trending data for program retrieved: " + tdata.size());
            SqlUtilities.releaseResources(rs, ps, null);
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
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty(
                    "TRENDING_DATA_DETAIL_FOR_PROGRAM_SEGMENT_FROM_UIDS");
            sql = QueryHelper.createInListSQL(sql, createInListReplacements(studentPool), "u.uid");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, testDefId);
            ps.setInt(2, quizSegment);
            rs = ps.executeQuery();
            while (rs.next()) {
                StudentModelExt parialStudent = new StudentModelExt();
                parialStudent.setName(rs.getString("user_name"));
                parialStudent.setUid(rs.getInt("uid"));
                students.add(parialStudent);
            }
            return students;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
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
        ResultSet rs = null;
        try {

            String sql = CmMultiLinePropertyReader.getInstance().getProperty(
                    "TRENDING_DATA_DETAIL_FOR_LESSON_FROM_UIDS");
            sql = QueryHelper.createInListSQL(sql, createInListReplacements(studentPool), "u.uid");
            ps = conn.prepareStatement(sql);
            ps.setString(1, lessonName);
            rs = ps.executeQuery();
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
            SqlUtilities.releaseResources(rs, ps, null);
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
        ResultSet rs = null;
        try {

            int counts[] = new int[testDef.getTestConfig().getSegmentCount()];

            String sqlToken = (useActiveOnly ? "TRENDING_DATA_FOR_TEST_SEGMENTS_SQL_FROM_UIDS_ACTIVE_ONLY"
                    : "TRENDING_DATA_FOR_TEST_SEGMENTS_SQL_FROM_UIDS_FULL_HISTORY");
            String sql = CmMultiLinePropertyReader.getInstance().getProperty(sqlToken);
            sql = QueryHelper.createInListSQL(sql, studentIds, "u.uid");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, testDef.getTestDefId());
            rs = ps.executeQuery();
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
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    private ProgramData createProgramData(final Connection conn, List<Integer> studentIds, int testDefId,
            boolean useActiveOnly) throws Exception {
        PreparedStatement ps = null;
        try {
            HaTestDef testDef = HaTestDefDao.getInstance().getTestDef(testDefId);

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
