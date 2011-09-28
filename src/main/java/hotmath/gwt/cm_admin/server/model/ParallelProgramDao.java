package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
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
	
    public boolean isParallelProgramGroup(final Integer adminId, final String groupName) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_GROUP");
        boolean isParallelProgramGroup = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{adminId, groupName},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isParallelProgramGroup;
                        try {
                            isParallelProgramGroup = (rs.getInt("is_parallel_prog_group") > 0);

                            return isParallelProgramGroup;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Parallel Program Group: %s, AdminId: %d", groupName, adminId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isParallelProgramGroup;
    }

    public boolean isParallelProgramStudent(final Integer userTemplateId, final String studentPassword) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_STUDENT");
        boolean isParallelProgramStudent = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userTemplateId, studentPassword},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isParallelProgramStudent;
                        try {
                            isParallelProgramStudent = (rs.getInt("is_parallel_prog_student") > 0);

                            return isParallelProgramStudent;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Parallel Program, userTemplateId: %d, password: %s", userTemplateId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isParallelProgramStudent;
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
                    ps.setString(5, model.getCmProgInfo().getProgramType().getType());
                    ps.setString(6, model.getCmProgInfo().getSubjectId());
                    ps.setString(7, model.getCmProgInfo().getProgramType().getType());
                    ps.setString(8, model.getCmProgInfo().getSubjectId());

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

}
