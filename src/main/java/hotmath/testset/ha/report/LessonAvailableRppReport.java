package hotmath.testset.ha.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.InmhItemData;
import hotmath.assessment.RppWidget;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

public class LessonAvailableRppReport {
	
	public LessonAvailableRppReport() throws Exception {
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = HMConnectionPool.getConnection();
			ps = conn.prepareStatement("select * from inmh_assessment order by file");
			
			
			conn.createStatement().executeQuery("drop table if exists junk_lesson_count");
			conn.createStatement().executeQuery("create table junk_lesson_count(lesson varchar(100) not null, total_rpp int, grade_7 int, grade_8 int, grade_9 int,grade_10 int,grade_11 int,grade_12 int)");
			
			Map<String, List<RppWidget>> map = new HashMap<String, List<RppWidget>>();
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String file = rs.getString("file");
				String range = rs.getString("range");
				
				System.out.println("-> " + file + ", " + range);
				file = file.trim();
				List<RppWidget> ranges = map.get(file);
				if(ranges == null) {
					ranges = new ArrayList<RppWidget>();
					map.put(file,  ranges);
				}

				InmhItemData iid = new InmhItemData(new INeedMoreHelpItem("", file,file));
				List<RppWidget> pool = iid.getWidgetPool(conn,  "avail_rpp_rpt");

				ranges.addAll(pool);
			}
			
			
			
			int g7, g8, g9, g10, g11, g12, total;
			
			ps = conn.prepareStatement("insert into junk_lesson_count(lesson, grade_7,grade_8,grade_9,grade_10,grade_11,grade_12, total_rpp)values(?,?,?,?,?,?,?,?)");
			for(String l : map.keySet()) {
			
				ps.setString(1, l);
				List<RppWidget> ranges = map.get(l);
				total = g7 = g8 = g9 = g10 = g11 = g12 = 0;				
				for(RppWidget r: ranges) {
					
					List<RppWidget> expanded = AssessmentPrescription.expandProblemSetPids(r);
					
					g7 += getLessonsInGrade(7, expanded);
					g8 += getLessonsInGrade(8, expanded);
					g9 += getLessonsInGrade(9, expanded);
					g10 += getLessonsInGrade(10, expanded);
					g11 += getLessonsInGrade(11, expanded);
					g12 += getLessonsInGrade(12, expanded);
					
					total += expanded.size();
				}
				ps.setInt(2,  g7);
				ps.setInt(3,  g8);
				ps.setInt(4,  g9);
				ps.setInt(5,  g10);
				ps.setInt(6,  g11);
				ps.setInt(7,  g12);
				ps.setInt(8,  total);
				ps.executeUpdate();

			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
		
		
	}
	
	private int getLessonsInGrade(int grade, List<RppWidget> expanded) {
		
		int cnt=0;
		for(RppWidget e: expanded) {
			cnt += e.isGradeLevel(grade)?1:0;
		}
		return cnt;
	}

	public static void main(String[] args) {
		try {
			new LessonAvailableRppReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	

}
