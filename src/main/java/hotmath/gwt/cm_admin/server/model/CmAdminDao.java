package hotmath.gwt.cm_admin.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

public class CmAdminDao {
	
	//private static final Logger logger = LoggerFactory.getLogger(CmAdminDao.class);
	private static final Log logger = LogFactory.getLog(CmAdminDao.class);

    public CmAdminDao() {
    }

    private static final String GET_STUDENTS_SQL = 
        "SELECT h.uid, h.user_name as name, h.user_passcode as passcode, h.user_email as email, h.admin_id as admin_uid, " +
        "       h.active_segment, p.test_def_id, p.create_date, concat(p.pass_percent,'%') as pass_percent, t.total_segments, " +
        "       trim(concat(ifnull(d.subj_id,''), ' ', d.prog_id)) as program, h.user_prog_id, " +
        "       date_format(m.last_run_time,'%Y-%m-%d') as last_use_date, 0 as has_tutoring, " +
        "       i.solution_views as solution_usage_count, i.video_views as video_usage_count, i.review_views as review_usage_count, " +
        "       ifnull(g.id, 0) as group_id, ifnull(g.name, 'none') as group_name " +
        "FROM  HA_ADMIN a " +
        "INNER JOIN HA_USER h " +
        "   on a.aid = h.admin_id " +
        "INNER JOIN CM_USER_PROGRAM p " +
        "   on p.user_id = h.uid and p.id = h.user_prog_id " +
        "LEFT JOIN (select user_id, max(create_time) as c_time, test_def_id from HA_TEST group by user_id) s" +
        "   on s.user_id = h.uid and s.test_def_id = p.test_def_id " +
        "LEFT JOIN HA_TEST t" +
        "   on t.user_id = h.uid and t.create_time = s.c_time " +
        "LEFT JOIN HA_TEST_DEF d " +
        "   on d.test_def_id = h.test_def_id " +
        "LEFT JOIN v_HA_TEST_INMH_VIEWS_INFO i " +
        "   on i.user_name = h.user_name " +
        "LEFT JOIN v_HA_TEST_RUN_max m " +
        "   on m.uid = h.uid " +
        "LEFT JOIN CM_GROUP g " +
        "   on g.id = h.group_id " +
        "WHERE a.aid = ? and h.is_active = ? " +
        "ORDER by h.user_name asc";

    public List <StudentModel> getSummariesForActiveStudents(Integer adminUid) {
    	return getStudentSummaries(adminUid, true);
    }
    
    public List <StudentModel> getSummariesForInactiveStudents(Integer adminUid) {
    	return getStudentSummaries(adminUid, false);
    }

    public List <StudentModel> getStudentSummaries(Integer adminUid, Boolean isActive) {
    	List <StudentModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(GET_STUDENTS_SQL);
    		ps.setInt(1, adminUid);
    		ps.setInt(2, (isActive)?1:0);
    		rs = ps.executeQuery();
    		
    		l = loadStudentSummaries(rs);
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error getting student summaries for Admin uid: %d", adminUid), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    //TODO add Subject selection by school type (non-college, college)
    
    private static String SELECT_SUBJECTS_SQL = "select * from HA_SUBJ_DEF where for_school = ?";
    
    public List <SubjectModel> getSubjectDefinitions() {
    	List <SubjectModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		
    		ps = conn.prepareStatement(SELECT_SUBJECTS_SQL);
    		//TODO: separate queries for schools and colleges
    		ps.setInt(1, 1);
    		rs = ps.executeQuery();
    		
    		l = loadSubjectDefinitions(rs);
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error getting subject definitions for school type: %s", "any"), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    private static final String GET_STUDENT_ACTIVITY_SQL =
    	"select date_format(create_time,'%Y-%m-%d') as use_date, date_format(create_time,'%h:%i %p') as start_time, " +
    	" date_format(r.run_time,'%h:%i %p') as stop_time, " +
    	" r.answered_correct, r.answered_incorrect, concat(td.subj_id, ' ', td.prog_id) as program, " +
    	" td.chapter as activity_section, " +  //TODO obtain activity_section from TEST_DEF.test_config_json
    	" l.test_id as test_id, td.test_def_id as test_def_id, r.run_id as test_run_id " +
        "from  HA_TEST l INNER JOIN HA_USER u ON l.user_id = u.uid " +
        "join HA_TEST_RUN r on r.test_id = l.test_id " +
        "join HA_TEST_DEF td on td.test_def_id = l.test_def_id " +
        "where u.user_name = ? " +
        "order by use_date desc";

    public List <StudentActivityModel> getStudentActivity(String studentUsername) {
    	List <StudentActivityModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(GET_STUDENT_ACTIVITY_SQL);
    		ps.setString(1, studentUsername);
    		rs = ps.executeQuery();
    		
    		l = loadStudentActivity(rs);
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error getting student details for student username: %s", studentUsername), e);
    		//throw e;
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

    public List <GroupModel> getActiveGroups(Integer adminUid) {
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
    		//logger.error(String.format("*** Error getting groups for admin uid: %d", adminUid), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    public AccountInfoModel getAccountInfo(Integer adminUid) {
    	AccountInfoModel ai = new AccountInfoModel();

    	//TODO: retrieve from DB
    	ai.setSchoolName("Hotmath High");
    	
    	return ai;
    }
    
    private static final String ADD_USER_SQL =
    	"insert into HA_USER (user_name, user_passcode, active_segment, group_id, test_def_id, admin_id, is_active) " +
    	"values(?, ?, ?, ?, (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), ?, 1)";
    
    public StudentModel addStudent(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	addStudentProgram(sm);
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(ADD_USER_SQL);
    		ps.setString(1, sm.getName());
    		ps.setString(2, sm.getPasscode());
    		ps.setInt(3, 0);
    		ps.setInt(4, Integer.parseInt(sm.getGroupId()));
    		Map <String, String> m = getSubjIdAndProgId(sm);
    		ps.setString(5, m.get("progId"));
    		ps.setString(6, m.get("subjId"));
    		ps.setInt(7, sm.getAdminUid());
    		
    		int count = ps.executeUpdate();
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error adding student with passcode: %s", sm.getPasscode()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return sm;    	

    	
    }
    
    private static final String DEACTIVATE_USER_SQL =
    	"update HA_USER set is_active = 0 where uid = ?";
    
    public StudentModel deactivateUser(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(DEACTIVATE_USER_SQL);
    		ps.setInt(1, sm.getUid());
    		int result = ps.executeUpdate();
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return sm;    	
    	
    }

    public StudentModel updateStudent(StudentModel sm, Boolean studentChanged, Boolean programChanged, Boolean progIsNew) {
    	if (progIsNew)
    		addStudentProgram(sm);
    	if (studentChanged)
    		updateStudent(sm);
    	if (programChanged)
    		updateStudentProgram(sm);
    	return sm;
    }

    private static final String UPDATE_USER_SQL =
    	"update HA_USER set " +
    	" user_name = ?, user_passcode = ?, group_id = ?, active_segment = ?, " +
    	" test_def_id = (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?) " +
    	"where uid = ?";
    
    public StudentModel updateStudent(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(UPDATE_USER_SQL);
    		ps.setString(1, sm.getName());
    		ps.setString(2, sm.getPasscode());
    		//ps.setString(3, sm.getEmail());
    		ps.setInt(3, Integer.parseInt(sm.getGroupId()));
    		int sectionNum = (sm.getSectionNum() != null) ? sm.getSectionNum().intValue() : 0;
    		ps.setInt(4, sectionNum);
    		Map <String, String> m = getSubjIdAndProgId(sm);
    		ps.setString(5, m.get("progId"));
    		ps.setString(6, m.get("subjId"));
    		ps.setInt(7, sm.getUid());
    		int result = ps.executeUpdate();
    	}
    	catch (Exception e) {
    		System.out.println("Exception: " + e.getMessage());
    		//logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return sm;    	
    }

    private static final String UPDATE_USER_PROGRAM_SQL =
    	"update CM_USER_PROGRAM " +
    	"set pass_percent = ? " +
    	"where id = ?";

    private static final String UPDATE_USER_PROGRAM_NULL_PASS_PERCENT_SQL =
    	"update CM_USER_PROGRAM " +
    	"set pass_percent = null " +
    	"where id = ?";

    public StudentModel updateStudentProgram(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		String s = sm.getPassPercent();

    		if (s != null) {
    			int passPcnt = Integer.parseInt(s.substring(0, s.indexOf("%")));
    			ps = conn.prepareStatement(UPDATE_USER_PROGRAM_SQL);
    			ps.setInt(1, passPcnt);
    			ps.setInt(2, sm.getUserProgramId());
    		}
    		else {
       			ps = conn.prepareStatement(UPDATE_USER_PROGRAM_NULL_PASS_PERCENT_SQL);
    			ps.setInt(1, sm.getUserProgramId());
    		}
    		
    		int result = ps.executeUpdate();
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(null, ps, conn);
    	}
    	return sm;    	
    }
    
    private static final String INSERT_USER_PROGRAM_SQL =
    	"insert CM_USER_PROGRAM (user_id, admin_id, test_def_id, pass_percent, create_date) " +
    	"values (?, ?, (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), ?, ?)";

    private static final String INSERT_USER_PROGRAM_NULL_PASS_PERCENT_SQL =
    	"insert CM_USER_PROGRAM (user_id, admin_id, test_def_id, create_date) " +
    	"values (?, ?, (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), ?, ?)";
    
    private static final String SELECT_LAST_INSERT_ID_SQL = "select LAST_INSERT_ID()";

    public StudentModel addStudentProgram(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		String s = sm.getPassPercent();

    		if (s != null) {
    			int passPcnt = Integer.parseInt(s.substring(0, s.indexOf("%")));
    			ps = conn.prepareStatement(INSERT_USER_PROGRAM_SQL);
    			ps.setInt(1, sm.getUid());
    			ps.setInt(2, sm.getAdminUid());
        		Map <String, String> m = getSubjIdAndProgId(sm);
        		ps.setString(3, m.get("progId"));
        		ps.setString(4, m.get("subjId"));
    			ps.setInt(5, passPcnt);
    			ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
    		}
    		else {
       			ps = conn.prepareStatement(INSERT_USER_PROGRAM_NULL_PASS_PERCENT_SQL);
    			ps.setInt(1, sm.getUid());
    			ps.setInt(2, sm.getAdminUid());
        		Map <String, String> m = getSubjIdAndProgId(sm);
        		ps.setString(3, m.get("progId"));
        		ps.setString(4, m.get("subjId"));
    			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
    		}
    		int result = ps.executeUpdate();
    		if (result == 1) {
    			// now get value of auto-increment id from CM_USER_PROGRAM
    			Statement stmt = null;
    			ResultSet rs = null;
    			try {
    			    stmt = conn.createStatement();
    			    // Note: this is MySQL specific
    		        rs = stmt.executeQuery(SELECT_LAST_INSERT_ID_SQL);
    		        if (rs.next()) {
    		        	int val = rs.getInt(1);
    		        	sm.setUserProgramId(val);
    		        }
    			}
    			finally {
    				SqlUtilities.releaseResources(rs, stmt, null);
    			}
    		}
    	}
    	catch (Exception e) {
    		//logger.error(String.format("*** Error adding student program for student with uid: %d", sm.getUid()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(null, ps, conn);
    	}
    	return sm;    	
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

    private List <StudentModel> loadStudentSummaries(ResultSet rs) throws Exception {
    	
    	List<StudentModel> l = new ArrayList<StudentModel>();
    	
    	while (rs.next()) {
    		StudentModel sm = new StudentModel();
    		sm.setUid(rs.getInt("uid"));
    		sm.setAdminUid(rs.getInt("admin_uid"));
    		sm.setName(rs.getString("name"));
    		sm.setPasscode(rs.getString("passcode"));
            sm.setEmail(rs.getString("email"));
            
    		int groupId = rs.getInt("group_id");
    		sm.setGroupId(String.valueOf(groupId));
    		sm.setUserProgramId(rs.getInt("user_prog_id"));
    		sm.setGroup(rs.getString("group_name"));
            sm.setProgramDescr(rs.getString("program"));
            sm.setLastLogin(rs.getString("last_use_date"));
            int totalUsage = rs.getInt("video_usage_count") + rs.getInt("solution_usage_count") + rs.getInt("review_usage_count");
            sm.setTotalUsage(String.valueOf(totalUsage));
            String passPercent = rs.getString("pass_percent");
            sm.setPassPercent(passPercent);
            int sectionNum = rs.getInt("active_segment");
            if (sectionNum > 0) {
            	int segmentCount = rs.getInt("total_segments");
            	String status = new StringBuilder("Section ").append(sectionNum).append(" of ").append(segmentCount).toString();
            	sm.setStatus(status);
            }
            else {
            	sm.setStatus("Not started");
            }
            String tutoringState = (rs.getInt("has_tutoring") > 0) ? "ON": "OFF";
            sm.setTutoringState(tutoringState);
            
    		l.add(sm);
    	}
    	return l;
    }

    private List <StudentActivityModel> loadStudentActivity(ResultSet rs) throws Exception {
    	
    	List<StudentActivityModel> l = new ArrayList<StudentActivityModel>();
    	
    	while (rs.next()) {
    		StudentActivityModel m = new StudentActivityModel();
    		m.setProgramDescr(rs.getString("program"));
    		m.setUseDate(rs.getString("use_date"));
    		m.setStart(rs.getString("start_time"));
    		m.setStop(rs.getString("stop_time"));
    		m.setActivity(rs.getString("activity_section"));
    		StringBuilder sb = new StringBuilder();
    		// TODO differentiate btwn started, completed, retake...
    		sb.append("completed, ");
    		int numCorrect = rs.getInt("answered_correct");
    		int numIncorrect = rs.getInt("answered_incorrect");
    		sb.append(numCorrect).append(" out of ").append(numCorrect + numIncorrect).append(" correct");
            m.setResult(sb.toString());
            
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
	
    private Map<String, String> getSubjIdAndProgId(StudentModel sm) {
    	Map<String, String> m = new HashMap<String, String> ();
    	
		String shortName = sm.getProgramDescr();
		int offset = shortName.lastIndexOf(" ");
		String subjId = "";
		if (offset > -1) {
			subjId = shortName.substring(0, offset);
		}
		String progId = shortName.substring(offset+1);
		
		m.put("subjId", subjId);
		m.put("progId", progId);
    	return m;
    }
}
