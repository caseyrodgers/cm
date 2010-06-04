package hotmath.cm.util;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

/*
 * @author bob
 */

public class QueryHelper_Test extends TestCase {
    
    public QueryHelper_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
    }
    
    public void testSingleChunk() throws Exception {
    	List<Integer> vals = new ArrayList<Integer>();
    	for (int i=1; i < 11; i++) {
    		vals.add(i);
    	}
        String sql = QueryHelper.createInListSQL("select * from mytable where %s and this='that'", vals, "UID", 20);
        System.out.println("sql: " + sql);
        assertTrue(sql.length() > 0);
    }

    public void testMultiChunk() throws Exception {
    	List<Integer> vals = new ArrayList<Integer>();
    	for (int i=1; i < 50; i++) {
    		vals.add(i);
    	}
        String sql = QueryHelper.createInListSQL("select * from mytable where %s and this='that'", vals, "UID", 20);
        System.out.println("sql: " + sql);
        assertTrue(sql.length() > 0);
    }
    
    public void testZeroUids() throws Exception {
        List<Integer> vals = new ArrayList<Integer>();
        String sql = QueryHelper.createInListSQL("select * from mytable where %s and this='that'", vals, "UID", 20);
        System.out.println("sql: " + sql);
        assertTrue(sql.length() > 0);
    }

}
