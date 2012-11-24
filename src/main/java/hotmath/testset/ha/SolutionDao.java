package hotmath.testset.ha;

import hotmath.ProblemID;
import hotmath.cm.util.CompressHelper;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.spring.SpringManager;

import java.io.UnsupportedEncodingException;
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
    public SolutionContext getSolutionContext(int runId, final String pid) {
        String sql = "select problem_number, variables from HA_SOLUTION_CONTEXT where run_id = ? and pid = ? order by id";
        List<SolutionContext> matches = getJdbcTemplate().query(sql,
                new Object[]{runId,pid},
                new RowMapper<SolutionContext>() {
                    @Override
                    public SolutionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	String solnCtx;
                    	try {
                        	solnCtx = loadSolutionContextString(rs);
                    	}
                    	catch( Exception e) {
                    		throw new SQLException(e);
                    	}
                        return new SolutionContext(pid, rs.getInt("problem_number"), solnCtx);
                    }
                });
        return matches.size()>0?matches.get(0):null;
    }
    
    public String getSolutionContextString(int runId, String pid) {
        final ProblemID pidO = new ProblemID(pid);
        final String sql = "select variables from HA_SOLUTION_CONTEXT where run_id = ? and pid = ? and problem_number = ?";
        List<String> matches = getJdbcTemplate().query(sql,
                new Object[]{runId,pid,pidO.getProblemSetProblemNumber()},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	__logger.debug("problem_number: " + pidO.getProblemNumber());
                    	try {
                    		return loadSolutionContextString(rs);
                    	}
                    	catch (Exception e) {
                    		throw new SQLException(e.getMessage());
                    	}
                    }
                });
        return matches.size() > 0?matches.get(0):null;
    }
    
    private String loadSolutionContextString(ResultSet rs) throws Exception {
    	byte[] compressed = rs.getBytes("variables");
    	if (compressed[0] != "{".getBytes("UTF-8")[0]) {
    		return CompressHelper.decompress(compressed);
    	}
    	else {
    		return rs.getString("variables");
    	}

    }

    /** TODO: Should we allow overwriting of existing Solution Context?
     * 
     * @param runId
     * @param pid
     * @param problemNumber
     * @param contextJson
     */
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

    			byte[] inBytes = null;
    			try {
    				inBytes = contextJson.getBytes("UTF-8");

    				byte[] outBytes = CompressHelper.compress(inBytes);
    				ps.setBytes(5, outBytes);

    				if (__logger.isDebugEnabled()) __logger.debug("in len: " + inBytes.length +", out len: " + outBytes.length);
    			} catch (UnsupportedEncodingException e) {
    				__logger.error(String.format("*** Error saving solution context for run_id: %d, pid: %s, prob#: %d",
    						runId, pid, problemNumber), e);
    				throw new SQLException(e.getLocalizedMessage());
    			}
    			return ps;
    		}
    	});
    }

    public void saveSolutionContextCompressed(final int runId, final String pid, final int problemNumber, final String contextJson) {
    	getJdbcTemplate().update(new PreparedStatementCreator() {
    		@Override
    		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    			PreparedStatement ps = null;
    			String sql = "insert into HA_SOLUTION_CONTEXT_COMPRESSED(time_viewed, run_id, pid, problem_number, variables)values(?,?,?,?,?)";
    			ps = con.prepareStatement(sql);

    			ps.setDate(1, new Date(System.currentTimeMillis()));
    			ps.setInt(2, runId);
    			ps.setString(3, pid);
    			ps.setInt(4, problemNumber);

    			byte[] inBytes = null;
    			try {
    				inBytes = contextJson.getBytes("UTF-8");

    				byte[] outBytes = CompressHelper.compress(inBytes);
    				ps.setBytes(5, outBytes);

    				if (__logger.isDebugEnabled()) __logger.debug("in len: " + inBytes.length +", out len: " + outBytes.length);
    			} catch (UnsupportedEncodingException e) {
    				__logger.error(String.format("*** Error saving solution context for run_id: %d, pid: %s, prob#: %d",
    						runId, pid, problemNumber), e);
    				throw new SQLException(e.getLocalizedMessage());
    			}
    			return ps;
    		}
    	});
    }

    public String getSolutionContextCompressedString(int runId, String pid) {
        ProblemID pidO = new ProblemID(pid);
        //TODO: remove following hard coding
        pidO.setProblemSetProblemNumber(1);
        String sql = "select variables from HA_SOLUTION_CONTEXT_COMPRESSED where run_id = ? and pid = ? and problem_number = ? limit 1";
        List<String> matches = getJdbcTemplate().query(sql,
                new Object[]{runId,pid,pidO.getProblemSetProblemNumber()},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	byte[] compressed = rs.getBytes("variables");
                    	try {
                    		return CompressHelper.decompress(compressed);
                    	}
                    	catch (Exception e) {
                    		throw new SQLException(e.getMessage());
                    	}
                    }
                });
        return matches.size() > 0?matches.get(0):null;
    }

    public String getSolutionXML(String pid) {
        String sql = "select solutionxml from SOLUTIONS where problemindex = ?";
        return getJdbcTemplate().queryForObject(sql,new Object[]{pid},
                new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("solutionxml");
            }
        });
    }
}
