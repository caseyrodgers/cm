package hotmath.cm.login.service.lcom;

import hotmath.gwt.shared.client.util.CmException;
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
				LcomTeacherSignup teacher = new LcomTeacherSignup(rs.getString("first_last"), rs.getString("teacher_id"), rs.getString("email"), rs.getString("district"), rs.getString("zip"));
				teacher.setAdminId(rs.getInt("admin_id"));
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
	
	public void registerTeacher(final Connection conn, LcomTeacherSignup teacher) throws Exception {
		PreparedStatement ps=null;
		try {
			String sql = "insert into LCOM_TEACHER(first_last, teacher_id, email, zip, admin_id)values(?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, teacher.getFirstLast());
			ps.setString(2, teacher.getTeacherId());
			ps.setString(3, teacher.getEmail());
			ps.setString(4, teacher.getZip());
			ps.setInt(5, teacher.getAdminId());
			
			int cnt = ps.executeUpdate();
			if(cnt != 1) {
				throw new CmException("Could not add new LCOM teacher");
			}
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}
	
	public void setupNewTeacherCourse(final Connection conn, String teacherId, String courseId,String courseName) throws Exception {
		PreparedStatement st=null;
		try {
			String sql = "insert into LCOM_TEACHER_COURSE(course_id, teacher_id, course_name)values(?,?,?)";
			st = conn.prepareStatement(sql);
			st.setString(1, courseId);
			st.setString(2,teacherId);
			st.setString(3, courseName);
			int cnt = st.executeUpdate();
			if(cnt != 1) {
				throw new CmException("Could not add new teacher course, see server logs");
			}
		}
		finally {
			SqlUtilities.releaseResources(null,st,null);
		}
	}
	
	public LcomTeacherCourse getTeacherCourse(final Connection conn, String courseId) throws Exception {
		PreparedStatement st=null;
		try {
			String sql = "select * from LCOM_TEACHER_COURSE where course_id = ?";
			st = conn.prepareStatement(sql);
			st.setString(1,courseId);
			ResultSet rs = st.executeQuery();
			if(rs.first()) {
				return new LcomTeacherCourse(rs.getString("teacher_id"), rs.getString("course_id"), rs.getString("course_name"));
			}
			else {
				return null;
			}
		}
		finally {
			SqlUtilities.releaseResources(null,st,null);
		}
	}
	
	
	public LcomStudentSignup getStudent(final Connection conn, String studentId) throws Exception {
		PreparedStatement st=null;
		try {
			String sql = "select * from LCOM_STUDENT where student_id = ?";
			st = conn.prepareStatement(sql);
			st.setString(1,studentId);
			ResultSet rs = st.executeQuery();
			if(rs.first()) {
				return new LcomStudentSignup(rs.getString("first_last"),rs.getString("student_id"), rs.getString("course_id"), rs.getString("teacher_id"), rs.getInt("user_id"));
			}
			else {
				return null;
			}
		}
		finally {
			SqlUtilities.releaseResources(null,st,null);
		}
	}
	
	
	public void registerStudent(final Connection conn, LcomStudentSignup student) throws Exception {
		PreparedStatement st=null;
		try {
			String sql = "insert into LCOM_STUDENT(student_id, first_last, teacher_id, course_id, user_id)values(?,?,?,?,?)";
			st = conn.prepareStatement(sql);
			st.setString(1, student.getStudentUserId());
			st.setString(2, student.getLastFirst());
			st.setString(3, student.getCurrentCourseTeacherUserId());
			st.setString(4, student.getStudentCourseId());
			st.setInt(5, student.getUserId());
			
			int cnt = st.executeUpdate();
			if(cnt != 1) {
				throw new CmException("Could not add new student, see server logs");
			}
		}
		finally {
			SqlUtilities.releaseResources(null,st,null);
		}
	}
	
}
