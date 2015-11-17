package hotmath.testset.ha.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

/** Get all pids associated with a given topic
 * 
 * @author casey
 *
 */
public class LessonPidsReport {
	
	public LessonPidsReport() throws Exception {
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = HMConnectionPool.getConnection();
			conn.createStatement().executeQuery("drop table if exists junk_lesson_pids");
			conn.createStatement().executeQuery("create table junk_lesson_pids(id int auto_increment not null primary key, lesson varchar(100) not null, pid varchar(200) not null)");

			ps = conn.prepareStatement("insert into junk_lesson_pids(lesson, pid) values(?,?)");
			ResultSet rs = conn.createStatement().executeQuery("select distinct file from inmh_assessment order by file");
			while(rs.next()) {
				String file = rs.getString("file");

				System.out.println("Looking for pids for: " + file);
				List<ProblemDto> pids = AssignmentDao.getInstance().getLessonProblemsFor(conn, file,  file,  "");
				ps.setString(1,  file);

				if(pids.size() > 0) {
					for(ProblemDto p: pids) {
						ps.setString(2,  p.getPid());
						ps.executeUpdate();
					}
				}
				else {
					ps.setString(2, "No pids found");
				}
			}
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
		

	public static void main(String[] args) {
		try {
			new LessonPidsReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	

}
