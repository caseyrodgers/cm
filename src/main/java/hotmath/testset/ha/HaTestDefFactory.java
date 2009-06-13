package hotmath.testset.ha;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** manage the creation of HaTestDef objects
 * 
 * a HaTestDef object represents a specific type of test
 * either an assement test + prescription, or a placement test
 * 
 * @author Casey
 *
 */
public class HaTestDefFactory {
	
	/** Create the appropriate HaTestDef depending on use
	 * 
	 * @TODO: use something other than the occurance of placement
	 *        as indicate of type.
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static HaTestDef createTestDef(String name) throws Exception {
		if(name.indexOf("Auto-Enrollment") > -1) {
			return new HaTestDefPlacement(name);
		}
		else return new HaTestDef(name);
	}
	
	public static HaTestDef createTestDef(int testDefId) throws Exception {
		Connection conn=null;
	    PreparedStatement pstat=null;
	    try {
	        String sql = "select test_name  " +
	                     " from HA_TEST_DEF d " +
	                     " where test_def_id = ? ";
	        
	        conn = HMConnectionPool.getConnection();
	        pstat = conn.prepareStatement(sql);
	        
	        pstat.setInt(1, testDefId);
	        
	        ResultSet rs = pstat.executeQuery();
	        if(!rs.first())
	        	throw new Exception("Test definition not found");
	        
	        return createTestDef(rs.getString("test_name"));
	    }
	    finally {
	        SqlUtilities.releaseResources(null,pstat,conn);
	    }    				
	}
}
