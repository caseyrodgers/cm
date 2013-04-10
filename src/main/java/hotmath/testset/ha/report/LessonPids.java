package hotmath.testset.ha.report;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

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
        conn.createStatement().executeUpdate("drop table if exists HA_PROGRAM_LESSON_PIDS");
        conn.createStatement().executeUpdate("create table HA_PROGRAM_LESSON_PIDS(id integer auto_increment not null primary key, lesson varchar(100), file varchar(100), subject varchar(100), pid varchar(100))");
        ResultSet rs = conn.createStatement().executeQuery("select distinct lesson, file, subject from HA_PROGRAM_LESSONS order by lesson");
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement("insert into HA_PROGRAM_LESSON_PIDS(lesson,file,subject,pid)values(?,?,?,?)");
            while(rs.next()) {
                String lesson = rs.getString("lesson");
                String file = rs.getString("file");
                String subject = rs.getString("subject");
                
                __logger.info("Processing: " + lesson + ", " + file + ", " + subject);
                ps.setString(1,  lesson);
                ps.setString(2,  file);
                ps.setString(3,  subject);
                
                List<ProblemDto> pids = AssignmentDao.getInstance().getLessonProblemsFor(conn, lesson, file, subject);
                for(ProblemDto d: pids) {
                    ps.setString(4,  d.getPid());
                    
                    ps.executeUpdate();
                }
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps, null);
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
