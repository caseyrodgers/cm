package hotmath.cm.login.service.lcom;

import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LcomDom {
	
	public LcomTeacherSignup getTeacher(final Connection conn, String teacherId) throws Exception {
		PreparedStatement st=null;
		try {
			String sql = "select * from LCOM_TEACHER where teacher_id = ?";
			st = conn.prepareStatement(sql);
			st.setString(1,teacherId);
			ResultSet rs = st.executeQuery();
			if(rs.first()) {
				LcomTeacherSignup teacher = new LcomTeacherSignup(rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("district"), rs.getString("zip"), rs.getString("courseName"), rs.getString("courseId"));
				return teacher;
			}
			else {
				return null;
			}
		}
		finally {
			SqlUtilities.releaseResources(null,st,null);
		}
	}

}
