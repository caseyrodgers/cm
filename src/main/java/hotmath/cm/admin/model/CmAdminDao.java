package hotmath.cm.admin.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

public class CmAdminDao {
	
	private static final Logger logger = LoggerFactory.getLogger(CmAdminDao.class);

    public CmAdminDao() {
    	
    }

    private static final String GET_STUDENTS_SQL = 
/*
    	"SELECT h.uid, h.user_name as name, h.user_passcode as passcode, h.user_email as email, h.admin_id as admin_uid, " +
    	"  concat(t.subj_id, ' ', t.prog_id) as program, date_format(m.last_run_time,'%Y-%m-%d') as last_use_date, " +
        "  i.solution_views as solution_usage_count, i.video_views as video_usage_count, i.review_views as review_usage_count " +
        "FROM HA_ADMIN a INNER JOIN HA_USER h on a.aid = h.admin_id " +
        "  INNER JOIN HA_TEST_DEF t on h.test_def_id = t.test_def_id  " +
        "  LEFT JOIN v_HA_TEST_INMH_VIEWS_INFO i on i.user_name = h.user_name " +
        "  LEFT JOIN v_HA_TEST_RUN_max m on m.uid = h.uid " +
        "WHERE a.passcode = ? " +
        "ORDER by h.uid desc";
*/    
        "SELECT h.uid, h.user_name as name, h.user_passcode as passcode, h.user_email as email, h.admin_id as admin_uid, " +
        " t.user_id, t.test_segment, t.test_def_id, t.create_time, t.pass_percent, concat(d.subj_id, ' ', d.prog_id) as program, " +
        " date_format(m.last_run_time,'%Y-%m-%d') as last_use_date, 0 as has_tutoring, " +
        " i.solution_views as solution_usage_count, i.video_views as video_usage_count, i.review_views as review_usage_count, " +
        " ifnull(g.id, 0) as group_id, ifnull(g.name, 'none') as group_name " +
        "FROM  HA_ADMIN a " +
        "INNER JOIN HA_USER h on a.aid = h.admin_id " +
        "INNER JOIN (select user_id, max(create_time) as c_time " +
        "            from HA_TEST " +
        "            group by user_id) s on s.user_id = h.uid " +
        "INNER JOIN HA_TEST t on t.user_id = h.uid and t.create_time = s.c_time " +
        "INNER JOIN HA_TEST_DEF d on d.test_def_id = t.test_def_id " +
        "LEFT JOIN v_HA_TEST_INMH_VIEWS_INFO i on i.user_name = h.user_name " +
        "LEFT JOIN v_HA_TEST_RUN_max m on m.uid = h.uid " +
        "LEFT JOIN CM_GROUP g on g.id = h.group_id " +
        "WHERE a.passcode = ? and h.is_active = ? " +
        "ORDER by h.user_name asc";

    public List <StudentModel> getSummariesForActiveStudents(String adminPasscode) {
    	return getStudentSummaries(adminPasscode, true);
    }
    
    public List <StudentModel> getSummariesForInactiveStudents(String adminPasscode) {
    	return getStudentSummaries(adminPasscode, false);
    }

    public List <StudentModel> getStudentSummaries(String adminPasscode, Boolean isActive) {
    	List <StudentModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(GET_STUDENTS_SQL);
    		ps.setString(1, adminPasscode);
    		ps.setInt(2, (isActive)?1:0);
    		rs = ps.executeQuery();
    		
    		l = loadStudentSummaries(rs);
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting student summaries for Admin passcode: %s", adminPasscode), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    //TODO add Subject selection by school type (non-college, college)
    
    private static String SELECT_SUBJECTS_SQL = "select * from HA_SUBJ_DEF";
    
    public List <SubjectModel> getSubjectDefinitions() {
    	List <SubjectModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		
    		ps = conn.prepareStatement(SELECT_SUBJECTS_SQL);
    		//ps.setString(1, schoolType);
    		rs = ps.executeQuery();
    		
    		l = loadSubjectDefinitions(rs);
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting subject definitions for school type: %s", "any"), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    public void updateStudent(StudentModel sm, boolean studentIsChanged, boolean programIsChanged) {
    	
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
    		logger.error(String.format("*** Error getting student details for student username: %s", studentUsername), e);
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
        "where a.passcode = ? " +
        " and g.is_active = ? " +
        "UNION " +
        "select g.id, g.name, g.description, g.is_active " +
        "from CM_GROUP g " +
        "where  g.admin_id = 0 " +
        " and g.is_active = ? " +
        "order by name asc";

    public List <GroupModel> getActiveGroups(String adminPasscode) {
    	List <GroupModel> l = null;
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(GET_GROUPS_SQL);
    		ps.setString(1, adminPasscode);
    		ps.setInt(2, 1);
    		ps.setInt(3, 1);
    		rs = ps.executeQuery();
    		
    		l = loadGroups(rs);
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting groups for admin passcode: %s", adminPasscode), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return l;
    }
    
    public AccountInfoModel getAccountInfo(String adminPasscode) {
    	AccountInfoModel ai = new AccountInfoModel();

    	//TODO: retrieve from DB
    	ai.setSchoolName("Hotmath High");
    	
    	return ai;
    }
    
    private static final String ADD_USER_SQL =
    	"insert into HA_USER (user_name, user_passcode, user_email, group_id, admin_id " +
    	"values(?, ?, ?, ?, ?)";
    
    public StudentModel addStudent(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(UPDATE_USER_SQL);
    		ps.setString(1, sm.getName());
    		ps.setString(2, sm.getPasscode());
    		ps.setString(3, sm.getEmail());
    		ps.setInt(4, Integer.parseInt(sm.getGroupId()));
    		ps.setInt(5, sm.getAdminUid());
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error adding student with passcode: %s", sm.getPasscode()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return sm;    	

    	
    }
    
    private static final String DEACTIVATE_USER_SQL =
    	"update HA_USER set is_active = 0 where user_passcode = ?";
    
    public StudentModel deactivateUser(StudentModel sm) {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(DEACTIVATE_USER_SQL);
    		ps.setString(1, sm.getPasscode());
    		int result = ps.executeUpdate();
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error deactivating student with passcode: %s", sm.getPasscode()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	return sm;    	
    	
    }

    private static final String UPDATE_USER_SQL =
    	"update HA_USER set " +
    	" user_name = ?, user_passcode = ?, user_email = ?, group_id = ? " +
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
    		ps.setString(3, sm.getEmail());
    		ps.setInt(4, Integer.parseInt(sm.getGroupId()));
    		ps.setInt(5, sm.getUid());
    		int result = ps.executeUpdate();
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
    		//throw e;
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, conn);
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
    		sm.setGroup(rs.getString("group_name"));
            sm.setProgramDescr(rs.getString("program"));
            sm.setLastLogin(rs.getString("last_use_date"));
            int totalUsage = rs.getInt("video_usage_count") + rs.getInt("solution_usage_count") + rs.getInt("review_usage_count");
            sm.setTotalUsage(String.valueOf(totalUsage));
            String passPercent = String.valueOf(rs.getInt("pass_percent")) + " %";
            sm.setPassPercent(passPercent);
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
	
}
