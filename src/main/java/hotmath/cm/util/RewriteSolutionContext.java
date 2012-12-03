package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class RewriteSolutionContext {

    final static Logger __logger = Logger.getLogger(RewriteSolutionContext.class);

    public void doIt(int start, int limit, int recsToProcess) {
    	Connection conn=null;
    	PreparedStatement ps = null;
    	PreparedStatement ps2 = null;
    	ResultSet rs = null;
    	long startTime = System.currentTimeMillis();
    	int count = 0;
    	int lastId = 0 ;

    	String sql = "select id, time_viewed, run_id, pid, problem_number, variables from HA_SOLUTION_CONTEXT where id >= ? limit ?";

    	try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(sql);

    		int compressCount = 0;
    		int skipCount = 0;
			int counter = 0;

    		while(count < recsToProcess) {

    			ps.setInt(1, start + count);
    			ps.setInt(2, limit);

    			rs = ps.executeQuery();

    			String sql2 = "insert into HA_SOLUTION_CONTEXT_NEW (time_viewed, run_id, pid, problem_number, variables) values (?,?,?,?,?)";
    			ps2 = conn.prepareStatement(sql2);

    			while (rs.next()) {

    				count++;
    				int id = rs.getInt("id");
    				lastId = id;

    				if ((counter++)%1000 == 0) {
    					__logger.debug("counter: " + counter);
    				}

    				byte[] compressed = rs.getBytes("variables");
    				if (compressed[0] != "{".getBytes("UTF-8")[0]) {
    					skipCount++;
    					ps2.setBytes(5, compressed);
    				}
    				else {
    					compressCount++;
    					String context = rs.getString("variables");
    					byte[] inBytes = null;
    					try {
    						inBytes = context.getBytes("UTF-8");

    						byte[] outBytes = CompressHelper.compress(inBytes);
    						ps2.setBytes(5, outBytes);

    						if (__logger.isDebugEnabled()) __logger.debug("in len: " + inBytes.length +", out len: " + outBytes.length);

    					} catch (UnsupportedEncodingException e) {
    						__logger.error(String.format("*** Error saving solution context for id: %d", id), e);
    						throw new SQLException(e.getLocalizedMessage());
    					}
    				}
    				ps2.setDate(1, rs.getDate("time_viewed"));
    				ps2.setInt(2, rs.getInt("run_id"));
    				ps2.setString(3, rs.getString("pid"));
    				ps2.setInt(4, rs.getInt("problem_number"));
    				if (ps2.executeUpdate() != 1) {
    					__logger.error("*** Could NOT INSERT record: id: " + id);
    				}

    			}
    		}
    		__logger.info(String.format("Compressed: %d, skipped: %d", compressCount, skipCount));
    		__logger.info("lastId: " + lastId);
    	} catch (Exception e) { 
    		e.printStackTrace();
    	} finally {
    		SqlUtilities.releaseResources(null, ps2, null);
    		SqlUtilities.releaseResources(rs, ps, conn);
    	}
    	__logger.info("Completed updating HA_SOLUTION_CONTEXT in: " + (System.currentTimeMillis()-startTime) + " msec");
    }
    
    static public void main(String as[]) {
        try {
            int start = Integer.parseInt(as[0]);
            int limit = Integer.parseInt(as[1]);
            int recsToProcess = Integer.parseInt(as[2]);

            __logger.info(String.format("start: %d,  limit: %d, recsToProcess: %d", start, limit, recsToProcess));

            RewriteSolutionContext fli = new RewriteSolutionContext();
            fli.doIt(start, limit, recsToProcess);
            System.exit(0);
        	
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }    	
}
