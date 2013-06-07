package hotmath.cm.dao;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
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
    public List<CCSSCoverageData> getStudentQuizStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CORRECT_QUIZ_CCSS_NAMES_FOR_STUDENT") +
        		CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY");
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
    public List<CCSSCoverageData> getStudentReviewStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_REVIEW_CCSS_NAMES_FOR_STUDENT") +
        		CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY");
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
    public List<CCSSCoverageData> getStudentAssignmentStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_CCSS_NAMES_FOR_STUDENT") +
        		CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student's quizzes, review work, and assignments
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<CCSSCoverageData> getStudentCombinedStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("GET_CORRECT_QUIZ_CCSS_NAMES_FOR_STUDENT")).append(" UNION ");
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("GET_REVIEW_CCSS_NAMES_FOR_STUDENT")).append(" UNION ");
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_CCSS_NAMES_FOR_STUDENT"));
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY"));
        return getStudentCombinedStandardNames(userId, fromDate, toDate, sb.toString());
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
    private List<CCSSCoverageData> getStudentStandardNames(int userId, Date fromDate, Date toDate, String sql) {
    	List<CCSSCoverageData> standardNames = null;
    	try {
    		standardNames = getJdbcTemplate().query(sql, new Object[] { userId, fromDate, toDate }, new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
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

    /**
     * Get CCSS standard names for student
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    private List<CCSSCoverageData> getStudentCombinedStandardNames(int userId, Date fromDate, Date toDate, String sql) {
    	List<CCSSCoverageData> standardNames = null;
    	try {
    		standardNames = getJdbcTemplate().query(sql, new Object[] { userId, fromDate, toDate, userId, fromDate, toDate, userId, fromDate, toDate }, new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
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
