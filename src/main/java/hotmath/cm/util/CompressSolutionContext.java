package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class CompressSolutionContext {

    final static Logger __logger = Logger.getLogger(CompressSolutionContext.class);

    public void doIt(int start, int limit) {
        Connection conn=null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {
            conn = HMConnectionPool.getConnection();

            String sql = "select id, variables from HA_SOLUTION_CONTEXT where id >= " + start + " limit " + limit;

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            String sql2 = "update HA_SOLUTION_CONTEXT set variables = ? where id = ?";
            ps2 = conn.prepareStatement(sql2);

            int counter = 1;
            int compressCount = 0;
            int skipCount = 0;
            int lastId = 0 ;
            while (rs.next()) {

            	int id = rs.getInt("id");
            	lastId = id;

                if ((counter++)%1000 == 0) {
                    __logger.debug("counter: " + counter);
                }

                byte[] compressed = rs.getBytes("variables");
            	if (compressed[0] != "{".getBytes("UTF-8")[0]) {
            		skipCount++;
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

        				if (ps2.executeUpdate() != 1) {
                        	__logger.error("COULD NOT UPDATE RECORD: id: " + id);
                        }
        			} catch (UnsupportedEncodingException e) {
        				__logger.error(String.format("*** Error saving solution context for id: %d", id), e);
        				throw new SQLException(e.getLocalizedMessage());
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
	    __logger.info("Completed updating HA_SOLUTION_CONTEXQT");
	}
    
    static public void main(String as[]) {
        try {
            int start=Integer.parseInt(as[0]);
            int limit=Integer.parseInt(as[1]);

            CompressSolutionContext fli = new CompressSolutionContext();
            fli.doIt(start, limit);
            System.exit(0);
        	
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }    	
}
