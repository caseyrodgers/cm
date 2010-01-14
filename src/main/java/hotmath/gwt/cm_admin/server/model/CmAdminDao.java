package hotmath.gwt.cm_admin.server.model;

import static hotmath.cm.util.CmCacheManager.CacheName.PROG_DEF;
import static hotmath.cm.util.CmCacheManager.CacheName.REPORT_ID;
import static hotmath.cm.util.CmCacheManager.CacheName.SUBJECT_CHAPTERS;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelBasic;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.SaveAutoRegistrationAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.UserInfo.AccountType;
import hotmath.gwt.shared.server.service.command.SaveAutoRegistrationCommand;
import hotmath.testset.ha.CmProgram;
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

    //TODO add Subject selection by school type (non-college, college)

    
    public List <SubjectModel> getSubjectDefinitions(String progId) throws Exception {
    	List <SubjectModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		
    		ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("SELECT_SUBJECTS_SQL"));
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
    
    public CmList <GroupInfoModel> getActiveGroups(final Connection conn, Integer adminUid) throws Exception {
    	CmList <GroupInfoModel> l = null;
    	
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("SELECT_GROUPS_SQL"));
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
    		SqlUtilities.releaseResources(rs, ps, null);
    	}
    	return l;
    }

    private static final String ADD_GROUP_SQL =
    	"insert into CM_GROUP (name, description, is_active, admin_id) " +
    	"values( ?, ?, ?, ?)";

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
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(String.format("*** Error adding Group: %s, for adminUid: %d", gm.getName(), adminUid), e);
    	    throw new Exception(String.format("*** Error adding Group: %s ***", gm.getName()));
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, null);
    	}
    	return gm;
    }

    
    /** Delete named group from CM_GROUP
     * 
     *  First clear any assigned students to null group, then 
     *  remove from table.
     *  
     *  NOTE: will not delete default groups
     *        also, the passed in adminId is the adminId of the user, not the group .. which could be different.
     *  
     * @param conn
     * @param groupId
     * @throws Exception
     */
    public void deleteGroup(final Connection conn, Integer adminId, Integer groupId) throws Exception {
        Statement ps=null;
        try {
            ps = conn.createStatement();
            
            /** set to group 'none' the existing students assigned to this group
             * 
             */
            String sql = "update HA_USER set group_id = 1 where group_id = " + groupId; 
            ps.executeUpdate(sql);
            
            /** Do not remove default groups
             * 
             */
            sql = "delete from CM_GROUP where admin_id != 0 and id = " + groupId ;
            int cnt=ps.executeUpdate(sql);
            if(cnt != 1)
                logger.warn("No group found to delete: " + groupId);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
    
    
    public void updateGroup(final Connection conn, Integer groupId, String name) throws Exception {
        

        checkForReservedGroup(name);
        
        PreparedStatement ps=null;
        try {
            String sql = "update CM_GROUP set name = ? where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2,groupId);
            
            int cnt = ps.executeUpdate();
            if(cnt != 1)
                logger.warn("could not update group: " + groupId + " to " + name);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }    


    /** Do not allow groups to be named as already existing default names
     * 
     * @TODO: should be all default groups..?
     *  
     */
    private void checkForReservedGroup(String name) throws Exception {
        if(name.equals("none") || name.equals("All Students")) {
            throw new Exception("The group name '" + name + "' is reserved.");
        }
    }
    
    //TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_GROUP_SQL =
    	"select 1 from CM_GROUP where name = ? and admin_id in (?, 0)";
    
    public Boolean checkForDuplicateGroup(final Connection conn, Integer adminUid, GroupInfoModel gm) throws Exception {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
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
    		SqlUtilities.releaseResources(rs, ps, null);
    	}
    }

    @SuppressWarnings("unchecked")
	public CmList<ChapterModel> getChaptersForProgramSubject(final Connection conn, String progId, String subjId) throws Exception {
    	
    	String key = new StringBuilder(progId).append(".").append(subjId).toString();
    	CmList <ChapterModel> l = (CmList<ChapterModel>)CmCacheManager.getInstance().retrieveFromCache(SUBJECT_CHAPTERS, key);
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("+++ getChaptersForProgramSubject(): key: %s, retrieved: %s", key, ((l == null)?"NULL":l.size())));
    	}
    	
    	if (l != null) return l;
    	
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
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting chapters for progId: %s, subjId: %s", progId, subjId), e);
    		throw new Exception("*** Error getting Chapter list ***", e);
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, null);
    	}
    }

    public AccountInfoModel getAccountInfo(Integer adminUid) throws Exception {
    	Connection conn = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		return getAccountInfo(conn, adminUid);
    	}
    	finally {
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
      	        String cmDate = (dt != null) ? dt.toString() : "2009-12-31";  /** @TODO: remove hard-coded value */
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
    		SqlUtilities.releaseResources(rs, ps, null);
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

    public String getPrintableStudentReportId(List<Integer> studentUids) {
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
    
    private CmList <GroupInfoModel> loadGroups(ResultSet rs) throws Exception {
    	CmList <GroupInfoModel> l = new CmArrayList<GroupInfoModel>();
    	
    	while (rs.next()) {
    	    GroupInfoModel m = new GroupInfoModel();
    		m.setId(rs.getInt("id"));
    		m.setGroupName(rs.getString("name"));
    		m.setIsActive(rs.getInt("is_active")!=0);
    		m.setIsSelfReg(rs.getInt("is_auto_create_template")!=0);
    		l.add(m);
    	}
    	return l;
    }

    private CmList <ChapterModel> loadChapters(ResultSet rs) throws Exception {
    	CmList <ChapterModel> l = new CmArrayList<ChapterModel>();
    	
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
    
    
    /** Remove any Auto Registration Setup based on this group name
     * 
     *  NOTE: this only deletes users marked as is_auto_create_template > 0, not normal
     *  user .. each is_auto_create_template account will have a unique password.
     * 
     * @param conn
     * @param adminId
     * @param groupName
     * @throws Exception
     */
    public void removeAutoRegistrationSetupFor(final Connection conn, Integer adminId, String groupName) throws Exception {
        
        String sql  = "delete u " +
                      " from   HA_USER u JOIN CM_GROUP g ON u.group_id = g.id " +
                      " where u.admin_id = ? " +
                      " and    g.name = ?" +
                      " and is_auto_create_template = 1 ";
        
        
        PreparedStatement pstat=null;
        try {
            pstat = conn.prepareStatement(sql);
            
            pstat.setInt(1, adminId);
            pstat.setString(2, groupName);
            int c = pstat.executeUpdate();
            
            logger.debug("Removed auto setup: " + groupName + ", " + c);
        }
        finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }
    
    
    /** Mark this account as a Auto Registration Setup account.
     * 
     * THis will make this account participate in the auto creation 
     * routines during login. 
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
            if(cnt == 0) {
                throw new CmException("Could not setup auto creation account: " + uid);
            }
        }
        finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    
    
    
    /** Return the account type for this admin 
     * 
     * @throws Exception
     */
    public AccountType getAccountType(final Connection conn, Integer adminId) throws Exception {
        PreparedStatement ps=null;
        try {
           String sql = CmMultiLinePropertyReader.getInstance().getProperty("ACCOUNT_TYPE_LOOKUP"); 
           ps = conn.prepareStatement(sql);
           ps.setInt(1, adminId);
           
           ResultSet rs = ps.executeQuery();
           if(!rs.first())
               throw new CmException("Admin record does not have an associated SUBSCRIBER record");
           
           String type=rs.getString("type");
           if(type.equals(AccountType.SCHOOL_TEACHER.getTag()))
               return AccountType.SCHOOL_TEACHER;
           else if(type.equals(AccountType.PARENT_STUDENT.getTag()))
               return AccountType.PARENT_STUDENT;
           else 
               return AccountType.OTHER;
           
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
    
    public void createSelfRegistrationGroup(final Connection conn, Integer aid, String groupName, CmProgram program, Boolean tutoringEnabled, Boolean showWorkRequired) throws Exception {
        try {
            StudentModelI sm = new StudentModelBasic();
            sm.setName(groupName);
            sm.setGroup(groupName);
            sm.setAdminUid(aid);
            sm.setGroupId("1");
            sm.setProgId(program.getProgramId());
            sm.setSubjId(program.getSubject());
            sm.setPassPercent("70%");
            sm.setTutoringAvail(tutoringEnabled);
            sm.setShowWorkRequired(showWorkRequired);
            new SaveAutoRegistrationCommand().execute(conn,new SaveAutoRegistrationAction(aid, sm));
        }
        catch(Exception e) {
            throw new CmException("The self-registration group could not be created", e);
        }
    }    
}
