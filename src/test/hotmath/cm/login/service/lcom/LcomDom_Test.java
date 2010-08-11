package hotmath.cm.login.service.lcom;

import hotmath.gwt.cm.server.CmDbTestCase;

public class LcomDom_Test extends CmDbTestCase {
	public LcomDom_Test(String name){
		super(name);
	}
	
	static String teacherId = "teacher_id_" + System.currentTimeMillis();
	static String courseId = "course_id: " + System.currentTimeMillis();
	static String courseName = "course_name: " + System.currentTimeMillis();
	static String studentId = "student_id: " + System.currentTimeMillis();

	public void testGetTeacherNotExist() throws Exception {
		LcomTeacherSignup teacher = new LcomDom().getTeacher(conn, "NOT_EXIST");
		assertTrue(teacher == null);
	}
	
	public void testRegisterTeacher() throws Exception {
		LcomTeacherSignup teacher = new LcomTeacherSignup("first_last", teacherId, "test@test.com", "district", "12345");
		new LcomDom().registerTeacher(conn, teacher);
		
		teacher = new LcomDom().getTeacher(conn,teacherId);
		assertTrue(teacher != null);
	}
	
	public void testGetTeacherCourseNotExist() throws Exception {
		LcomTeacherCourse course = new LcomDom().getTeacherCourse(conn, "NOT_EXIST");
		assertTrue(course == null);
	}
	
	public void testGetCourse() throws Exception {
		LcomTeacherCourse course = new LcomDom().getTeacherCourse(conn, courseId);
		assertTrue(course == null);
		
		new LcomDom().setupNewTeacherCourse(conn, teacherId, courseId, courseName);
		
		course = new LcomDom().getTeacherCourse(conn, courseId);
		assertTrue(course != null);
	}
	
	public void testGetStudentNotExist() throws Exception {
		LcomStudentSignup student = new LcomDom().getStudent(conn, "NOT_EXIST");
		assertTrue(student == null);
	}
	
	public void testGetStudent() throws Exception {
		LcomStudentSignup student = new LcomDom().getStudent(conn, studentId);
		assertTrue(student == null);
		
		student = new LcomStudentSignup("first_last",studentId, courseId, teacherId, 0);
		new LcomDom().registerStudent(conn, student);

		student = new LcomDom().getStudent(conn, studentId);
		assertTrue(student != null);
	}
}
