package hotmath.testset.ha;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Provide DAO functionality for HaTestDefs
 * 
 * @author casey
 * 
 */
public class SolutionDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(HaTestDefDao.class);

    static private SolutionDao __instance;

    static public SolutionDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (SolutionDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(SolutionDao.class.getName());
        }
        return __instance;
    }

    private SolutionDao() {
        /** Empty */
    }

    
    /** Return all contexts associated with this PID (problem set).
     * 
     * The will be from 1-N (zero based)
     * 
     * @param runId
     * @param pid
     * @return
     */
    public List<SolutionContext> getSolutionContext(int runId, final String pid) {
        String sql = "select problem_number, variables from HA_SOLUTION_CONTEXT where run_id = ? and pid = ? order by id";
        List<SolutionContext> matches = getJdbcTemplate().query(sql,
                new Object[]{runId,pid},
                new RowMapper<SolutionContext>() {
                    @Override
                    public SolutionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new SolutionContext(pid, rs.getInt("problem_number"), rs.getString("variables"));
                    }
                });
        return matches;
    }
    
    public String getSolutionContext(int runId, String pid, int probNum) {
        String sql = "select variables from HA_SOLUTION_CONTEXT where run_id = ? and pid = ? and problem_number = ?";
        List<String> matches = getJdbcTemplate().query(sql,
                new Object[]{runId,pid,probNum},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("variables");
                    }
                });
        return matches.size() > 0?matches.get(0):null;
    }

    public void saveSolutionContext(final int runId, final String pid, final int problemNumber, final String contextJson) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = null;
                String sql = "insert into HA_SOLUTION_CONTEXT(time_viewed, run_id, pid, problem_number, variables)values(?,?,?,?,?)";
                ps = con.prepareStatement(sql);

                ps.setDate(1, new Date(System.currentTimeMillis()));
                ps.setInt(2, runId);
                ps.setString(3, pid);
                ps.setInt(4, problemNumber);
                ps.setString(5, contextJson);
                return ps;
            }
        });
    }
}
