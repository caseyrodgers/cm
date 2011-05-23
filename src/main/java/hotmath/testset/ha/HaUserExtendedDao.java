package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Defines data access methods for HA_USER_EXTENDED
 * 
 * @author Bob
 *
 */

public class HaUserExtendedDao extends SimpleJdbcDaoSupport {
	
    private static final Logger LOGGER = Logger.getLogger(HaTest.class);
    
    
    static private HaUserExtendedDao __instance;
    static public HaUserExtendedDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (HaUserExtendedDao)SpringManager.getInstance().getBeanFactory().getBean(HaUserExtendedDao.class.getName());
        }
        return __instance;
    }
    
    
    private HaUserExtendedDao() { ; }

    static public void updateUserExtended(Connection conn, Integer userId, HaTestRun testRun) throws Exception {
    	
    	/*
    	 * if row exists update it, otherwise insert new row
    	 */
    	String sql = "select quiz_pass_count, quiz_not_pass_count from HA_USER_EXTENDED where user_id = ?";
    	
    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;

    	/** protect against divide by zero errors */
    	int total = testRun.getAnsweredCorrect() + testRun.getAnsweredIncorrect() + testRun.getNotAnswered();
		int lastQuiz = (total != 0) ? (testRun.getAnsweredCorrect() * 100) / total : 0;

		try {
    		stmt = conn.prepareStatement(sql);
    		stmt.setInt(1, userId);
    		rs = stmt.executeQuery();
    		if (rs.next()) {
    			int passCount = rs.getInt(1) + ((testRun.isPassing()) ? 1 : 0);
    			int notPassCount = rs.getInt(2) + ((testRun.isPassing()) ? 0 : 1);
    					
    			// perform update
    			sql = "update HA_USER_EXTENDED set quiz_pass_count = ?, quiz_not_pass_count = ?, last_quiz = ? where user_id = ?";
    			stmt2 = conn.prepareStatement(sql);
    			stmt2.setInt(1, passCount);
    			stmt2.setInt(2, notPassCount);
    			stmt2.setInt(3, lastQuiz);
    			stmt2.setInt(4, userId);
    			
    			stmt2.executeUpdate();
    			
    		}
    		else {
    			// perform insert
    			insertUserExtended(conn, userId, testRun.isPassing(), lastQuiz);
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("*** Error updating User extended data, userId: " + userId, e);
    		throw new HotMathException("Error updating User extended data");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
    		SqlUtilities.releaseResources(null, stmt2, null);
    	}
    }

    static final String SELECT_USER_EXTENDED_SQL = 
        "select x.user_id, ux.user_id " +
        "from (select t.user_id from HA_TEST t, HA_TEST_RUN tr " +
        "      where t.test_id = tr.test_id and tr.run_id = ?) x " +
        "left outer join HA_USER_EXTENDED ux on ux.user_id = x.user_id";

    static final String SELECT_USER_ID_SQL = 
        "select x.user_id " +
        "from (select t.user_id from HA_TEST t, HA_TEST_RUN tr " +
        "      where t.test_id = tr.test_id and tr.run_id = ?) x";

    static public int runIdToUid(final Connection conn, Integer runId) throws Exception {
    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	int userId = 0;

		try {
    		stmt = conn.prepareStatement(SELECT_USER_ID_SQL);
    		stmt.setInt(1, runId);
    		rs = stmt.executeQuery();
    		if (rs.next()) {
    			userId = rs.getInt(1);
    		}
		}
		finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
		}
		return userId;
    }
    			
    static public void updateUserExtendedLessonStatus(final Connection conn, HaTestRun testRun, int lessonCount) throws Exception {
        updateUserExtendedLessonStatus(conn, testRun.getRunId(), lessonCount);
    }

    /**
     * @param conn
     * @param runId - the run ID (HA_TEST_RUN)
     * @param lessonCount - the lesson count (total) to set for specified run ID
     * @throws Exception
     */
    static public void updateUserExtendedLessonStatus(final Connection conn, Integer runId, int lessonCount) throws Exception {

    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;
    	int userId = 0;

		try {
    		stmt = conn.prepareStatement(SELECT_USER_EXTENDED_SQL);
    		stmt.setInt(1, runId);
    		rs = stmt.executeQuery();
    		if (rs.next()) {
    			
    			userId = rs.getInt(1);
    			int uxUserId = rs.getInt(2);
    			
    			if (userId == uxUserId) {
    				// HA_USER_EXTENDED record exists, update
        			String sql = "update HA_USER_EXTENDED set lesson_count = ?, current_lesson = 0, lessons_completed = 0 where user_id = ?";
        			stmt2 = conn.prepareStatement(sql);
        			stmt2.setInt(1, lessonCount);
        			stmt2.setInt(2, userId);
        			stmt2.executeUpdate();
    			}
        		else {
        			// update for userId (shouldn't happen)
    	    		LOGGER.warn("*** updateUserExtendedLessonStatus(): runId match not found, updating User extended lesson status data by userId, userId: " + userId);
    	    		if (userId != 0)
        	    		updateUserExtendedLessonStatusForUid(conn, userId, lessonCount);
    		    }
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("*** Error updating User extended data, userId: " + userId, e);
    		throw new HotMathException("Error updating User extended data");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
    		SqlUtilities.releaseResources(null, stmt2, null);
    	}
    	
    }

    static final String SELECT_USER_EXTENDED_BY_UID_SQL = 
        "select * from HA_USER_EXTENDED where user_id = ?";
        
    public void resyncUserExtendedLessonStatusForUid(int userId) {
        try {
        	List<NameValue> nvPairs = getJdbcTemplate().query(
        			CmMultiLinePropertyReader.getInstance().getProperty("GET_USER_EXTENDED_RESET"),
        			new Object[]{userId, userId, userId, userId, userId, userId,userId},
        			new RowMapper<NameValue>() {
        				@Override
        				public NameValue mapRow(ResultSet rs, int RowNum) throws SQLException {
        				    return new NameValue(rs.getString(1), rs.getInt(2));	
        				}
        			});
        	
        	Map<String, Integer> nvMap = new HashMap<String,Integer>();
        	for (NameValue nv : nvPairs) {
        		nvMap.put(nv.getName(), nv.getValue());
        	}
        			
        	Integer passQuizzes = nvMap.get("PASS_COUNT");
        	Integer notPassQuizzes = nvMap.get("NOT_PASS_COUNT");
        	Integer lastQuiz = nvMap.get("LAST_QUIZ");
        	Integer currentLesson = nvMap.get("CURRENT_LESSON");
        	Integer lessonCount = nvMap.get("LESSON_COUNT");
        	Integer lessonsCompleted = nvMap.get("LESSONS_COMPLETED");
        	Integer isLessonsComplete = (lessonsCompleted > 0 && lessonsCompleted == lessonCount) ? 1 : 0;

            Date lastLogin = getJdbcTemplate().queryForObject(
                    "select max(create_time) from HA_TEST where user_id = ?",
                    new Object[]{userId},
                    new RowMapper<Date>() {
                        @Override
                        public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getDate(1);
                        }
                     });        
            
            int count = getJdbcTemplate().update(
                    CmMultiLinePropertyReader.getInstance().getProperty("SET_UPDATE_EXTENDED_DATA"),
                    new Object[]{passQuizzes,notPassQuizzes,lastQuiz,lastLogin,currentLesson,lessonCount,isLessonsComplete,userId}
            );
            
            if (LOGGER.isDebugEnabled()) LOGGER.debug("Updated " + count);
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class NameValue {
		private String name;
		private int    value;
    	
    	NameValue(String name, int value) {
    		this.setName(name);
    		this.setValue(value);
    	}

		void setName(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

		void setValue(int value) {
			this.value = value;
		}

		int getValue() {
			return value;
		}
    }

    static public void resetUserExtendedLessonStatusForUid(final Connection conn, StudentProgramModel program, int userId) throws Exception {
    	if (program.getCustom().isCustom()) {
    		CmCustomProgramDao dao = CmCustomProgramDao.getInstance();
    		int programSegment = 1;
    		CmList<CustomLessonModel> list = dao.getCustomProgramLessons(conn, program.getCustom().getCustomProgramId(),programSegment);
    	    updateUserExtendedLessonStatusForUid(conn, userId, (list!=null)?list.size():0);    		
    	}
    	else {
    	    updateUserExtendedLessonStatusForUid(conn, userId, 0);
    	}
    }

    public void resetUserExtendedLessonStatusForUid(final Connection conn, int userId) throws Exception {
    	updateUserExtendedLessonStatusForUid(conn, userId, 0);
    }

    static public void updateUserExtendedLessonCompleted(final Connection conn, int runId, int lessonNumber) throws Exception {
    	int uid = runIdToUid(conn, runId);
    	updateUserExtendedLessonsCompletedForUid(conn, uid, lessonNumber);
    }
 
    static public void updateUserExtendedLessonStatusForUid(final Connection conn, int userId, int lessonCount) throws Exception {

    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;

		try {
    		stmt = conn.prepareStatement(SELECT_USER_EXTENDED_BY_UID_SQL);
    		stmt.setInt(1, userId);
    		rs = stmt.executeQuery();
    		if (rs.next()) {
    			String sql = "update HA_USER_EXTENDED set lesson_count = ?, current_lesson = 0, lessons_completed = 0 where user_id = ?";
    			stmt2 = conn.prepareStatement(sql);
    			stmt2.setInt(1, lessonCount);
    			stmt2.setInt(2, userId);
    			stmt2.executeUpdate();
    		}
        	else {
    	    	LOGGER.warn("*** updateUserExtendedLessonStatus(): Inserting User extended lesson status data, userId: " + userId);
        		// perform insert (shouldn't happen)
        	    insertUserExtendedLessonStatus(conn, userId, lessonCount);
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("*** Error updating User extended data, userId: " + userId, e);
    		throw new HotMathException("Error updating User extended data");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
    		SqlUtilities.releaseResources(null, stmt2, null);
    	}
    	
    }
    
    static public void updateUserExtendedLessonsCompletedForUid(final Connection conn, int userId, int lessonNumber) throws Exception {

    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;

		try {
    		stmt = conn.prepareStatement(SELECT_USER_EXTENDED_BY_UID_SQL);
    		stmt.setInt(1, userId);
    		rs = stmt.executeQuery();
    		
    		if (rs.next()) {
        		int lessonCount = rs.getInt("lesson_count");

        		// don't set completed status if lessonCount > (lessonNumber+1) {lessonNumber 0 is 1st lesson}
    	    	if (lessonCount > (lessonNumber+1)) return;

    			String sql = "update HA_USER_EXTENDED set lessons_completed = 1 where user_id = ?";
    			stmt2 = conn.prepareStatement(sql);
    			stmt2.setInt(1, userId);
    			stmt2.executeUpdate();
    		}
        	else {
    	    	LOGGER.warn("*** updateUserExtendedLessonsCompletedForUid(): Inserting User extended lesson completed data, userId: " + userId);
        		// perform insert (shouldn't happen)
        	    insertUserExtendedLessonCompleted(conn, userId);
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("*** Error updating User extended data, userId: " + userId, e);
    		throw new HotMathException("Error updating User extended data");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
    		SqlUtilities.releaseResources(null, stmt2, null);
    	}
    	
    }

    static public void updateUserExtendedCurrentLesson(final Connection conn, Integer runId, int currentLesson) throws Exception {

    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;
    	int userId = 0;

		try {
    		stmt = conn.prepareStatement(SELECT_USER_EXTENDED_SQL);
    		stmt.setInt(1, runId);
    		rs = stmt.executeQuery();
    		if (rs.next()) {
    			
    			userId = rs.getInt(1);
    			int uxUserId = rs.getInt(2);
    			
    			if (userId == uxUserId) {
    				// HA_USER_EXTENDED record exists, update
        			String sql = "update HA_USER_EXTENDED set current_lesson = ? where user_id = ?";
        			stmt2 = conn.prepareStatement(sql);
        			stmt2.setInt(1, currentLesson);
        			stmt2.setInt(2, userId);
        			stmt2.executeUpdate();
    			}
        		else {
    	    		LOGGER.warn("*** updateUserExtendedCurrentLesson(): Inserting User extended lesson status data, userId: " + userId);
        			// perform insert (shouldn't happen)
    	    		if (userId != 0) {
        	    		insertUserExtendedLessonStatusPlus(conn, runId);
        	    		updateUserExtendedCurrentLesson(conn, runId, currentLesson);
    	    		}
    		    }
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("*** Error updating User extended data, userId: " + userId, e);
    		throw new HotMathException("Error updating User extended data");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
    		SqlUtilities.releaseResources(null, stmt2, null);
    	}
    	
    }

    static public void updateUserExtendedLastLogin(Connection conn, Integer userId) throws Exception {
    	
    	/*
    	 * if row exists update it, otherwise insert new row
    	 */
    	String sql = "select * from HA_USER_EXTENDED where user_id = ?";
    	
    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;

		try {
    		stmt = conn.prepareStatement(sql);
    		stmt.setInt(1, userId);
    		rs = stmt.executeQuery();
    		if (rs.next()) {
    					
    			// perform update
    			sql = "update HA_USER_EXTENDED set last_login = (select max(login_time) from HA_USER_LOGIN where user_id = ?) where user_id = ?";
    			stmt2 = conn.prepareStatement(sql);
    			stmt2.setInt(1, userId);
    			stmt2.setInt(2, userId);
    			
    			stmt2.executeUpdate();
    			
    		}
    		else {
    			// perform insert
    			insertUserExtendedLastLogin(conn, userId);
    		}
    	}
    	catch (Exception e) {
    		LOGGER.error("*** Error updating User extended data, userId: " + userId, e);
    		throw new HotMathException("Error updating User extended data");
    	}
    	finally {
    		SqlUtilities.releaseResources(rs, stmt, null);
    		SqlUtilities.releaseResources(null, stmt2, null);
    	}
    	
    }

    static public void insertUserExtended(Connection conn, Integer userId, boolean isPassing, int passPercent) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_EXTENDED");
        
        PreparedStatement stmt = null;
        try {
        	stmt = conn.prepareStatement(sql);
        	stmt.setInt(1, userId);
        	stmt.setInt(2, isPassing?1:0);
        	stmt.setInt(3, isPassing?0:1);
            stmt.setInt(4, passPercent);
        	stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
        finally {
        	SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    static public void insertUserExtendedLastLogin(Connection conn, Integer userId) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_EXTENDED_LOGIN");
        
        PreparedStatement stmt = null;
        try {
        	stmt = conn.prepareStatement(sql);
        	stmt.setInt(1, userId);
        	stmt.setInt(2, userId);        	
        	stmt.executeUpdate();
        }
        finally {
        	SqlUtilities.releaseResources(null, stmt, null);
        }
    }
    
    static public void insertUserExtendedLessonStatus(Connection conn, Integer userId, int lessonCount) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_EXTENDED_LESSON_STATUS");
        
        PreparedStatement stmt = null;
        try {
        	stmt = conn.prepareStatement(sql);
        	stmt.setInt(1, userId);
        	stmt.setInt(2, lessonCount);
        	stmt.setInt(3, userId);
        	stmt.executeUpdate();
        }
        finally {
        	SqlUtilities.releaseResources(null, stmt, null);
        }
    }

    static public void insertUserExtendedLessonStatusPlus(Connection conn, Integer userId) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_EXTENDED_LESSON_STATUS_PLUS");

        LOGGER.warn("*** insertUserExtendedLessonStatusPlus() for userId: " + userId);
        PreparedStatement stmt = null;
        try {
        	stmt = conn.prepareStatement(sql);
        	stmt.setInt(1, userId);
        	stmt.setInt(2, userId);
        	stmt.setInt(3, userId);
        	stmt.executeUpdate();
        }
        finally {
        	SqlUtilities.releaseResources(null, stmt, null);
        }
    }


    static public void insertUserExtendedLessonCompleted(Connection conn, Integer userId) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_STUDENT_EXTENDED_LESSON_COMPLETED");

        LOGGER.warn("*** insertUserExtendedLessonCompleted() for userId: " + userId);
        PreparedStatement stmt = null;
        try {
        	stmt = conn.prepareStatement(sql);
        	stmt.setInt(1, userId);
        	stmt.setInt(2, userId);
        	stmt.executeUpdate();
        }
        finally {
        	SqlUtilities.releaseResources(null, stmt, null);
        }
    }

}
