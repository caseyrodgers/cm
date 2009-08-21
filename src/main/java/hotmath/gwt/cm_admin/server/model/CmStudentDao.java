package hotmath.gwt.cm_admin.server.model;

import hotmath.assessment.InmhItemData;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelBasic;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDefDescription;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
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
import org.json.JSONObject;

public class CmStudentDao {

    private static final Logger logger = Logger.getLogger(CmStudentDao.class);

    public CmStudentDao() {
    }

    enum StudentSqlType {
        SINGLE_STUDENT, ALL_STUDENTS_FOR_ADMIN
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
    private String getStudentSql(StudentSqlType sqlType, Boolean includeSelfRegistrationTemplates) {
        String studentSql =
        	"SELECT h.uid, h.user_name as name, h.user_passcode as passcode, h.user_email as email, h.admin_id as admin_uid, " +
            "       h.is_show_work_required, h.is_tutoring_available,  h.active_segment, p.test_config_json, h.user_prog_id, " +
            "       p.test_def_id, p.create_date, concat(p.pass_percent,'%') as pass_percent, t.total_segments, " +
            "       lpad(concat(format((m.answered_correct*100)/(m.answered_correct+m.answered_incorrect+m.not_answered),0),'%'),4,' ') as last_quiz, " +
            "       trim(concat(ifnull(d.subj_id,''), ' ', d.prog_id)) as program, d.prog_id, d.subj_id, " +
            "       date_format(m.run_time,'%Y-%m-%d') as last_use_date, " +
            "       tu.usage_count, ifnull(g.id, 0) as group_id, ifnull(g.name, 'none') as group_name, gui_background_style " + 
            "FROM  HA_ADMIN a " +
            "INNER JOIN HA_USER h " +
            "   on a.aid = h.admin_id " + 
            "INNER JOIN CM_USER_PROGRAM p " +
            "   on p.user_id = h.uid and p.id = h.user_prog_id " + 
            "LEFT JOIN HA_TEST_DEF d " +
            "   on d.test_def_id = p.test_def_id " + 
            "LEFT JOIN HA_TEST_RUN m " +
            "  on m.run_id = h.active_run_id " + 
            "LEFT JOIN HA_TEST t " +
            "  on t.user_id = h.uid and t.test_id = m.test_id " +
            "LEFT JOIN (select u.uid, count(*) as usage_count from HA_TEST_RUN_INMH_USE i, HA_TEST t, HA_TEST_RUN r, HA_USER u " + 
            "           where t.user_id = u.uid and r.test_id = t.test_id and i.run_id = r.run_id group by u.uid) tu " +
            "   on tu.uid = h.uid " +
            "LEFT JOIN CM_GROUP g " +
            "   on g.id = h.group_id ";

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

    public List<StudentModel> getSummariesForActiveStudents(Integer adminUid) throws Exception {
        return getStudentSummaries(adminUid, true);
    }

    public List<StudentModel> getSummariesForInactiveStudents(Integer adminUid) throws Exception {
        return getStudentSummaries(adminUid, false);
    }

    public List<StudentModel> getStudentSummaries(Integer adminUid, Boolean isActive) throws Exception {
        List<StudentModel> l = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(getStudentSql(StudentSqlType.ALL_STUDENTS_FOR_ADMIN, false));
            ps.setInt(1, adminUid);
            ps.setInt(2, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentSummaries(rs);
            
            loadChapterInfo(conn, l);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting student summaries for Admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting student summary data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
        return l;
    }

	private void loadChapterInfo(final Connection conn, List<StudentModel> l) throws Exception {

		CmAdminDao dao = new CmAdminDao();
		
		for (StudentModel sm : l) {
			String chapter = sm.getChapter();
			if (chapter != null) {
				String subjId = sm.getSubjId();
		        List <ChapterModel> cmList = dao.getChaptersForProgramSubject(conn, "Chap", subjId);
		        for (ChapterModel cm : cmList) {
		        	if (cm.getTitle().equals(chapter)) {
		        		sm.setProgramDescr(new StringBuilder(sm.getProgramDescr()).append(" ").append(cm.getNumber()).toString());
		        		break;
		        	}
		        }
		        
			}
		}
	}

    private static final String GET_STUDENT_ACTIVITY_SQL =
            "select max(s.use_date) as use_date, date_format(min(s.view_time),'%h:%i %p') as start_time, " +
            "  date_format(max(s.view_time),'%h:%i %p') as stop_time, max(s.view_time) as view_time, " +
            "  max(s.run_date) as run_date, " +
            "  s.answered_correct, s.answered_incorrect, s.not_answered, s.program, s.prog_id, s.subj_id, s.test_config_json, " +
            "  s.test_id as test_id, max(s.test_segment) as test_segment, s.test_def_id, s.test_run_id, " +
            "  s.activity, s.is_quiz, count(*) as problems_viewed, max(s.session_number) as session_number, " +
            "  s.total_sessions " +
            "from ( " +
            " select date_format(l.create_time,'%Y-%m-%d') as use_date, date_format(l.create_time,'%h:%i %p') as start_time, " +
            "   date_format(r.run_time,'%h:%i %p') as stop_time, r.run_time as view_time, " +
            "   date_format(r.run_time,'%Y-%m-%d') as run_date, " +
            "   r.answered_correct, r.answered_incorrect, r.not_answered, " +
            "   concat(td.subj_id, ' ', td.prog_id) as program,  td.prog_id, td.subj_id, p.test_config_json, " +
            "   l.test_id as test_id, l.test_segment, td.test_def_id as test_def_id, " +
            "   r.run_id as test_run_id, 0 as total_sessions, " +
            "   'Quiz-' as activity, 1 as is_quiz, l.test_segment as session_number " +
            " from  HA_TEST l INNER JOIN HA_USER u ON l.user_id = u.uid " +
            " join HA_TEST_RUN r on r.test_id = l.test_id " +
            " join HA_TEST_DEF td on td.test_def_id = l.test_def_id " +
            " join CM_USER_PROGRAM p on p.user_id = u.uid and p.test_def_id = td.test_def_id " +
            " where u.uid = ? " +
            " union " +
            " select date_format(iu.view_time,'%Y-%m-%d') as use_date, date_format(iu.view_time,'%h:%i %p') as start_time, " +
            "  date_format(iu.view_time,'%h:%i %p') as stop_time, iu.view_time as view_time, " +
            "  date_format(iu.view_time,'%Y-%m-%d') as run_date, " +
            "  0 as answered_correct, 0 as answered_incorrect, 0 as not_answered, " +
            "  concat(td.subj_id, ' ', td.prog_id) as program, td.prog_id, td.subj_id, p.test_config_json, " +
            "  l.test_id as test_id, iu.session_number as test_segment, td.test_def_id as test_def_id, " +
            "  r.run_id as test_run_id, r.total_sessions as total_sessions, " +
            "  'Review-' as activity, 0 as is_quiz, iu.session_number as session_number " +
            " from  HA_TEST l INNER JOIN HA_USER u ON l.user_id = u.uid " +
            " join HA_TEST_RUN r on r.test_id = l.test_id " +
            " join HA_TEST_RUN_INMH_USE iu on iu.run_id = r.run_id and iu.item_type = 'practice' " +
            " join HA_TEST_DEF td on td.test_def_id = l.test_def_id " +
            " join CM_USER_PROGRAM p on p.user_id = u.uid and p.test_def_id = td.test_def_id " +
            " where u.uid = ? " +
            ") s " +
            "group by s.test_run_id, s.is_quiz, s.use_date " +
            "order by s.view_time asc, s.test_run_id asc, s.session_number asc";

    public List<StudentActivityModel> getStudentActivity(int uid) throws Exception {
        List<StudentActivityModel> l = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(GET_STUDENT_ACTIVITY_SQL);
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            rs = ps.executeQuery();

            l = loadStudentActivity(conn, rs);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting student details for student uid: %d", uid), e);
            throw new Exception("*** Error getting student details ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
        return l;
    }

    private static final String GET_TEST_RESULTS_SQL =
        "select td.test_name, t.test_segment, im.file, rr.pid, rr.answer_status " +
        "from HA_TEST_DEF td, HA_TEST t, HA_TEST_RUN tr, HA_TEST_RUN_RESULTS rr, inmh_map im " +
        "where tr.run_id = ? and rr.run_id = tr.run_id and tr.test_id = t.test_id " +
        "  and t.test_def_id = td.test_def_id and im.guid = rr.pid";
    
    public List <LessonItemModel> getLessonItemsForTestRun(final Connection conn, Integer runId) throws Exception {
   	    List <LessonItemModel> l = null;
    	
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		ps = conn.prepareStatement(GET_TEST_RESULTS_SQL);
    		ps.setInt(1, runId);
    		rs = ps.executeQuery();
    		
    		if (rs.next()) {
    			l = loadLessonItems(rs);
    			sortLessonItems(l);
    		}
    	}
    	catch (Exception e) {
    		logger.error(String.format("*** Error getting lesson items for runId:  %d", runId), e);
    		throw new Exception("*** Error getting Lesson Items ***");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, ps, null);
    	}
    	return l;
    }

    private static final String ADD_STUDENT_SQL =
            "insert into HA_USER (user_name, user_passcode, active_segment, group_id, test_def_id, admin_id, is_active) " +
            "values(?, ?, ?, ?, (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), ?, 1)";

    public StudentModel addStudent(final Connection conn, StudentModel sm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        Boolean isDuplicate = checkForDuplicatePasscode(conn, sm);
        if (isDuplicate) {
            throw new Exception("The passcode you entered is already in use, please try again.");
        }

        try {
            ps = conn.prepareStatement(ADD_STUDENT_SQL);
            ps.setString(1, sm.getName());
            ps.setString(2, sm.getPasscode());
            ps.setInt(3, 0);
            ps.setInt(4, Integer.parseInt(sm.getGroupId()));
            ps.setString(5, sm.getProgId());
            ps.setString(6, sm.getSubjId());
            ps.setInt(7, sm.getAdminUid());

            int count = ps.executeUpdate();
            if (count == 1) {
                int stuUid = this.getLastInsertId(conn);
                sm.setUid(stuUid);
                addStudentProgram(sm);
                updateStudent(sm);
            }
        } catch (Exception e) {
            throw new Exception(String.format("Error adding Student: %s, Passcode: %s ***", sm.getName(), sm.getPasscode()),e);
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return sm;
    }

    private static final String DEACTIVATE_STUDENT_SQL =
            "update HA_USER set is_active = 0, user_passcode = ? where uid = ?";

    public StringHolder unregisterStudents(Connection conn, List<StudentModel> smList) {
    	int removeCount = 0;
    	int removeErrorCount = 0;
    	int deactivateCount = 0;
    	int deactivateErrorCount = 0;
        PreparedStatement ps = null;
        
    	for (StudentModel sm : smList) {
            Statement stmt = null;
            ResultSet rsCheck = null;
            try {
                /*
                 *  Remove from DB if user has never used account
                 */
                stmt = conn.createStatement();
                rsCheck = stmt.executeQuery("select 'x' from HA_TEST where user_id = " + sm.getUid());
                if(!rsCheck.first()) {
                    removeUser(conn, sm);
                    removeCount++;
                    continue;
                }
            }
            catch (Exception e) {
            	removeErrorCount++;
            	logger.error(String.format("*** Error removing student with uid: %d", sm.getUid()), e);
            }
            finally {
                SqlUtilities.releaseResources(rsCheck, stmt, null);
            }
            
            /*
             * account has been used, deactivate
             */
            try {
                ps = conn.prepareStatement(DEACTIVATE_STUDENT_SQL);
                StringBuilder sb = new StringBuilder();
                sb.append(sm.getUid()).append(".").append(System.currentTimeMillis());
                ps.setString(1, sb.toString());
                ps.setInt(2, sm.getUid());
                if (ps.executeUpdate() < 1) {
                	deactivateErrorCount++;
                	logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()));
                }
                deactivateCount++;
            }
            catch (Exception e) {
            	deactivateErrorCount++;
            	logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()), e);
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
     * NOTE: if student does not any history (no entry in HA_TEST), then delete
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
            
            
            
            ps = conn.prepareStatement(DEACTIVATE_STUDENT_SQL);
            StringBuilder sb = new StringBuilder();
            sb.append(sm.getUid()).append(".").append(System.currentTimeMillis());
            ps.setString(1, sb.toString());
            ps.setInt(2, sm.getUid());
            if (ps.executeUpdate() < 1) {
                logger.error(String.format("user deactivation failed; SQL: %s", ps.toString()));
                throw new Exception(String.format("*** Error deactivating student with uid: %d", sm.getUid()));
            }
        } catch (Exception e) {
            logger.error(String.format("*** Error deactivating student with uid: %d", sm.getUid()), e);
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
            logger.info("Removing user: " + sm.getUid());
            ps = conn.prepareStatement(REMOVE_USER_SQL);
            ps.setInt(1, sm.getUid());
            if (ps.executeUpdate() == 0) {
                logger.warn("User was not removed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    public StudentModelI updateStudent(final Connection conn, StudentModelI sm, Boolean studentChanged, Boolean programChanged,
            Boolean progIsNew,
            Boolean passcodeChanged) throws Exception {
        if (passcodeChanged) {
            Boolean isDuplicate = checkForDuplicatePasscode(conn, sm);
            if (isDuplicate) {
                throw new Exception("The passcode you entered is already in use, please try again.");
            }
        }
        if (progIsNew)
            addStudentProgram(sm);
        if (studentChanged)
            updateStudent(sm);
        if (programChanged)
            updateStudentProgram(sm);
        return sm;
    }

    // TODO: assumes a single Admin per school
    private static final String CHECK_DUPLICATE_PASSCODE_SQL =
            "select 1 from HA_USER where user_passcode = ? and uid <> ? and admin_id = ?";

    public Boolean checkForDuplicatePasscode(final Connection conn, StudentModelI sm) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CHECK_DUPLICATE_PASSCODE_SQL);
            ps.setString(1, sm.getPasscode());
            ps.setInt(2, (sm.getUid() != null) ? sm.getUid() : -1);
            ps.setInt(3, sm.getAdminUid());

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            logger.error(String.format("*** Error checking passcode for student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error checking passcode for student: %s ***", sm.getName()));
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
    
    private static final String UPDATE_STUDENT_SQL =
            "update HA_USER set " +
            " user_name = ?, user_passcode = ?, group_id = ?, active_segment = ?, " +
            " test_def_id = (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), " +
            " user_prog_id = ?, is_tutoring_available = ?, is_show_work_required = ?, gui_background_style = ? " +
            "where uid = ?";

    /** Update the StudentModel
     * 
     * @TODO: move the sectionNum to segmentNum and then move to ActiveInfo
     * 
     * @param sm
     * @return
     * @throws Exception
     */
    public StudentModelI updateStudent(StudentModelI sm) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(UPDATE_STUDENT_SQL);
            ps.setString(1, sm.getName());
            ps.setString(2, sm.getPasscode());
            // ps.setString(3, sm.getEmail());
            ps.setInt(3, Integer.parseInt(sm.getGroupId()));
            /** @TODO: why is section needed here?  Should be in ActiveInfo
            //int sectionNum = (sm.getSectionNum() != null) ? sm.getSectionNum().intValue() : 0;
             * 
             */
            ps.setInt(4, 0);  // set constant zero, see if we can remove
            
            ps.setString(5, sm.getProgId());
            ps.setString(6, sm.getSubjId());
            ps.setInt(7, sm.getUserProgramId());
            ps.setInt(8, sm.getTutoringAvail() ? 1 : 0);
            ps.setInt(9, sm.getShowWorkRequired() ? 1 : 0);
            ps.setString(10, sm.getBackgroundStyle());
            ps.setInt(11, sm.getUid());
            int result = ps.executeUpdate();
        } catch (Exception e) {
            logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error occurred while updating student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
        }
        return sm;
    }

    private static final String UPDATE_STUDENT_PROGRAM_SQL =
            "update CM_USER_PROGRAM " +
            "set pass_percent = ? " +
            "where id = ?";

    private static final String UPDATE_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL =
            "update CM_USER_PROGRAM " +
            "set pass_percent = null " +
            "where id = ?";

    public StudentModelI updateStudentProgram(StudentModelI sm) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = HMConnectionPool.getConnection();
            String s = sm.getPassPercent();

            if (s != null) {
                int passPcnt = Integer.parseInt(s.substring(0, s.indexOf("%")));
                ps = conn.prepareStatement(UPDATE_STUDENT_PROGRAM_SQL);
                ps.setInt(1, passPcnt);
                ps.setInt(2, sm.getUserProgramId());
            } else {
                ps = conn.prepareStatement(UPDATE_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL);
                ps.setInt(1, sm.getUserProgramId());
            }

            int result = ps.executeUpdate();

            setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());

        } catch (Exception e) {
            logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error occurred while updating Student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
        }
        return sm;
    }

    private static final String INSERT_STUDENT_PROGRAM_SQL =
            "insert CM_USER_PROGRAM (user_id, admin_id, test_def_id, pass_percent, create_date, test_config_json) " +
            "values (?, ?, (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), ?, ?,?)";

    private static final String INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL =
            "insert CM_USER_PROGRAM (user_id, admin_id, test_def_id, create_date,test_config_json) " +
            "values (?, ?, (select test_def_id from HA_TEST_DEF where prog_id = ? and subj_id = ?), ?, ?)";

    public StudentModelI addStudentProgram(StudentModelI sm) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = HMConnectionPool.getConnection();

            setTestConfig(conn, sm);

            String pp = sm.getPassPercent();
            if (pp != null) {
                int passPcnt = Integer.parseInt(pp.substring(0, pp.indexOf("%")));
                ps = conn.prepareStatement(INSERT_STUDENT_PROGRAM_SQL);
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, sm.getProgId());
                ps.setString(4, sm.getSubjId());
                ps.setInt(5, passPcnt);
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                ps.setString(7, sm.getJson());
            } else {
                ps = conn.prepareStatement(INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL);
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, sm.getProgId());
                ps.setString(4, sm.getSubjId());
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setString(6, sm.getJson());
            }
            int result = ps.executeUpdate();
            if (result == 1) {
                // now get value of auto-increment id from CM_USER_PROGRAM
                int val = getLastInsertId(conn);
                sm.setUserProgramId(val);
            }
            
            sm.setSectionNum(0);
            setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());
        } catch (Exception e) {
            String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
            logger.error(m, e);
            throw new Exception(m, e);
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
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
    private void setTestConfig(final Connection conn, StudentModelI sm) throws Exception {

        ResultSet rs = null;
        PreparedStatement ps2 = null;
        String sql = "select test_config_json from HA_TEST_DEF where subj_id = ? and prog_id = ?";
        String json = "";
        try {
            ps2 = conn.prepareStatement(sql);
            ps2.setString(1, sm.getSubjId());
            String progId = sm.getProgId();
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
            throw new CmException("Could not configure test",e);
        } finally {
            SqlUtilities.releaseResources(rs, ps2, null);
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
    public List<StudentShowWorkModel> getStudentShowWork(int uid, Integer runId) throws Exception {

        List<StudentShowWorkModel> swModels = new ArrayList<StudentShowWorkModel>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "select distinct a.pid,a.run_id, b.* " +
                "from   HA_TEST_RUN_WHITEBOARD a, " +
                "( " +
                "select  user_id, pid, max(insert_time_mills) as insert_time_mills  " +
                " from HA_TEST_RUN_WHITEBOARD b " +
                " group by user_id, pid " +
                " ) b " +
                " where a.user_id = ?  " +
                " and   b.pid = a.pid " +
                " and   b.user_id = a.user_id " +
                " order by insert_time_mills desc";

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            rs = ps.executeQuery();

            SimpleDateFormat dteForat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            // int prob = 1;
            while (rs.next()) {

                /**
                 * Quick hack to restrict to runId if specified
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
            e.printStackTrace();
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
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
    public StudentModel getStudentModel(Integer uid) throws Exception {
        // default is without templates
        return getStudentModel(uid, false);  
    }
    
    
    /** Return student model named by uid
     *  
     * @param uid
     * @param includeSelfRegTemplate  If true, then is_auto_create_template will be considered
     * @return
     * @throws Exception
     */
    public StudentModel getStudentModel(Integer uid, Boolean includeSelfRegTemplate) throws Exception {
        
        long timeStart = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(getStudentSql(StudentSqlType.SINGLE_STUDENT, includeSelfRegTemplate));
            ps.setInt(1, uid);
            ps.setInt(2, 1);
            rs = ps.executeQuery();

            List<StudentModel> l = null;
            l = loadStudentSummaries(rs);
            if (l.size() == 0)
                throw new Exception(String.format("Student with UID: %d was not found", uid));
            if (l.size() > 1)
                throw new Exception(String.format("Student with UID: %d matches more than one row", uid));

            loadChapterInfo(conn, l);
            
            return l.get(0);
        } catch (Exception e) {
            logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(rs, ps, conn);
            logger.info(String.format("End getStudentModel(), UID: %d, elapsed seconds: %d", uid, ((System.currentTimeMillis() - timeStart)/1000)));
        }
    }
    
    /**
     * Return a basic student object that only contains the most important data.
     * 
     * The other pieces of info will be loaded lazily.
     *  
     * 
     * @param conn
     * @param uid
     * @return
     * @throws Exception
     */
    
    public StudentModelI getStudentModelBasic(final Connection conn, Integer uid) throws Exception {
        
        String sql = "select * from HA_USER where uid = " + uid;
        long timeStart = System.currentTimeMillis();
        Statement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.createStatement();
            rs = ps.executeQuery(sql);
            if(!rs.first())
                throw new CmException("No such student id: " + uid);
            
            StudentModelI sm = new StudentModelBasic();
            sm.setUid(uid);
            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_id"));
            sm.setName(rs.getString("user_name"));
            sm.setPasscode(rs.getString("user_passcode"));
            sm.setEmail(rs.getString("user_email"));
            sm.setGroupId(rs.getString("group_id"));
            sm.setUserProgramId(rs.getInt("user_prog_id"));
            sm.setBackgroundStyle(rs.getString("gui_background_style"));
            sm.setShowWorkRequired(rs.getInt("is_show_work_required")==0?false:true);
            sm.setTutoringAvail(rs.getInt("is_tutoring_available")==0?false:true);

            
            return sm;
            
        } catch (Exception e) {
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
            logger.info(String.format("End getStudentModelBasic(), UID: %d, elapsed seconds: %d", uid, ((System.currentTimeMillis() - timeStart)/1000)));
        }
    }

    private List<StudentModel> loadStudentSummaries(ResultSet rs) throws Exception {

        List<StudentModel> l = new ArrayList<StudentModel>();

        while (rs.next()) {
            StudentModel sm = new StudentModel();
            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_uid"));
            sm.setName(rs.getString("name"));
            sm.setPasscode(rs.getString("passcode"));
            sm.setEmail(rs.getString("email"));
            sm.setShowWorkRequired(rs.getInt("is_show_work_required") > 0);
            sm.setTutoringAvail(rs.getInt("is_tutoring_available") > 0);

            int groupId = rs.getInt("group_id");
            sm.setGroupId(String.valueOf(groupId));
            sm.setUserProgramId(rs.getInt("user_prog_id"));
            sm.setGroup(rs.getString("group_name"));
            sm.setProgramDescr(rs.getString("program"));
            sm.setProgId(rs.getString("prog_id"));
            sm.setSubjId(rs.getString("subj_id"));
            sm.setLastQuiz(rs.getString("last_quiz"));
            sm.setChapter(getChapter(rs.getString("test_config_json")));
            sm.setLastLogin(rs.getString("last_use_date"));
            sm.setTotalUsage(rs.getInt("usage_count"));
            String passPercent = rs.getString("pass_percent");
            sm.setPassPercent(passPercent);
            sm.setBackgroundStyle(rs.getString("gui_background_style"));
            int activeSegment = rs.getInt("active_segment");
            sm.setSectionNum(activeSegment);
            if (activeSegment > 0) {
                int segmentCount = rs.getInt("total_segments");

                /**
                 * If not set, then guess
                 * 
                 * @TODO: this should never happen
                 */
                if (segmentCount == 0)
                    segmentCount = 4;

                String status = new StringBuilder("Section ").append(activeSegment).append(" of ").append(segmentCount)
                        .toString();
                sm.setStatus(status);
            } else {
                sm.setStatus("Not started");
            }
            String tutoringState = (sm.getTutoringAvail()) ? "ON" : "OFF";
            sm.setTutoringState(tutoringState);
            String showWorkState = (sm.getShowWorkRequired()) ? "REQUIRED" : "OPTIONAL";
            sm.setShowWorkState(showWorkState);

            l.add(sm);
        }
        return l;
    }

    private List<StudentActivityModel> loadStudentActivity(Connection conn, ResultSet rs) throws Exception {

        List<StudentActivityModel> l = new ArrayList<StudentActivityModel>();
        int problemsViewed = 0;
        
        CmAdminDao dao = null;

        while (rs.next()) {
            StudentActivityModel m = new StudentActivityModel();
            m.setProgramDescr(rs.getString("program"));
            m.setUseDate(rs.getString("use_date"));
            m.setStart(rs.getString("start_time"));
            m.setStop(rs.getString("stop_time"));
            int sectionNum = rs.getInt("test_segment");
            String progId = rs.getString("prog_id");

            if (progId.equalsIgnoreCase("chap")) {
            	if (dao == null) dao = new CmAdminDao();
            	String subjId = rs.getString("subj_id");
                String chapter = getChapter(rs.getString("test_config_json"));
		        List <ChapterModel> cmList = dao.getChaptersForProgramSubject(conn, "Chap", subjId);
		        for (ChapterModel cm : cmList) {
		        	if (cm.getTitle().equals(chapter)) {
		        		m.setProgramDescr(new StringBuilder(m.getProgramDescr()).append(" ").append(cm.getNumber()).toString());
		        		break;
		        	}
		        }
            }

            int runId = rs.getInt("test_run_id");
            m.setRunId(runId);

            StringBuilder sb = new StringBuilder();
            sb.append(rs.getString("activity"));

            boolean isQuiz = (rs.getInt("is_quiz") > 0);
            m.setIsQuiz(isQuiz);
            if (isQuiz) {
                sb.append(sectionNum);
            }
            m.setActivity(sb.toString());

            // TODO: flag re-takes?
            sb.delete(0, sb.length());
            if (isQuiz) {
                int numCorrect = rs.getInt("answered_correct");
                int numIncorrect = rs.getInt("answered_incorrect");
                int notAnswered = rs.getInt("not_answered");
                double percent = (double) (numCorrect * 100) / (double) (numCorrect + numIncorrect + notAnswered);
                sb.append(java.lang.Math.round(percent)).append("% correct");
                problemsViewed = 0;
            } else {
                problemsViewed += rs.getInt("problems_viewed");
                // TODO: are there always 3 problems per session/lesson?
                int problemsPerLesson = 3;
                int completed = problemsViewed / problemsPerLesson;
                int inProgress = 0; // lessonsViewed % problemsPerLesson;
                int totalSessions = rs.getInt("total_sessions");

                if (completed >= 0) {
                    if (totalSessions < 1) {
                        sb.append("total of ").append(completed);
                        if (completed > 1)
                            sb.append(" reviews completed");
                        else
                            sb.append(" review completed");
                        if (inProgress != 0) {
                            sb.append(", 1 in progress");
                        }
                    } else {
                        sb.append(completed).append(" out of ");
                        sb.append(totalSessions).append(" reviewed");
                    }
                } else {
                    if (inProgress != 0) {
                        sb.append("1 review in progress");
                    }
                }
            }
            m.setResult(sb.toString());
            l.add(m);
        }

        fixReviewSectionNumbers(l);

        // reverse order of list
        List<StudentActivityModel> m = new ArrayList<StudentActivityModel>(l.size());
        for (int i = (l.size() - 1); i >= 0; i--) {
            m.add(l.get(i));
        }

        return m;
    }

    private void fixReviewSectionNumbers(List<StudentActivityModel> l) {
        Map<Integer, StudentActivityModel> h = new HashMap<Integer, StudentActivityModel>();
        for (StudentActivityModel m : l) {
            if (m.getIsQuiz()) {
                h.put(m.getRunId(), m);
            }
        }

        for (StudentActivityModel m : l) {
            if (!m.getIsQuiz()) {
                Integer runId = m.getRunId();
                StudentActivityModel q = h.get(runId);
                if (q != null) {
                    String[] t = q.getActivity().split("-");
                    if (t.length > 1) {
                        StringBuilder sb = new StringBuilder(m.getActivity());
                        sb.append(t[1]);
                        m.setActivity(sb.toString());
                    }
                }
            }
        }

    }

    private List<LessonItemModel> loadLessonItems(ResultSet rs) throws Exception {
    	List<LessonItemModel> l = new ArrayList<LessonItemModel>();
    	
		String testName = rs.getString(1);
		int testSegment = rs.getInt(2);

	    HaTestDefDescription tdDesc = HaTestDefDescription.getHaTestDefDescription(testName, testSegment);

		// identify incomplete topics
		Set <String> topicFileSet = new HashSet<String>();
		do {
			if (!"correct".equalsIgnoreCase(rs.getString("answer_status"))) {
				topicFileSet.add(rs.getString("file"));
			}
		} while (rs.next());
		
    	for (InmhItemData item : tdDesc.getLessonItems()) {
    		LessonItemModel mdl = new LessonItemModel();
    		mdl.setName(item.getInmhItem().getTitle());
    		String file = item.getInmhItem().getFile();
    		mdl.setFile(file);
    		mdl.setPrescribed((topicFileSet.contains(file))?"Prescribed":"");
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
            logger.error("*** Error extracting Chapter from JSON", e);
        }

        return chap;
    }

    /**
     * Return the currently configured user program for this user
     * 
     * Return null if no program has been defined or a defined program does not exist.
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel loadProgramInfo(final Connection conn, Integer userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "select c.id, c.user_id, c.pass_percent, u.admin_id, c.test_def_id, t.test_name, c.test_config_json " +
                     "from CM_USER_PROGRAM c " +
                     "JOIN HA_USER u on c.id = u.user_prog_id " +
                     "JOIN HA_TEST_DEF t on c.test_def_id = t.test_def_id " +
                     "and u.uid = ?";
        try {
            StudentUserProgramModel supm = new StudentUserProgramModel();

            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                supm.setId(rs.getInt("id"));
                supm.setUserId(rs.getInt("user_id"));
                supm.setAdminId(rs.getInt("admin_id"));
                supm.setPassPercent(rs.getInt("pass_percent"));
                supm.setTestDefId(rs.getInt("test_def_id"));
                supm.setTestName(rs.getString("test_name"));
                supm.setConfig(new HaTestConfig(rs.getString("test_config_json")));
            }
            return supm;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Load this user's curently active state information. This shows the
     * current test/run and session the user is currently viewing.
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentActiveInfo loadActiveInfo(final Connection conn, Integer userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "select active_run_id, active_test_id, active_segment, active_run_session from HA_USER where uid = ? ";
        try {
            StudentActiveInfo activeInfo = new StudentActiveInfo();

            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                activeInfo.setActiveRunId(rs.getInt("active_run_id"));
                activeInfo.setActiveRunSession(rs.getInt("active_run_session"));
                activeInfo.setActiveSegment(rs.getInt("active_segment"));
                activeInfo.setActiveTestId(rs.getInt("active_test_id"));
            }
            return activeInfo;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Set the active information for the named user
     * 
     * @TODO: Move to 1-to-1 table HA_USER_ACTIVE, or set is_active flag in HA_USER_PROGRAM
     * @TODO: move all active info to here (including the studentModel.getSection)
     * @param conn
     * @param userId
     * @param activeInfo
     * @throws Exception If record cannot be updated
     * 
     */
    public void setActiveInfo(final Connection conn, Integer userId, StudentActiveInfo activeInfo) throws Exception {
        PreparedStatement ps = null;

        String sql = "update HA_USER set active_run_id = ?, active_test_id = ?, active_segment = ?, active_run_session = ? where uid = ? ";
        try {
            ps = conn.prepareStatement(sql);

            ps.setInt(1, activeInfo.getActiveRunId());
            ps.setInt(2, activeInfo.getActiveTestId());
            ps.setInt(3, activeInfo.getActiveSegment());
            ps.setInt(4, activeInfo.getActiveRunSession());
            ps.setInt(5, userId);
            if (ps.executeUpdate() != 1)
                throw new Exception("Could not update active information for id: " + userId);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }
    

    /** Helper function to assign the named program/subject to the student identified by uid
     * 
     * @param uid
     * @param subId
     * @param progId
     * @param chapter
     * @throws Exception
     */
    public void assignProgramToStudent(final Connection conn, Integer uid, CmProgram program, String chapter) throws Exception {
        
        StudentModelI sm = getStudentModelBasic(conn, uid);
        sm.setProgId(program.getProgramId());
        sm.setSubjId(program.getSubject());
        sm.setProgramChanged(true);
        
        sm.setChapter(chapter);
        
        setTestConfig(conn, sm);
        
        updateStudent(conn, sm, true, false, true, false);        
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
            e.printStackTrace();
            throw new CmRpcException("Error adding test run item view: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }


}
