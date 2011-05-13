package hotmath.conn;

import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class TestDao extends SimpleJdbcDaoSupport {
    public void doSomething() {
        List list = getSimpleJdbcTemplate().queryForList("select * from GT_CONFIG");
        Iterator i=list.iterator();        
        while(i.hasNext()) {
            System.out.println(i.next());
        }
    }
}