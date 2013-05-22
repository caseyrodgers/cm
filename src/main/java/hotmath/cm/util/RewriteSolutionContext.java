package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    	int lastId = 0;
    	int lastSkippedId = 0;

    	String sql = "select id, variables from HA_SOLUTION_CONTEXT where id >= ? limit ?";

		String sql2 = "update HA_SOLUTION_CONTEXT set variables = ? where id = ?";

		try {
    		conn = HMConnectionPool.getConnection();
    		ps = conn.prepareStatement(sql);
    		ps2 = conn.prepareStatement(sql2);

    		int compressCount = 0;
    		int skipCount = 0;
			int counter = 0;

    		while(count < recsToProcess) {

    			ps.setInt(1, start + count);
    			ps.setInt(2, limit);

    			rs = ps.executeQuery();


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
    					lastSkippedId = id;
    					continue;
    				}
    				else {
    					compressCount++;
    					String context = rs.getString("variables");
    					byte[] inBytes = null;
    					try {
    						inBytes = context.getBytes("UTF-8");

    						byte[] outBytes = CompressHelper.compress(inBytes);
    						ps2.setBytes(1, outBytes);
    						ps2.setInt(2, id);

    						if (__logger.isDebugEnabled()) __logger.debug("in len: " + inBytes.length +", out len: " + outBytes.length);

    					} catch (UnsupportedEncodingException e) {
    						__logger.error(String.format("*** Error updating solution context for id: %d", id), e);
    						throw new SQLException(e.getLocalizedMessage());
    					}
    				}
    				if (ps2.executeUpdate() != 1) {
    					__logger.error("*** Could NOT UPDATE record: id: " + id);
    				}

    			}
    		}
    		__logger.info(String.format("Compressed: %d, skipped: %d", compressCount, skipCount));
    		__logger.info(String.format("lastId: %d, lastSkippedId: %d", lastId, lastSkippedId));
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
