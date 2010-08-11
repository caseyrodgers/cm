package hotmath.cm.login.service.lcom;

/** Encapsulation of LCOM teacher
 * 
 * @author casey
 *
 */
public class LcomTeacherSignup {

	String firstLast,teacherId, email,district,zip;
	int adminId;
	
	public LcomTeacherSignup(String firstLast, String teacherId, String email, String district, String zip) {
		this.firstLast = firstLast;
		this.teacherId = teacherId;
		this.email = email;
		this.district = district;
		this.zip = zip!=null?zip:"";
	}

	public String getSchoolName() {
		return "LCOM: " + district;
	}


	@Override
	public String toString() {
		return "LcomTeacherSignup [firstLast=" + firstLast + ", teacherId="
				+ teacherId + ", email=" + email + ", district=" + district
				+ ", zip=" + zip + ", adminId=" + adminId + "]";
	}



	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getFirstLast() {
		return firstLast;
	}

	public void setFirstLast(String firstLast) {
		this.firstLast = firstLast;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
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

}
