package hotmath.cm.dao;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.spring.SpringManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class CCSSReportDao extends SimpleJdbcDaoSupport {

    private static final Logger LOGGER = Logger.getLogger(CCSSReportDao.class);

	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private static CCSSReportDao __instance;

    static public CCSSReportDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (CCSSReportDao) SpringManager.getInstance().getBeanFactory().getBean("CCSSReportDao");
        }
        return __instance;
    }

    private CCSSReportDao() {
        /** empty */
    }

    /**
     * Get CCSS standard names for student's quizzes
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<String> getStudentQuizStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CORRECT_QUIZ_CCSS_NAMES_FOR_STUDENT");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student's review work
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<String> getStudentReviewStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_REVIEW_CCSS_NAMES_FOR_STUDENT");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student's assignments
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<String> getStudentAssignmentStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_CCSS_NAMES_FOR_STUDENT");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    private List<String> getStudentStandardNames(int userId, Date fromDate, Date toDate, String sql) {
    	List<String> standardNames = null;
    	try {
    		standardNames = getJdbcTemplate().query(sql, new Object[] { userId, fromDate, toDate }, new RowMapper<String>() {
    			@Override
    			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return rs.getString("standard_name_new");
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStudentStandardNames(): userId: %d, fromDate: %s, toDate: %s, sql: %s",
    				userId, DATE_FMT.format(fromDate), DATE_FMT.format(toDate), sql), e);
    		throw e;
    	}
    	return standardNames;
    }

}
