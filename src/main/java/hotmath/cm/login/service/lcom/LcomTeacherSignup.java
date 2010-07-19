package hotmath.cm.login.service.lcom;

/** Encapsulation of LCOM teacher
 * 
 * @author casey
 *
 */
public class LcomTeacherSignup {

	String firstName, lastName,email,district,zip,courseName,courseId;
	
	public LcomTeacherSignup(String firstName, String lastName, String email, String district, String zip, String courseName,String courseId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.district = district;
		this.zip = zip;
		this.courseName = courseName;
		this.courseId = courseId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
}
