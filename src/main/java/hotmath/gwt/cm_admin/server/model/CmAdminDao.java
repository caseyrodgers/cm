package hotmath.gwt.cm_admin.server.model;

import static hotmath.cm.util.CmCacheManager.CacheName.PROG_DEF;
import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import static hotmath.cm.util.CmCacheManager.CacheName.SUBJECT_CHAPTERS;
import hotmath.cm.util.CmCacheManager;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.testset.ha.HaAdmin;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CmAdminDao {
	
	private static final Logger logger = Logger.getLogger(CmAdminDao.class);

    public CmAdminDao() {
    }

    //TODO add Subject selection by school type (non-college, college)
    
    private static String SELECT_SUBJECTS_SQL =
    	"select sd.* " +
        "from HA_SUBJ_DEF sd, HA_TEST_DEF td " +
    	"where sd.id = td.subj_id and td.is_active = 1 " + 
    	"  and td.prog_id = ? and sd.for_school = ?";
    
    public List <SubjectModel> getSubjectDefinitions(String progId) throws Exception {
    	List <SubjectModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		
    		ps = conn.prepareStatement(SELECT_SUBJECTS_SQL);
    		ps.setString(1, progId);
    		//TODO: separate queries for schools and colleges
    		ps.setInt(2, 1);

    		rs = ps.executeQuery();
    		
    		l = loadSubjectDefinitions(rs);
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting subject definitions for school type: %s", "any"), e);
    	    throw new Exception("*** Error getting subject definitions ***");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    private static final String GET_GROUPS_SQL =
    	"select id, name, description, is_active " +
        "from  CM_GROUP g INNER JOIN HA_ADMIN a ON g.admin_id = a.aid " +
        "where a.aid = ? " +
        " and g.is_active = ? " +
        "UNION " +
        "select g.id, g.name, g.description, g.is_active " +
        "from CM_GROUP g " +
        "where  g.admin_id = 0 " +
        " and g.is_active = ? " +
        "order by name asc";

    public List <GroupModel> getActiveGroups(Integer adminUid) throws Exception {
    	List <GroupModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(GET_GROUPS_SQL);
    		ps.setInt(1, adminUid);
    		ps.setInt(2, 1);
    		ps.setInt(3, 1);
    		rs = ps.executeQuery();
    		
    		l = loadGroups(rs);
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting groups for admin uid: %d", adminUid), e);
    		throw new Exception("*** Error getting Group data ***");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }

    private static final String ADD_GROUP_SQL =
    	"insert into CM_GROUP (name, description, is_active, admin_id) " +
    	"values( ?, ?, ?, ?)";

    public GroupModel addGroup(Integer adminUid, GroupModel gm) throws Exception {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
		Boolean isDuplicate = checkForDuplicateGroup(adminUid, gm);
		if (isDuplicate) {
			throw new Exception("The group you entered already exists, please try again.");
		}
		
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(ADD_GROUP_SQL);
    		ps.setString(1, gm.getName());
    		ps.setString(2, null);
    		ps.setInt(3, 1);
    		ps.setInt(4, adminUid);
    		
    		int count = ps.executeUpdate();
    		if (count == 1) {
        	    int grpId = this.getLastInsertId(conn);
        	    gm.setId(String.valueOf(grpId));
    		}
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(String.format("*** Error adding Group: %s, for adminUid: %d", gm.getName(), adminUid), e);
    	    throw new Exception(String.format("*** Error adding Group: %s ***", gm.getName()));
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return gm;
    }

    //TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_GROUP_SQL =
    	"select 1 from CM_GROUP where name = ? and admin_id in (?, 0)";
    
    public Boolean checkForDuplicateGroup(Integer adminUid, GroupModel gm) throws Exception {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(CHECK_DUPLICATE_GROUP_SQL);
    		ps.setString(1, gm.getName());
    		ps.setInt(2, adminUid);
    		
    		rs = ps.executeQuery();
    		return (rs.next());
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error checking for group: %s, adminUid: %d", gm.getName(), adminUid), e);
    		throw new Exception(String.format("*** Error checking for group: %s ***", gm.getName()));
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    }

    private static final String CHAPTERS_SQL =
    	"select bt.title_number, trim(bt.title) as title, td.textcode " +
        "from HA_TEST_DEF td, BOOK_TOC bt " +
        "where td.prog_id = ? and td.subj_id = ? " +
        "  and bt.textcode = td.textcode and bt.parent <> 0";
    
    /** Get Chapter titles for given progId/subID
     * 
     *  NOTE: This is duplicated in HaTestDefDao.getProgramChapters(testDef)
     *  
     *  
     * @param progId
     * @param subjId
     * @return
     * @throws Exception
     */
    public List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws Exception {
    	
    	Connection conn = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		return getChaptersForProgramSubject(conn, progId, subjId);
    	}
    	finally {
    		SqlUtilities.releaseResources(null, null, conn);
    	}
    }

    @SuppressWarnings("unchecked")
	public List<ChapterModel> getChaptersForProgramSubject(final Connection conn, String progId, String subjId) throws Exception {
    	
    	String key = new StringBuilder(progId).append(".").append(subjId).toString();
    	List <ChapterModel> l = (List<ChapterModel>)CmCacheManager.getInstance().retrieveFromCache(SUBJECT_CHAPTERS, key);
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("+++ getChaptersForProgramSubject(): key: %s, retrieved: %s", key, ((l == null)?"NULL":l.size())));
    	}
    	
    	if (l != null) return l;
    	
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		ps = conn.prepareStatement(CHAPTERS_SQL);
    		ps.setString(1, progId);
    		ps.setString(2, subjId);
    		rs = ps.executeQuery();
    		l = loadChapters(rs);

    		CmCacheManager.getInstance().addToCache(SUBJECT_CHAPTERS, key, l);

    		return l;
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting chapters for progId: %s, subjId: %s", progId, subjId), e);
    		throw new Exception("*** Error getting Chapter list ***", e);
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, null);
    	}
    }

    //TODO: obtain max_students from DB (SUBSCRIBERS_SERVICES ?)
    private static final String ACCOUNT_INFO_SQL =
    	"select s.id, ifnull(s.school_type, 'NONE') as school_name, s.responsible_name, sc.date_expire as catchup_expire_date, " +
    	"  sc.service_name, st.date_expire as tutoring_expire_date, h.user_name, t.student_count, 1000 as max_students, " +
    	" l.login_time, date_format(l.login_time, '%Y-%m-%d %h:%i %p') as login_date_time " +
        "from SUBSCRIBERS s " +
        " inner join HA_ADMIN h on h.subscriber_id = s.id " +
        " left join SUBSCRIBERS_SERVICES st on st.subscriber_id = h.subscriber_id and st.service_name = 'tutoring' " +
        " left join SUBSCRIBERS_SERVICES sc on sc.subscriber_id = h.subscriber_id and sc.service_name = 'catchup' " +
        " left join (select admin_id, is_active, count(*) as student_count from HA_USER where is_active = 1 group by admin_id) t " +
        "   on t.admin_id = h.aid " + 
        " left join (" +
        "   select user_id, max(login_time) as login_time from HA_USER_LOGIN u " +
        "   where u.login_time < (select max(login_time) as login_time from HA_USER_LOGIN " +
        "                         where user_type = 'ADMIN' and user_id = u.user_id group by user_id) " +
        "   and u.user_id = ? group by u.user_id) l " +
        "   on l.user_id = h.aid " +
        "where h.aid = ?";

    public AccountInfoModel getAccountInfo(Integer adminUid) throws Exception {
    	AccountInfoModel ai = new AccountInfoModel();

    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(ACCOUNT_INFO_SQL);
    		ps.setInt(1, adminUid);
    		ps.setInt(2, adminUid);
    		rs = ps.executeQuery();
    		if (rs.next()) {
    		    ai.setSubscriberId(rs.getString("id"));
          	    ai.setSchoolName(rs.getString("school_name"));
          	    ai.setSchoolUserName(rs.getString("responsible_name"));
          	    ai.setAdminUserName(rs.getString("user_name"));
      	        ai.setMaxStudents(rs.getInt("max_students"));
      	        ai.setTotalStudents(rs.getInt("student_count"));
      	        java.sql.Date dt = rs.getDate("catchup_expire_date");
      	        String cmDate = (dt != null) ? dt.toString() : "2009-07-31";
      	        ai.setExpirationDate(cmDate);
      	        dt = rs.getDate("tutoring_expire_date");
      	        if (dt != null && dt.after(new java.sql.Date(System.currentTimeMillis()))) {
      	        	ai.setHasTutoring("Enabled");
      	        }
      	        else {
      	        	ai.setHasTutoring("Not Enabled");
      	        }
      	        //java.sql.Time time = rs.getTime("login_time");
      	        //DateFormat df = DateFormat.getDateTimeInstance();
      	        ai.setLastLogin(rs.getString("login_date_time"));
    		}
    		else {
    			throw new Exception("*** No Account data found ***");
    		}
    	}
    	catch (Exception e) {
            logger.error(String.format("*** Error getting account info for admin id: %d", adminUid), e);
    		throw new Exception("*** Error getting Account data ***");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return ai;
    }
    
    /** Remove this user from the admin's scope
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
            if(ps.executeUpdate()==0) {
                logger.warn("User was not removed");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
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
		    }
		    else {
		    	throw new Exception("Unable to obtain last auto-increment id");
		    }
		}
		finally {
			SqlUtilities.releaseResources(rs, stmt, null);
		}
	}
    
    private static final String PROGRAM_SQL =
    	"select d.id, d.title, d.description, d.needs_subject, d.needs_chapter, d.needs_pass_percent, d.needs_state " +
    	"from  HA_PROG_DEF d where d.is_active = 1 " +
    	"and exists (select 1 from HA_TEST_DEF td where d.id = td.prog_id and td.is_active = 1) " +
        "order by id";
    
    @SuppressWarnings("unchecked")
	public List<StudyProgramModel> getProgramDefinitions() throws Exception {
    	
    	List<StudyProgramModel> rval =
    		(List<StudyProgramModel>)CmCacheManager.getInstance().retrieveFromCache(PROG_DEF, PROG_DEF.toString());
    	if (rval != null) return rval;
    	
    	Connection conn = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery(PROGRAM_SQL);
    		rval = loadProgramDefinitions(rs);
    		
    		CmCacheManager.getInstance().addToCache(PROG_DEF, PROG_DEF.toString(), rval);
    	}
    	catch (Exception e) {
    		logger.error("*** Error while getting Program definitions ***", e);
    		throw new Exception("*** Error while getting Program definitions ***");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, conn);
    	}
    	return rval;
    }

    /** Return the Admin object currently attached to this 
     *  SUBSCRIBER record or null
     *  
     * @param subscriberId  The subscriber record (id) to lookup
     * @return HaAdmin record, or null if no such record exists
     * 
     * @throws Exception on db errors
     */
    public HaAdmin getAdmin(String subscriberId) throws Exception {
        
        String GET_ADMIN_SQL = "select * from HA_ADMIN where subscriber_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(GET_ADMIN_SQL);
            ps.setString(1,subscriberId);
            
            rs = ps.executeQuery();
            if(!rs.first())
                return null;
            
            HaAdmin haAdmin = new HaAdmin();
            haAdmin.setAdminId(rs.getInt("aid"));
            haAdmin.setUserName(rs.getString("user_name"));
            haAdmin.setPassword(rs.getString("passcode"));
            
            return haAdmin;
        }
        catch (Exception e) {
        	logger.error(String.format("*** Error obtaining admins for subscriberId: %s", subscriberId), e);
        	throw new Exception(String.format("*** Error obtaining admins for subscriberId: %s", subscriberId));        	
        }
        finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }
    
    
    /** Create a new HA_ADMIN record
     * 
     * @param subscriberId  The subscriber to associated with this cm admin
     * @param userName The username for the admin account
     * @param passcode The passcode for this admin account
     * 
     * @return The admin_id of the just created HA_ADMIN record
     * 
     * @throws Exception
     */
    public HaAdmin addAdmin(String subscriberId, String userName, String passcode) throws Exception {
        
        String ADD_ADMIN_SQL = "insert into HA_ADMIN(subscriber_id, user_name, passcode, create_date)" +
                               "values(?,?,?,now())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(ADD_ADMIN_SQL);
            ps.setString(1,subscriberId);
            ps.setString(2, userName);
            ps.setString(3,passcode);
            int count = ps.executeUpdate();
            int adminId=0;
            if (count == 1) {
                adminId = this.getLastInsertId(conn);
            }
            else {
                throw new Exception("Could not create HA_ADMIN account for unknown reason");
            }

            HaAdmin haAdmin = new HaAdmin();
            haAdmin.setAdminId(adminId);
            haAdmin.setUserName(userName);
            haAdmin.setPassword(passcode);
            
            return haAdmin;
        }
        catch (Exception e) {
        	logger.error(String.format("*** Error creating admin, name: %s, subscriberId: %s, passcode: %s", userName, subscriberId, passcode), e);
        	throw new Exception(String.format("*** Error creating HA_ADMIN, name: %s, subscriberId: %s, passcode: %s", userName, subscriberId, passcode));
        }
        finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
    }

    public String getPrintableSummaryReportId(List<Integer> studentUids) {
    	String reportId = String.format("%d%d%d", studentUids.get(0), System.currentTimeMillis(), studentUids.size());
    	CmCacheManager.getInstance().addToCache(REPORT_ID, reportId, studentUids);
    	return reportId;
    }
    
    private List <StudyProgramModel> loadProgramDefinitions(ResultSet rs) throws Exception {
    	List <StudyProgramModel> l = new ArrayList<StudyProgramModel> ();
    	while (rs.next()) {
    		StudyProgramModel m = new StudyProgramModel();
            m.setShortTitle(rs.getString("id"));
            m.setTitle(rs.getString("title"));
    		m.setDescr(rs.getString("description"));
    		m.setNeedsChapters(rs.getInt("needs_chapter"));
    		m.setNeedsSubject(rs.getInt("needs_subject"));
    		m.setNeedsPassPercent(rs.getInt("needs_pass_percent"));
    		m.setNeedsState(rs.getInt("needs_state"));
            l.add(m);
    	}
    	return l;
    }
    
    private List <GroupModel> loadGroups(ResultSet rs) throws Exception {
    	List <GroupModel> l = new ArrayList<GroupModel>();
    	
    	while (rs.next()) {
    		GroupModel m = new GroupModel();
    		m.setId(String.valueOf(rs.getInt("id")));
    		m.setName(rs.getString("name"));
    		m.setDescription(rs.getString("description"));
    		m.setIsActive(String.valueOf(rs.getInt("is_active")));
    		l.add(m);
    	}
    	return l;
    }

    private List <ChapterModel> loadChapters(ResultSet rs) throws Exception {
    	List <ChapterModel> l = new ArrayList<ChapterModel>();
    	
    	while (rs.next()) {
    		ChapterModel m = new ChapterModel(String.valueOf(rs.getInt("title_number")), rs.getString("title"));
    		l.add(m);
    	}
    	return l;
    }

    private List <SubjectModel> loadSubjectDefinitions(ResultSet rs) throws Exception {
    	
    	List<SubjectModel> l = new ArrayList<SubjectModel>();
    	
    	while (rs.next()) {
    		SubjectModel sm = new SubjectModel(rs.getString("title"), rs.getString("id"));            
    		l.add(sm);
    	}
    	return l;
    }

}
