package hotmath.gwt.cm.server;

import java.sql.Connection;

import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class DbTestCase extends TestCase {
    
    public DbTestCase(String name) {
        super(name);
    }
    
    public  Connection conn = null;
    
    @Override
    protected void setUp() throws Exception {
        conn = HMConnectionPool.getConnection();
    }
    
    @Override
    protected void tearDown() throws Exception {
        if(conn != null)
            conn.close();
    }

}
