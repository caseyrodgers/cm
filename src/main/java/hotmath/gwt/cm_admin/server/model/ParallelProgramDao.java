package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgramAssign;
import hotmath.gwt.cm_rpc.client.model.CmProgramInfo;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * <codeParallelProgramDao</code> supports Parallel Programs
 * 
 * @author bob
 *
 */

public class ParallelProgramDao extends SimpleJdbcDaoSupport {

	private static final Logger LOGGER = Logger.getLogger(ParallelProgramDao.class);
	
    static private ParallelProgramDao __instance;

    static public ParallelProgramDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (ParallelProgramDao)SpringManager.getInstance().getBeanFactory().getBean("parallelProgramDao");
        }
        return __instance;
    }
	
    public boolean isParallelProgramStudent(final Integer parallelProgId, final String studentPassword) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_STUDENT");
        boolean isParallelProgramStudent = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId, studentPassword},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isParallelProgramStudent;
                        try {
                            isParallelProgramStudent = (rs.getInt("is_parallel_prog_student") > 0);

                            return isParallelProgramStudent;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Parallel Program, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isParallelProgramStudent;
    }

    public boolean isParallelProgramAssignedToStudent(final Integer parallelProgId, final String studentPassword) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_ASSIGNED");
        boolean isAssigned = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{studentPassword, parallelProgId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isAssigned;
                        try {
                            isAssigned = (rs.getInt("is_assigned") > 0);

                            return isAssigned;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Parallel Program assignment, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isAssigned;
    }

    public boolean parallelProgramPrevAssignedToStudent(final Integer parallelProgId, final String studentPassword) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("PROGRAM_PREV_ASSIGNED");
        boolean prevAssigned = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{studentPassword, parallelProgId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean prevAssigned;
                        try {
                            prevAssigned = (rs.getInt("prev_assigned") > 0);

                            return prevAssigned;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Previous Program assignment, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return prevAssigned;
    }

    public int getStudentUserId(final Integer parallelProgId, final String studentPassword) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENT_UID");
        int studentUserId = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId, studentPassword},
                new RowMapper<Integer>() {
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer studentUserId;
                        try {
                            studentUserId = rs.getInt("student_uid");

                            return studentUserId;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Student UID, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return studentUserId;
    }

    public CmProgram getCmProgramForParallelProgramId(final Integer parallelProgId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_FOR_PP");
        CmProgram cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId},
                new RowMapper<CmProgram>() {
                    public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgram cmProg;
                        try {
                        	cmProg = new CmProgram();
                        	CmProgramInfo progInfo = cmProg.getCmProgInfo();
                        	cmProg.setId(rs.getInt("id"));
                        	cmProg.setAdminId(rs.getInt("admin_id"));
                            progInfo.setTestDefId(rs.getInt("test_def_id"));
                            cmProg.setTestConfigJson(rs.getString("rtest_config_json"));
                            cmProg.setCustomProgId(rs.getInt("custom_prog_id"));
                            cmProg.setCustomQuizId(rs.getInt("custom_quiz_id"));

                            return cmProg;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting TestDef ID, parallelProgId: %d", parallelProgId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return cmProg;
    }

    public void addProgram(final CmProgram model) {
    	
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PROGRAM");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, model.getAdminId());
                    ps.setInt(2, model.getPassPercent());
                    ps.setInt(3, model.getCustomProgId());
                    ps.setInt(4, model.getCustomQuizId());
                    ps.setString(5, model.getTestConfigJson());
                    ps.setString(6, model.getCmProgInfo().getProgramType().getType());
                    ps.setString(7, model.getCmProgInfo().getSubjectId());

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding: " + model.toString(), e);
                    throw new SQLException("Error adding CM_PROGRAM", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();
        model.setId(id);
    }

    public void addParallelProgram(final CmParallelProgram model) {
    	
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PARALLEL_PROGRAM");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, model.getAdminId());
                    ps.setInt(2, model.getCmProgId());
                    ps.setString(3, model.getPassword());
                    ps.setString(4, model.getName());

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding: " + model.toString(), e);
                    throw new SQLException("Error adding CM_PARALLEL_PROGRAM", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();
        model.setId(id);
    }

    public void addProgramAssignment(final CmProgramAssign model) {
    	
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PROGRAM_ASSIGN");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, model.getUserId());
                    ps.setInt(2, model.getUserProgId());
                    ps.setInt(3, model.getCmProgram().getId());
                    ps.setInt(4, model.getProgSegment());
                    ps.setInt(5, model.getRunId());
                    ps.setInt(6, model.getRunSession());
                    ps.setInt(7, model.getTestId());

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding: " + model.toString(), e);
                    throw new SQLException("Error adding CM_PROGRAM_ASSIGN", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();
        model.setId(id);
    }
    
    /**
     * is User's current Program in CM_PROGRAM?
     * 
     * @param userId
     */
    public boolean currentProgramExistsForStudent(final int userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("CM_PROGRAM_EXISTS_FOR_STUDENT");
        boolean progExists = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean progExists;
                        try {
                            progExists = (rs.getInt("prog_exists") > 0);

                            return progExists;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for CM Program existence, userId: %d", userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return progExists;
    }

    /**
     * add User's current Program to CM_PROGRAM?
     * 
     * @param userId
     */
    public CmProgram addCurrentProgramForStudent(final int userId) throws Exception {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADD_CM_PROGRAM_FOR_STUDENT");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, userId);

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding CM Program for userId: " + userId, e);
                    throw new SQLException("Error adding CM_PROGRAM", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();

        return getCmProgramForId(id);
    }
    
    /**
     * get CM Program for specified ID
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public CmProgram getCmProgramForId(final int id) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_FOR_ID");
        CmProgram cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{id},
                new RowMapper<CmProgram>() {
                    public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgram cmProg;
                        try {
                        	cmProg = new CmProgram();
                        	CmProgramInfo progInfo = cmProg.getCmProgInfo();
                        	cmProg.setId(rs.getInt("id"));
                        	cmProg.setAdminId(rs.getInt("admin_id"));
                            progInfo.setTestDefId(rs.getInt("test_def_id"));
                            cmProg.setTestConfigJson(rs.getString("rtest_config_json"));
                            cmProg.setCustomProgId(rs.getInt("custom_prog_id"));
                            cmProg.setCustomQuizId(rs.getInt("custom_quiz_id"));

                            return cmProg;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting CM Program for ID: %d", id), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return cmProg;
    }

}
