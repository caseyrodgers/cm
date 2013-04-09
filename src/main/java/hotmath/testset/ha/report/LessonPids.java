package hotmath.testset.ha.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class LessonPids {

    static Logger __logger = Logger.getLogger(LessonPids.class);
    Connection _conn;
    public LessonPids() throws Exception {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            runReport(conn);
        }
        finally {
            SqlUtilities.releaseResources(null,  null,  conn);
        }
    }
    
    private void runReport(Connection conn) throws Exception {
        ResultSet rs = conn.createStatement().executeQuery("select distinct lesson, file, subject from HA_PROGRAM_LESSONS order by lesson");
        
        while(rs.next()) {
            String lesson = rs.getString("lesson");
            String file = rs.getString("file");
            String subject = rs.getString("subject");
            
            int probCount = AssignmentDao.getInstance().getLessonProblemsFor(conn, lesson, file, subject).size();
            
            System.out.println(lesson + "|" + file + "|" + subject + "|" + probCount);
        }
    }
    
    static public void main(String as[]) {
        try {
            new LessonPids();
            System.out.println("Done");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
