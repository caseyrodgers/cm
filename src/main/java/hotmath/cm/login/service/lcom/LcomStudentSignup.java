package hotmath.cm.login.service.lcom;

/** Encapsulation of LCOM student
 * 
 * @author casey
 *
 */
public class LcomStudentSignup {
	String lastFirst;
	int userId;
	String studentUserId;
	String studentCourseId;
	String currentCourseTeacherUserId;
	
	public LcomStudentSignup(String lastFirst, String studentUserId, String courseId, String currentCourseTeacherUserId, int userId) {
		this.lastFirst = lastFirst;
		this.studentUserId = studentUserId;
		this.studentCourseId = courseId;
		this.currentCourseTeacherUserId = currentCourseTeacherUserId;
		this.userId = userId;
	}

	public String getLastFirst() {
		return lastFirst;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setLastFirst(String lastFirst) {
		this.lastFirst = lastFirst;
	}

	public String getStudentUserId() {
		return studentUserId;
	}

	public void setStudentUserId(String studentUserId) {
		this.studentUserId = studentUserId;
	}

	public String getStudentCourseId() {
		return studentCourseId;
	}

	public void setStudentCourseId(String studentCourseId) {
		this.studentCourseId = studentCourseId;
	}

	public String getCurrentCourseTeacherUserId() {
		return currentCourseTeacherUserId;
	}

	public void setCurrentCourseTeacherUserId(String currentCourseTeacherUserId) {
		this.currentCourseTeacherUserId = currentCourseTeacherUserId;
	}
}
