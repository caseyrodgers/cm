package hotmath.gwt.cm_admin.server.model;

import hotmath.util.HMConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sb.client.SbTesterFrameGeneric;
import sb.util.SbException;
import sb.util.SbTestImpl;

/** Do one time transfer for new Topic lessons 
 *  into Pilot account.
 * 
 * @author casey
 *
 */
public class CustomProgramJunkTransfer implements SbTestImpl {
    
    Connection conn;
    static final int DEMO_ADMIN_ID=13;
    static final int FROM_ADMIN_ID=5;
    
    String progNames[] = {"Essentials Topics", "Pre-Algebra Topics", "Algebra 1 Topics", "Geometry Topics",  "Algebra 2 Topics"};
    public CustomProgramJunkTransfer(Connection conn) throws Exception  {
        this.conn = conn;
    }
    
    private void moveNewTopicsOver() throws Exception {
        for(String progName: progNames) {
            makeSureExistingCustomProgramsAreRemovedFromPilotAccount(progName);
            copyNewProgramsToPilotAccount(progName);
        }
    }

    private void makeSureExistingCustomProgramsAreRemovedFromPilotAccount(String progName) throws Exception {
        String sql = "delete from HA_CUSTOM_PROGRAM_LESSON where program_id in (select id from HA_CUSTOM_PROGRAM where admin_id = ? and name = ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, DEMO_ADMIN_ID); // demo admin
        ps.setString(2, progName);
        int res = ps.executeUpdate();
        System.out.println("Removed: " + res);
        ps.close();
    }
    
    private void copyNewProgramsToPilotAccount(String progName) throws Exception {
       
        String sql = "select tcp.id as to_cp, fcp.id as from_cp " +
                     " from HA_CUSTOM_PROGRAM tcp, JUNK_HA_CUSTOM_PROGRAM fcp " +
                     " where tcp.admin_id = ? and tcp.name = ? " +
                     " and fcp.admin_id = ? and fcp.name = tcp.name";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, DEMO_ADMIN_ID);
        ps.setString(2, progName);
        ps.setInt(3, FROM_ADMIN_ID);
        
        ResultSet rs = ps.executeQuery();
        rs.first();
        int toCp = rs.getInt("to_cp");
        int fromCp = rs.getInt("from_cp");
        ps.close();
        
        
        sql = "select * from JUNK_CUST_PROG_TOPICS_LESSON where program_id = ? order by id";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, fromCp);
      
        rs = ps.executeQuery();
        int cnt = 0;
        while(rs.next()) {
            cnt += addNewLesson(toCp, rs.getString("lesson"), rs.getString("file"));
        }
        ps.close();
        System.out.println("Added " + cnt + " to " + progName);
        ps.close();
    }
    
    private int addNewLesson(int progId, String lesson, String file) throws Exception {
        String sqlInsert = "insert into HA_CUSTOM_PROGRAM_LESSON(program_id, lesson, file, subject) " +
                " values(?,?,?,'')";
        PreparedStatement ps = conn.prepareStatement(sqlInsert);
        ps.setInt(1, progId);
        ps.setString(2, lesson);
        ps.setString(3, file);
        
        int res = ps.executeUpdate();
        return res;
    }
    
    @Override
    public void doTest(Object objLogger, String sFromGUI) throws SbException {
        try {
            moveNewTopicsOver();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    static public void main(String as[]) {
        try {
            if(as.length > 0) {
                new CustomProgramJunkTransfer(HMConnectionPool.getConnection()).moveNewTopicsOver();
            }
            else {
                SbTesterFrameGeneric tester = new SbTesterFrameGeneric(new CustomProgramJunkTransfer(HMConnectionPool.getConnection()));
                tester.doTest(null, null);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
