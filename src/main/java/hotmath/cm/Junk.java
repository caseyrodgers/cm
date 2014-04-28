package hotmath.cm;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Junk {

    PreparedStatement _ps;

    public Junk() throws Exception {

        String sql = "select * from HA_SOLUTION_GLOBAL_CONTEXT where id = 62778";
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            _ps = conn.prepareStatement("update HA_SOLUTION_GLOBAL_CONTEXT set variables = ? where id = ? ");
            
            ResultSet rs = conn.createStatement().executeQuery(sql);
            rs.first();
            String v = rs.getString("variables");
            
            _ps.setString(1,  v);
            _ps.setInt(2,  62698);
            int cnt = _ps.executeUpdate();
            if(cnt != 1) {
            	throw new Exception("Could not update record!");
            }
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }


    static public void main(String as[]) {
        try {
            new Junk();
            System.out.println("Complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.exit(0);
    }

}
