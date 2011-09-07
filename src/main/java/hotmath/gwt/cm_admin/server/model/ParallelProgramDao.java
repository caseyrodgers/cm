package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.spring.SpringManager;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

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

}
