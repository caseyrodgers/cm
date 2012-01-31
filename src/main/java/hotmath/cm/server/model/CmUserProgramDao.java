package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * 
 * @author Bob
 * 
 */

public class CmUserProgramDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(CmUserProgramDao.class);

    static private CmUserProgramDao __instance;

    static public CmUserProgramDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (CmUserProgramDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(CmUserProgramDao.class.getName());
        }
        return __instance;
    }

    private CmUserProgramDao() {
        /** empty */
    }


    public void updateStudentGradeLevel(final int userProgId, final int gradeLevel) throws Exception {
        final String sql = CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_STUDENT_GRADE_LEVEL_PROGRAM_SQL");
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1,gradeLevel);
                ps.setInt(2,userProgId);
                return ps;
            }
        });
    }
    

    /**
     * Return the currently configured user program for this user
     * 
     * Return null if no program has been defined or a defined program does not
     * exist.
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel loadProgramInfoCurrent(final Integer userId) throws Exception {
        StudentUserProgramModel student = this.getJdbcTemplate().queryForObject(
                CmMultiLinePropertyReader.getInstance().getProperty("CURRENT_USER_PROGRAM_SQL"),
                new Object[] { userId }, new StudentUserProgramModelMapper(userId));

        HaTestDef td = HaTestDefDao.getInstance().getTestDef(student.getTestDefId());
        student.setTestDef(td);

        return student;
    }

    public StudentUserProgramModel loadProgramInfo(final Connection conn, final Integer userProgId) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("LOAD_USER_PROGRAM_SQL");

        StudentUserProgramModel student = this.getJdbcTemplate().queryForObject(sql, new Object[] { userProgId },
                new RowMapper<StudentUserProgramModel>() {
                    public StudentUserProgramModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentUserProgramModel supm = new StudentUserProgramModel();
                        try {
                            supm.setId(rs.getInt("id"));
                            supm.setUserId(rs.getInt("user_id"));
                            supm.setAdminId(rs.getInt("admin_id"));
                            supm.setTestDefId(rs.getInt("test_def_id"));
                            supm.setTestName(rs.getString("test_name"));
                            int passPercent = rs.getInt("pass_percent");
                            supm.setPassPercent(passPercent);
                            java.sql.Date dt = rs.getDate("create_date");
                            supm.setCreateDate(new Date(dt.getTime()));
                            supm.setConfig(new HaTestConfig(passPercent, rs.getString("test_config_json")));
                            supm.setCustomProgramId(rs.getInt("custom_program_id"));
                            supm.setCustomProgramName(rs.getString("custom_program_name"));
                            supm.setCustomQuizId(rs.getInt("custom_quiz_id"));
                            supm.setCustomQuizName(rs.getString("custom_quiz_name"));
                            supm.setComplete(rs.getDate("date_completed") != null);
                            return supm;
                        } catch (Exception e) {
                            __logger.error("Error creating StudentUserProgramModel: " + userProgId, e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });

        if (student.getTestDefId() > 0) {
            HaTestDef td = HaTestDefDao.getInstance().getTestDef(student.getTestDefId());
            student.setTestDef(td);
        }
        return student;
    }
    
    
    public int getCustomProgramGradeLevel(int custProgId) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_PROGRAM_GRADE_LEVEL");
        return this.getJdbcTemplate().queryForInt(
                sql,
                new Object[]{custProgId}
                );
    }

    /**
     * Mark this user program has being complete.
     * 
     * This is saved as a static value to allow for quick checks on the status
     * of a program without having dynamically check.
     * 
     * return true if program was successfully set to completed.
     * 
     * @param conn
     * @param userProgId
     * @throws Exception
     */
    public boolean setProgramAsComplete(final Connection conn, int userProgId, boolean trueOrFalse) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("USER_PROGRAM_SET_COMPLETE");
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, trueOrFalse ? new java.sql.Date(System.currentTimeMillis()) : null);
            ps.setInt(2, userProgId);

            int res = ps.executeUpdate();
            return res == 1;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Return all User Programs for the specified User
     * 
     * Return an empty List if no valid programs found
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public List<StudentUserProgramModel> loadProgramInfoAll(final Connection conn, Integer userId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("ALL_USER_PROGRAM_SQL");
        try {
            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            rs = ps.executeQuery();

            List<StudentUserProgramModel> list = new ArrayList<StudentUserProgramModel>();
            while (rs.next()) {
                StudentUserProgramModel supm = defineUserProgram(rs);
                HaTestDef td = HaTestDefDao.getInstance().getTestDef(supm.getTestDefId());
                supm.setTestDef(td);
                list.add(supm);
            }
            return list;
        } finally {
            SqlUtilities.releaseResources(rs, ps, null);
        }
    }

    /**
     * Return the program information for the specified test
     * 
     * @param testId
     * @return
     * @throws Exception
     */
    public StudentUserProgramModel loadProgramInfoForTest(Integer testId) throws Exception {
        
        StudentUserProgramModel sm =  getJdbcTemplate().queryForObject(
                CmMultiLinePropertyReader.getInstance().getProperty("LOAD_USER_PROGRAM_FOR_TEST_SQL"),
                new Object[] { testId },
                new StudentUserProgramRowMapper());
        
        return sm;
    }

    /**
     * Set the user pass_percent to named value
     * 
     * @param conn
     * @param programId
     * @param passPercent
     * @throws Exception
     */
    public void setProgramPassPercent(final Connection conn, Integer programId, Integer passPercent) throws Exception {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_PASS_PERCENT_SQL"));

            ps.setInt(1, passPercent);
            ps.setInt(2, programId);

            int cnt = ps.executeUpdate();
            if (cnt != 1)
                __logger.warn("no such program: " + programId);

        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    static class StudentUserProgramRowMapper implements RowMapper<StudentUserProgramModel> {
        @Override
        public StudentUserProgramModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                StudentUserProgramModel supm = new StudentUserProgramModel();

                supm.setId(rs.getInt("id"));
                supm.setUserId(rs.getInt("user_id"));
                supm.setAdminId(rs.getInt("admin_id"));
                supm.setTestDefId(rs.getInt("test_def_id"));
                supm.setTestName(rs.getString("test_name"));
                int passPercent = rs.getInt("pass_percent");
                supm.setPassPercent(passPercent);
                java.sql.Date dt = rs.getDate("create_date");
                supm.setCreateDate(new Date(dt.getTime()));
                supm.setConfig(new HaTestConfig(passPercent, rs.getString("test_config_json")));
                supm.setCustomProgramId(rs.getInt("custom_program_id"));
                supm.setCustomProgramName(rs.getString("custom_program_name"));
                supm.setCustomQuizId(rs.getInt("custom_quiz_id"));
                supm.setCustomQuizName(rs.getString("custom_quiz_name"));
                supm.setComplete(rs.getDate("date_completed") != null);

                return supm;
            } catch (Exception e) {
                __logger.error("Error loading problem info", e);
                throw new SQLException(e.getMessage(), e);
            }
        }
    }

    /**
     * Define a StudentUserProgramModel from ResultSet
     * 
     * @deprecated use the RowMapper
     * 
     * @param rs
     * @return
     * @throws Exception
     */
    private StudentUserProgramModel defineUserProgram(ResultSet rs) throws Exception {

        StudentUserProgramModel supm = new StudentUserProgramModel();

        supm.setId(rs.getInt("id"));
        supm.setUserId(rs.getInt("user_id"));
        supm.setAdminId(rs.getInt("admin_id"));
        supm.setTestDefId(rs.getInt("test_def_id"));
        supm.setTestName(rs.getString("test_name"));
        int passPercent = rs.getInt("pass_percent");
        supm.setPassPercent(passPercent);
        java.sql.Date dt = rs.getDate("create_date");
        supm.setCreateDate(new Date(dt.getTime()));
        supm.setConfig(new HaTestConfig(passPercent, rs.getString("test_config_json")));
        supm.setCustomProgramId(rs.getInt("custom_program_id"));
        supm.setCustomProgramName(rs.getString("custom_program_name"));
        supm.setCustomQuizId(rs.getInt("custom_quiz_id"));
        supm.setCustomQuizName(rs.getString("custom_quiz_name"));
        supm.setComplete(rs.getDate("date_completed") != null);

        return supm;
    }
    
    

    static class StudentUserProgramModelMapper implements RowMapper<StudentUserProgramModel> {

        int userId;

        public StudentUserProgramModelMapper(int userId) {
            this.userId = userId;
        }

        public StudentUserProgramModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudentUserProgramModel supm = new StudentUserProgramModel();
            try {
                supm.setId(rs.getInt("id"));
                supm.setUserId(rs.getInt("user_id"));
                supm.setAdminId(rs.getInt("admin_id"));
                supm.setTestDefId(rs.getInt("test_def_id"));
                supm.setTestName(rs.getString("test_name"));
                int passPercent = rs.getInt("pass_percent");
                supm.setPassPercent(passPercent);
                java.sql.Date dt = rs.getDate("create_date");
                supm.setCreateDate(new Date(dt.getTime()));
                supm.setConfig(new HaTestConfig(passPercent, rs.getString("test_config_json")));
                supm.setCustomProgramId(rs.getInt("custom_program_id"));
                supm.setCustomProgramName(rs.getString("custom_program_name"));
                supm.setCustomQuizId(rs.getInt("custom_quiz_id"));
                supm.setCustomQuizName(rs.getString("custom_quiz_name"));
                supm.setComplete(rs.getDate("date_completed") != null);
                return supm;
            } catch (Exception e) {
                __logger.error("Error creating StudentUserProgramModel: " + userId, e);
                throw new SQLException(e.getMessage());
            }
        }
    }    

}
