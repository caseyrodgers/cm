package hotmath.gwt.cm_admin.server.model;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudentQuickSearcher {
    
    Connection conn;
    Set<Integer> studentUids;
    String inList;
    List<Integer> matches = new ArrayList<Integer>();
    
    public StudentQuickSearcher(final Connection conn, Set<Integer> studentUids) {
        this.studentUids = studentUids;
        this.conn = conn;
        this.inList = createInList(studentUids);
    }
    
    private String createInList(Set<Integer> list) {
        String il = null;
        for(Integer i: list) {
            if(il != null) 
                il += ",";
            else
                il = "";
            
            il+=i;
        }
        return il;
    }

    
    /** Perform quick searches on set pool 
     *  
     * @param conn
     * @param studentUids
     * @throws Exception
     */
    public List<Integer> doQuickSearch(String search) throws Exception {
        search = search.trim();
        matches.clear();
        checkCoreFields(search);
        return matches;
    }
    
    private void checkCoreFields(String search) throws Exception {
        String sql = 
        "SELECT u.uid " +
        "FROM  HA_USER u " +
        "  JOIN CM_GROUP g on g.id = u.group_id " +
        "  JOIN CM_USER_PROGRAM p on p.id = u.user_prog_id " +
        "  JOIN HA_TEST_DEF d ON d.test_def_id = p.test_def_id " +
        "where u.uid in (" + inList + ") " +
        "and ( " +
        "    lower(u.user_name) like ? " +
        "    or " +
        "    lower(u.user_passcode) like ? " +
        "    or " +
        "    lower(g.name) like ? " +
        "    or " +
        "    lower(d.test_name) like ? " +
        " ) ";
        PreparedStatement pstat=null;
        try {
            search = "%" + search + "%";
            
            pstat = conn.prepareStatement(sql);
            
            pstat.setString(1, search);
            pstat.setString(2, search);
            pstat.setString(3, search);
            pstat.setString(4, search);
            
            ResultSet rs = pstat.executeQuery();
            while(rs.next()) {
                matches.add(rs.getInt("uid"));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }
    }
}
