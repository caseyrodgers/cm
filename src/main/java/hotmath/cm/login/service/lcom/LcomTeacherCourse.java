package hotmath.cm.login.service.lcom;

public class LcomTeacherCourse {
	String courseId;
	String courseName;
	String teacherId;
	
	public LcomTeacherCourse(String teacherId, String courseId, String courseName) {
		this.teacherId = teacherId;
		this.courseId = courseId;
		this.courseName = courseName;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}
}
