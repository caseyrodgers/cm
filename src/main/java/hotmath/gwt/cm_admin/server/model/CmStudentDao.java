package hotmath.gwt.cm_admin.server.model;

import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_LAST_LOGIN_KEY;
import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_LAST_QUIZ_KEY;
import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_PASSING_COUNT_KEY;
import static hotmath.gwt.cm_tools.client.model.StudentModelExt.HAS_TUTORING_USE_KEY;
import hotmath.assessment.InmhItemData;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelBase;
import hotmath.gwt.cm_tools.client.model.StudentModelBasic;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.model.UserProgramIsNotActiveException;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTestDefDescription;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
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
    
    public static final int GROUP_NONE_ID = 1;

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
        String studentSql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_SUMMARY");

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
    private String getStudentSummarySql(StudentSqlType sqlType, Boolean includeSelfRegistrationTemplates) {
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
            ps.setInt(2, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentSummaries(rs);
            
            loadChapterInfo(conn, l);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting student summaries for Admin uid: %d", adminUid), e);
            throw new Exception("*** Error getting student summary data ***");
        } finally {
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
            ps.setInt(2, (isActive) ? 1 : 0);
            rs = ps.executeQuery();

            l = loadStudentBaseSummaries(conn, rs, tutoringEnabledForAdmin);
            
            loadChapterInfo(conn, l);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting student base summaries for Admin uid: %d", adminUid), e);
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
    			
    	String sqlWithUids = sql.replaceFirst("XXX", uidStr);

    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		ps = conn.prepareStatement(sqlWithUids);
    		rs = ps.executeQuery();
    		l = loadStudentExtendedSummaries(rs);
    	} catch (Exception e) {
            logger.error(String.format("*** Error getting student extended summary data for uids: %s", uidStr), e);
			throw new Exception("*** Error getting student extended summary data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }

	private String getUidString(List<Integer> studentUids) {
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
            logger.error(String.format("*** Error getting student extended data for uids: %s, sql: ", uidStr, sqlWithUids), e);
			throw new Exception("*** Error getting student extended data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
    }
    
    /** Return a list UIDs that represent the students 
     *  that match the requested quick search 'search'.
     *  
     *  
     * @param conn
     * @param studentUids
     * @param search
     * @return
     * @throws Exception
     */
    public List<Integer> getQuickSearchUids(final Connection conn, Set<Integer> studentUids, String search) throws Exception {
    	
    	List<Integer> qsUids = new ArrayList<Integer>();
    	
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_SEARCH_EXTENDED");
    	String uidStr = getUidString(studentUids);
    	String sqlWithUids = sql.replaceFirst("XXX", uidStr);
    	String sqlWithSearch = sqlWithUids.replaceAll("YYY", search.toLowerCase());

    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		ps = conn.prepareStatement(sqlWithSearch);
    		rs = ps.executeQuery();
    		while (rs.next()) {
    			qsUids.add(rs.getInt(1));
    		}
    	} catch (Exception e) {
            logger.error(String.format("*** Error getting quick search student data for uids: %s, sql: ", uidStr, sqlWithUids), e);
			throw new Exception("*** Error getting student quick search data ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    	return qsUids;
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

		CmAdminDao dao = new CmAdminDao();
		
		for (StudentModelI sm : l) {
			String chapter = sm.getChapter();
			if (chapter != null) {
				String subjId = sm.getProgram().getSubjectId();
		        List <ChapterModel> cmList = dao.getChaptersForProgramSubject(conn, "Chap", subjId);
		        for (ChapterModel cm : cmList) {
		        	if (cm.getTitle().equals(chapter)) {
		        		sm.getProgram().setProgramDescription(new StringBuilder(sm.getProgram().getProgramDescription()).append(" ").append(cm.getNumber()).toString());
		        		break;
		        	}
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
	    AccountInfoModel aim = new CmAdminDao().getAccountInfo(conn, aid);
	    String hasTutoring = aim.getHasTutoring();
	    if(hasTutoring != null && hasTutoring.equals("Enabled")) {
	        return true;
	    }
	    else {
	        return false;
	    }
	}

	/** Return list of activity records for student detail
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
    public List<StudentActivityModel> getStudentActivity(final Connection conn, int uid) throws Exception {
        List<StudentActivityModel> l = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_ACTIVITY"));
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            ps.setInt(3, uid);            
            rs = ps.executeQuery();

            l = loadStudentActivity(conn, rs);
        } catch (Exception e) {
            logger.error(String.format("*** Error getting student details for student uid: %d", uid), e);
            throw new Exception("*** Error getting student details ***");
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
        return l;
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
    		logger.error(String.format("*** Error getting lesson items for runId:  %d", runId), e);
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
            throw new Exception("The passcode you entered is already in use, please try again.");
        }
        isDuplicate = checkForDuplicateName(conn, sm);
        if (isDuplicate) {
            throw new Exception("The name you entered is already in use, please try again.");
        }

        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("ADD_STUDENT_SQL"));
            ps.setString(1, sm.getName());
            ps.setString(2, sm.getPasscode());
            ps.setInt(3, 0);
            ps.setInt(4, Integer.parseInt(sm.getGroupId()));
            ps.setString(5, sm.getProgram().getProgramType());
            ps.setString(6, sm.getProgram().getSubjectId());
            ps.setInt(7, sm.getAdminUid());
            ps.setInt(8, (sm.getIsDemoUser() != null && sm.getIsDemoUser()) ? 1 : 0);

            int count = ps.executeUpdate();
            if (count == 1) {
                int stuUid = SqlUtilities.getLastInsertId(conn);
                sm.setUid(stuUid);
                addStudentProgram(conn,sm);
                updateStudent(conn,sm);
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
            Boolean progIsNew, Boolean passcodeChanged, Boolean passPercentChanged) throws Exception {
        if (passcodeChanged) {
            Boolean isDuplicate = checkForDuplicatePasscode(conn, sm);
            if (isDuplicate) {
                throw new Exception("The passcode you entered is already in use, please try again.");
            }
        }
        if (progIsNew)
            addStudentProgram(conn, sm);
        if (studentChanged)
            updateStudent(conn, sm);
        if (programChanged)
            updateStudentProgram(sm);

        if (passPercentChanged) {
        	String passPcnt = sm.getPassPercent();
        	Integer passPercent = (passPcnt != null) ? Integer.parseInt(passPcnt.substring(0, passPcnt.length() - 1)) : 0;
        	
            new CmUserProgramDao().setProgramPassPercent(conn, sm.getProgram().getProgramId(), passPercent);
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

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            logger.error(String.format("*** Error checking passcode for student with uid: %d", sm.getUid()), e);
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

            if (logger.isDebugEnabled()) {
            	logger.debug(String.format("+++ checkForDuplicateName(): query: %s", ps.toString()));
            }

            rs = ps.executeQuery();
            return (rs.next());
        } catch (Exception e) {
            logger.error(String.format("*** Error checking name for student with uid: %d", sm.getUid()), e);
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
        
        logger.info("+++ in updateStudent(): tutoringAvail: " + sm.getTutoringAvail() + ", showWorkReqd: " + sm.getShowWorkRequired());

        try {
            StudentProgramModel spm = sm.getProgram();
            
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_STUDENT_SQL"));
            ps.setString(1, sm.getName());
            ps.setString(2, sm.getPasscode());
            // ps.setString(3, sm.getEmail());
            
            // if group Id has not been set default to GROUP_NONE_ID
            ps.setInt(3, (sm.getGroupId() != null) ? Integer.parseInt(sm.getGroupId()):GROUP_NONE_ID);
            
            ps.setString(4, spm.getProgramType());
            ps.setString(5, spm.getSubjectId());
            ps.setInt(6, spm.getProgramId());
            ps.setInt(7, sm.getTutoringAvail() ? 1 : 0);
            ps.setInt(8, sm.getShowWorkRequired() ? 1 : 0);
            ps.setString(9, sm.getBackgroundStyle());
            ps.setInt(10, sm.getUid());
            int result = ps.executeUpdate();
        } catch (Exception e) {
            logger.error(String.format("*** Error updating student with uid: %d", sm.getUid()), e);
            throw new Exception(String.format("*** Error occurred while updating student: %s ***", sm.getName()));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
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
            if(cnt != 1)
                logger.warn("user not found to update: " + uid);
            
            /** update the current program information with new pass percentages
             * 
             */
            StudentModelI sm = getStudentModelBasic(conn, uid);
            new CmUserProgramDao().setProgramPassPercent(conn, sm.getProgram().getProgramId(),passPercent);

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
                ps.setInt(2, sm.getProgram().getProgramId());
            } else {
                ps = conn.prepareStatement(UPDATE_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL);
                ps.setInt(1, sm.getProgram().getProgramId());
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



    /**
     * 
     * @param conn
     * @param sm
     * @return
     * @throws Exception
     */
    public StudentModelI addStudentProgram(final Connection conn, StudentModelI sm) throws Exception {
        
        /** check for setting of StudyProgram, and implement override
         *  We should depreciate using instance vars to identify the program
         */
        if(sm.getProgram().isCustomProgram()) {
            return addStudentProgramCustom(conn, sm);
        }
        
        PreparedStatement ps = null;
        try {
            
            StudentProgramModel sp = sm.getProgram();
            
            setTestConfig(conn, sm);
            String pp = sm.getPassPercent();
            if (pp != null) {
                int passPcnt = Integer.parseInt(pp.substring(0, pp.indexOf("%")));
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, sp.getProgramType());
                ps.setString(4, sp.getSubjectId());
                ps.setInt(5, passPcnt);
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                ps.setString(7, sm.getJson());
                ps.setInt(8, sp.getCustomProgramId());
            } else {
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, sp.getProgramType());
                ps.setString(4, sp.getSubjectId());
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setString(6, sm.getJson());
                ps.setInt(7, sp.getCustomProgramId());
            }
            int result = ps.executeUpdate();
            if (result == 1) {
                // now get value of auto-increment id from CM_USER_PROGRAM
                int newKey = SqlUtilities.getLastInsertId(conn);
                sp.setProgramId(newKey);
           }
            
            sm.setSectionNum(0);
            setActiveInfo(conn, sm.getUid(), new StudentActiveInfo());
        } catch (Exception e) {
            String m = String.format("*** Error adding student program for student with uid: %d", sm.getUid());
            logger.error(m, e);
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
    public StudentModelI addStudentProgramCustom(final Connection conn, StudentModelI sm) throws Exception {

        StudentProgramModel studyProgram = sm.getProgram();
        if(studyProgram == null)
            throw new CmException("StudentProgramModel cannot be null: " + sm);

        PreparedStatement ps = null;
        try {
            setTestCustomConfig(conn, sm, studyProgram.getCustomProgramId());
            String pp = sm.getPassPercent();
            if (pp != null) {
                int passPcnt = Integer.parseInt(pp.substring(0, pp.indexOf("%")));
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, studyProgram.getProgramType());
                ps.setString(4, studyProgram.getSubjectId());
                ps.setInt(5, passPcnt);
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                ps.setString(7, sm.getJson());
                ps.setInt(8, sm.getProgram().getCustomProgramId());
            } else {
                ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_PROGRAM_NULL_PASS_PERCENT_SQL"));
                ps.setInt(1, sm.getUid());
                ps.setInt(2, sm.getAdminUid());
                ps.setString(3, studyProgram.getProgramType());
                ps.setString(4, studyProgram.getSubjectId());
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setString(6, sm.getJson());
                ps.setInt(7, sm.getProgram().getCustomProgramId());
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
            logger.error(m, e);
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
    private void setTestConfig(final Connection conn, StudentModelI sm) throws Exception {

        ResultSet rs = null;
        PreparedStatement ps2 = null;
        String sql = "select test_config_json from HA_TEST_DEF where subj_id = ? and prog_id = ? and is_active = 1";
        String json = "";
        try {
            ps2 = conn.prepareStatement(sql);
            ps2.setString(1, sm.getProgram().getSubjectId());
            String progId = sm.getProgram().getProgramType();
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
    
    private void setTestCustomConfig(final Connection conn, StudentModelI sm, Integer customProgramId) throws Exception {
        CustomProgramModel customProgram = new CmCustomProgramDao().getCustomProgram(conn, customProgramId);
        String testConfigJson = "{custom_program_id:" + customProgramId + "}";
        sm.setJson(testConfigJson);
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
            e.printStackTrace();
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
           students.add(getStudentModelBasic(conn, uid));
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
            logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
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
    public StudentModelI getStudentModel(final Connection conn, Integer uid, Boolean includeSelfRegTemplate) throws Exception {
        
        long timeStart = System.currentTimeMillis();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(getStudentSql(StudentSqlType.SINGLE_STUDENT, includeSelfRegTemplate));
            ps.setInt(1, uid);
            ps.setInt(2, 1);
            rs = ps.executeQuery();

            List<StudentModelI> l = null;
            l = loadStudentSummaries(rs);
            if (l.size() == 0)
                throw new Exception(String.format("Student with UID: %d was not found", uid));
            if (l.size() > 1)
                throw new Exception(String.format("Student with UID: %d matches more than one row", uid));

            loadChapterInfo(conn, l);
            
            StudentModelI sm = l.get(0);
            
            if(sm.getTutoringAvail()) {
                /** make sure the admin has tutoring enabled too
                 * 
                 */
                sm.setTutoringAvail(isTutoringEnabledForAdmin(conn, sm.getAdminUid()));
            }
            return sm; 
        } catch (Exception e) {
            logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
            logger.info(String.format("End getStudentModel(), UID: %d, elapsed seconds: %d", uid, ((System.currentTimeMillis() - timeStart)/1000)));
        }
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
                students.add( getStudentModel(rs.getInt(1)));
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
                students.add( getStudentModel(conn, rs.getInt(1), true));
            }
            return students;
        } finally {
            SqlUtilities.releaseResources(rs, null, null);
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
            sm.getProgram().setProgramId(rs.getInt("user_prog_id"));
            sm.setBackgroundStyle(rs.getString("gui_background_style"));
            sm.setShowWorkRequired(rs.getInt("is_show_work_required")==0?false:true);

            /** Check for tutoring available.
             * 
             * If the student has been enabled, make sure the admin has it 
             * enabled.  This is to allow for tighter messaging and not send
             * the user to LWL if the admin has not registered.
             */
            boolean isTutoringAvailable = rs.getInt("is_tutoring_available")==0?false:true;
            if(isTutoringAvailable) {
                isTutoringAvailable = isTutoringEnabledForAdmin(conn, sm.getAdminUid());
            }
            sm.setTutoringAvail(isTutoringAvailable);
            sm.setIsDemoUser(rs.getInt("is_demo")==0?false:true);

            return sm;
            
        } catch (Exception e) {
            logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
            logger.info(String.format("End getStudentModelBasic(), UID: %d, elapsed seconds: %d", uid, ((System.currentTimeMillis() - timeStart)/1000)));
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
    public StudentModelI getStudentModelBase(final Connection conn, Integer uid, Boolean includeSelfRegTemplate) throws Exception {
        
        long timeStart = System.currentTimeMillis();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(getStudentSummarySql(StudentSqlType.SINGLE_STUDENT, includeSelfRegTemplate));
            ps.setInt(1, uid);
            ps.setInt(2, 1);
            rs = ps.executeQuery();

            List<StudentModelI> l = null;
            l = loadStudentBaseSummaries(conn, rs, null);
            if (l.size() == 0)
                throw new Exception(String.format("Student with UID: %d was not found", uid));
            if (l.size() > 1)
                throw new Exception(String.format("Student with UID: %d matches more than one row", uid));

            loadChapterInfo(conn, l);
            
            StudentModelI sm = l.get(0);
            
            if(sm.getTutoringAvail()) {
                /**
                 * make sure the admin has tutoring enabled
                 */
                sm.setTutoringAvail(isTutoringEnabledForAdmin(conn, sm.getAdminUid()));
            }
            return sm; 
        } catch (Exception e) {
            logger.error(String.format("*** Error obtaining data for student UID: %d", uid), e);
            throw new Exception(String.format("*** Error obtaining data for student with UID: %d", uid));
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
            logger.info(String.format("End getStudentModel(), UID: %d, elapsed seconds: %d", uid, ((System.currentTimeMillis() - timeStart)/1000)));
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
            sm.setShowWorkRequired(rs.getInt("is_show_work_required") > 0);
            sm.setTutoringAvail(rs.getInt("is_tutoring_available") > 0);
            sm.setTutoringUse(rs.getInt("tutoring_use"));

            int groupId = rs.getInt("group_id");
            sm.setGroupId(String.valueOf(groupId));
            sm.setGroup(rs.getString("group_name"));

            sm.getProgram().setProgramDescription(rs.getString("program"));
            sm.getProgram().setProgramId(rs.getInt("user_prog_id"));
            sm.getProgram().setProgramType(rs.getString("prog_id"));
            sm.getProgram().setSubjectId(rs.getString("subj_id"));
            sm.getProgram().setCustomProgramId(rs.getInt("custom_program_id"));
            sm.getProgram().setCustomProgramName(rs.getString("custom_program_name"));

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
            sm.setStatus(getStatus(sm.getProgram().getProgramId(), sm.getSectionNum(), rs.getString("test_config_json")));

            String tutoringState = (sm.getTutoringAvail()) ? "ON" : "OFF";
            sm.setTutoringState(tutoringState);
            String showWorkState = (sm.getShowWorkRequired()) ? "REQUIRED" : "OPTIONAL";
            sm.setShowWorkState(showWorkState);

            l.add(sm);
        }
        return l;
    }

    private List<StudentModelI> loadStudentBaseSummaries(final Connection conn, ResultSet rs, Boolean tutoringEnabledForAdmin) throws Exception {

        List<StudentModelI> l = new ArrayList<StudentModelI>();

        while (rs.next()) {
            //StudentModelBaseI sm = new StudentModelBase();
            StudentModelI sm = new StudentModelBase();

            sm.setUid(rs.getInt("uid"));
            sm.setAdminUid(rs.getInt("admin_uid"));
            sm.setName(rs.getString("name"));
            sm.setPasscode(rs.getString("passcode"));
            sm.setEmail(rs.getString("email"));
            sm.setShowWorkRequired(rs.getInt("is_show_work_required") > 0);
            sm.setTutoringAvail(rs.getInt("is_tutoring_available") > 0 && ((tutoringEnabledForAdmin == null) || tutoringEnabledForAdmin));
            sm.setBackgroundStyle(rs.getString("gui_background_style"));

            sm.setGroupId(String.valueOf(rs.getInt("group_id")));
            sm.setGroup(rs.getString("group_name"));

            sm.setSectionNum(rs.getInt("active_segment"));
            sm.setChapter(getChapter(rs.getString("test_config_json")));
            sm.setJson(rs.getString("test_config_json"));
            sm.setSectionNum(rs.getInt("active_segment"));
            sm.setPassPercent(rs.getInt("pass_percent") + "%");

            StudentProgramModel program = sm.getProgram();
            program.setProgramId(rs.getInt("user_prog_id"));
            program.setSubjectId(rs.getString("subj_id"));
            program.setProgramType(rs.getString("prog_id"));
            program.setCustomProgramId(rs.getInt("custom_program_id"));
            program.setCustomProgramName(rs.getString("custom_program_name"));
            
            sm.setProgram(program);

            if (program.isCustomProgram() == false) {
                program.setProgramDescription(rs.getString("program"));
                sm.setStatus(getStatus(program.getProgramId(), sm.getSectionNum(), rs.getString("test_config_json")));
            }
            else {
                program.setProgramDescription("CP: " + program.getCustomProgramName());
                sm.setStatus(getCustomProgramStatus(conn, program.getCustomProgramId(), sm.getUid()));            	
            }

            l.add(sm);
        }
        return l;
    }

    private String getStatus(Integer userProgId, Integer activeSegment, String testConfigJson) {
        if (activeSegment > 0) {
            if (testConfigJson != null) {
            	try {
                    JSONObject jo = new JSONObject(testConfigJson);
                    StringBuilder sb = new StringBuilder("Section ").append(activeSegment).append(" of ").append(jo.getInt("segments"));
                    return sb.toString();
                }
                catch(Exception e) {
                    logger.error(String.format("*** Error getting status for user_prog_id: %d, test_config_json: %s", userProgId, testConfigJson), e);
                }
            }
        }
        return "Not started";
    }

	static final String CUSTOM_PROG_STATUS_SQL =
		"select rl.lesson_viewed, rl.date_completed " +
        " from HA_USER h " +
        " LEFT JOIN CM_USER_PROGRAM p       on p.id = h.user_prog_id " +
        " LEFT JOIN HA_TEST t               on t.user_prog_id = p.id " +
        " LEFT JOIN HA_TEST_RUN r           on r.test_id = t.test_id " +
        " LEFT JOIN HA_TEST_RUN_LESSON rl   on rl.run_id = r.run_id " +
        " where h.uid = ?";

    private String getCustomProgramStatus(final Connection conn, Integer customProgId, Integer uid) {
    	
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(CUSTOM_PROG_STATUS_SQL);
            ps.setInt(1, uid);
            rs = ps.executeQuery();

            int viewedCount = 0;
            int completedCount = 0;
            int lessonCount = 0;
            while (rs.next()) {
            	if (rs.getDate("lesson_viewed") != null) viewedCount++;
            	if (rs.getDate("date_completed") != null) completedCount++;
            	lessonCount++;
            }
            if (viewedCount > 0) {
                if (completedCount < lessonCount) {
                    StringBuilder sb = new StringBuilder();
                	return sb.append("Lesson ").append(viewedCount).append(" of " ).append(lessonCount).toString();
                }
                return "Completed";
            }
        }
        catch(Exception e) {
            logger.error(String.format("*** Error getting status for uid: %d, custom_prog_id: %d", uid, customProgId), e);
        }
        finally {
        	SqlUtilities.releaseResources(rs, ps, null);
        }
        return "Not started";
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
            
            logger.info("+++ passing count: " + sm.getPassingCount() + ", not passing count: " + sm.getNotPassingCount());

            l.add(sm);
        }
        return l;
    }
    
    private List<StudentActivityModel> loadStudentActivity(final Connection conn, ResultSet rs) throws Exception {

        List<StudentActivityModel> l = new ArrayList<StudentActivityModel>();
        CmAdminDao cmaDao = new CmAdminDao();
        HaTestRunDao trDao = new HaTestRunDao();

        while (rs.next()) {
            StudentActivityModel m = new StudentActivityModel();
            m.setProgramDescr(rs.getString("program"));
            m.setUseDate(rs.getString("use_date"));
            m.setStart(rs.getString("start_time"));
            m.setStop(rs.getString("stop_time"));
            m.setTestId(rs.getInt("test_id"));
            int sectionNum = rs.getInt("test_segment");
            String progId = rs.getString("prog_id");

            if (progId.equalsIgnoreCase("chap")) {
            	String subjId = rs.getString("subj_id");
                String chapter = getChapter(rs.getString("test_config_json"));
		        List <ChapterModel> cmList = cmaDao.getChaptersForProgramSubject(conn, "Chap", subjId);
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
                if ((numCorrect + numIncorrect + notAnswered) > 0) {
                    double percent = (double) (numCorrect * 100) / (double) (numCorrect + numIncorrect + notAnswered);
                    sb.append(Math.round(percent)).append("% correct");
                }
                else {
                	sb.append("Started");
                }
            } else {
                // was: problemsViewed / problemsPerLesson; // where problemsPerLesson assumed to be always equal to 3
                int completed =  trDao.getLessonsViewedCount(conn, runId);
                
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
	    HaTestRun testRun = new HaTestRunDao().lookupTestRun(conn, runId);	        
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
            logger.error("*** Error extracting Chapter from JSON", e);
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
    			return getStudentModel(conn, uid, true); 
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
    public StudentActiveInfo loadActiveInfo(final Connection conn, Integer userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "select active_run_id, active_test_id, active_segment, active_segment_slot, active_run_session from HA_USER where uid = ? ";
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
                activeInfo.setActiveSegmentSlot(rs.getInt("active_segment_slot"));
            }

            /** Check to see if this program supports alternate tests, if so
             *  pass along the segment_slot to use .. otherwise, make sure it 
             *  is zero to always use the same slot
             */
            if(activeInfo.getActiveSegmentSlot() > 0) {
            	CmUserProgramDao upDao = new CmUserProgramDao();
                if(! upDao.loadProgramInfoCurrent(conn, userId).hasAlternateTests())
                   activeInfo.setActiveSegmentSlot(0);
            }
            return activeInfo;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /** How many slots of alternate questions have been setup
     * 
     */
    static public int MAX_QUIZ_SEGMENT_SLOTS=2;
    
    
    
    /** Move this user to the next quiz slot.
     * 
     *  A quiz slot acts like a circular data structure, rolling over back to zero.
     * @param conn
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentActiveInfo moveToNextQuizSegmentSlot(final Connection conn, Integer userId) throws Exception {
        StudentActiveInfo activeInfo = loadActiveInfo(conn, userId);
        
        int segmentSlot = activeInfo.getActiveSegmentSlot();
        segmentSlot++;
        
        if(segmentSlot > (MAX_QUIZ_SEGMENT_SLOTS-1))
            segmentSlot = 0; // roll over, back to zero
        
        activeInfo.setActiveSegmentSlot(segmentSlot);
        setActiveInfo(conn, userId, activeInfo);
        
        return activeInfo;
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

        String sql = "update HA_USER set active_run_id = ?, active_test_id = ?, active_segment = ?, active_segment_slot = ?, active_run_session = ? where uid = ? ";
        try {
            ps = conn.prepareStatement(sql);

            ps.setInt(1, activeInfo.getActiveRunId());
            ps.setInt(2, activeInfo.getActiveTestId());
            ps.setInt(3, activeInfo.getActiveSegment());
            ps.setInt(4, activeInfo.getActiveSegmentSlot());
            ps.setInt(5, activeInfo.getActiveRunSession());
            ps.setInt(6, userId);
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
        assignProgramToStudent(conn, uid, program.getProgramType(),program.getSubject(), chapter, null);
    }
    
    
    public void assignProgramToStudent(final Connection conn, Integer uid, String programType, String subjId, String chapter, String passPercent) throws Exception {
        
        StudentModelI sm = getStudentModelBasic(conn, uid);
        
        sm.getProgram().setProgramType(programType);
        sm.getProgram().setSubjectId(subjId);
        sm.setProgramChanged(true);
        sm.setChapter(chapter);
        sm.setPassPercent(passPercent);
        
        setTestConfig(conn, sm);
        updateStudent(conn, sm, true, false, true, false, false);        
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
    
    public void setBackgroundStyle(final Connection conn, int uid, String style) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "update HA_USER set gui_background_style = ? where uid = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, style);
            ps.setInt(2, uid);
            int c = ps.executeUpdate();
            if(c != 1)
                logger.warn("Could not update background style for user: " + uid);
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
    public void verifyActiveProgram(final Connection conn, int testId) throws Exception {
        
        if(testId == 0)
            return ;
        
        PreparedStatement ps=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("VERIFY_IS_ACTIVE_PROGRAM");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
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
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
}
