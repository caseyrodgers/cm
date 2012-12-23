package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class VerifyWhiteboardCompression {
	
	private boolean wasCompressed;
	private int lastId;

    public void doIt(int start, int limit) {
        Connection conn=null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            conn = HMConnectionPool.getConnection();

            String sql = "select whiteboard_id, command_data from HA_TEST_RUN_WHITEBOARD where whiteboard_id >= " + start + " limit " + limit;

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {

            	lastId = rs.getInt("whiteboard_id");

            	String commandData = loadCommandData(rs);
            	
            	if (wasCompressed == false && commandData != null) {
            		// compress, decompress, and compare;
            		byte[] compressed = CompressHelper.compress(commandData.getBytes());
            		String decompressed = CompressHelper.decompress(compressed);
            		if (commandData.equals(decompressed) == false) {
            			System.out.println("ERROR: whiteboard_id: " + lastId);
            		}
            	}
            	else if (commandData == null) {
            		System.out.println("WARN: command_data is NULL; whitboard_id: " + lastId);
            	}
            }
            System.out.println("lastId: " + lastId);
        } catch (Exception e) { 
	    	e.printStackTrace();
	    } finally {
		    SqlUtilities.releaseResources(null, ps3, null);
		    SqlUtilities.releaseResources(rs2, ps2, null);
		    SqlUtilities.releaseResources(rs, ps, conn);
	    }
	    System.out.println("Completed verifying whiteboard compression, beginId: " + start + ", endId: " + lastId);
	}

    private String loadCommandData(ResultSet rs) throws Exception {
    	byte[] compressed = rs.getBytes("command_data");
    	if (compressed != null && compressed[0] != "{".getBytes("UTF-8")[0]) {
    		wasCompressed = true;
    		return CompressHelper.decompress(compressed);
    	}
    	else {
    		wasCompressed = false;
    		return rs.getString("command_data");
    	}
    }
    
    static public void main(String as[]) {
        try {
            int start=Integer.parseInt(as[0]);
            int limit=Integer.parseInt(as[1]);

            VerifyWhiteboardCompression vwc = new VerifyWhiteboardCompression();
            vwc.doIt(start, limit);
            System.exit(0);
        	
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }    	
}
