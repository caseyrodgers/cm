package hotmath.testset.ha;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** manage the creation of HaTestDef objects
 * 
 * a HaTestDef object represents a specific type of test
 * either an assessment test + prescription, or a placement test
 * 
 * @author Casey
 *
 */
public class HaTestDefFactory {
	
	/** Create the appropriate HaTestDef depending on use
	 * 
	 * @TODO: use something other than the occurrence of "Auto-Enrollment"
	 *        as indicator of type.
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static HaTestDef createTestDef(final Connection conn, String name) throws Exception {
		if(name.indexOf("Auto-Enrollment") > -1) {
			return new HaTestDefPlacement(conn, name);
		}
		else {
			return new HaTestDefDao().getTestDef(conn, name);
		}
	}
	
	public static HaTestDef createTestDef(final Connection conn, int testDefId) throws Exception {
	    PreparedStatement pstat=null;
	    ResultSet rs = null;
	    try {
	        String sql = "select test_name  " +
	                     " from HA_TEST_DEF d " +
	                     " where test_def_id = ? ";
	        
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setInt(1, testDefId);
	        
	        rs = pstat.executeQuery();
	        if(!rs.first())
	        	throw new Exception("Test definition not found");
	        
	        return createTestDef(conn, rs.getString("test_name"));
	    }
	    finally {
	        SqlUtilities.releaseResources(rs,pstat,null);
	    }    				
	}
}
