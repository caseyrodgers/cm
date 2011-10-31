package hotmath.gwt.cm_rpc.client.model;

import java.util.Date;

import hotmath.gwt.cm_rpc.client.rpc.Response;

@SuppressWarnings("serial")
public class CmParallelProgram implements Response {

	int id;
	int adminId;
	int cmProgId;
	int studentCount;
	String password;
	String name;
	String cmProgName;
	boolean isActive;
	Date createDate;
	
    public CmParallelProgram() {/* empty */}
    
    public CmParallelProgram(int id, int adminId, int cmProgId, String password, String name) {
        this.id       = id;
        this.adminId  = adminId;
        this.cmProgId = cmProgId;
        this.password = password;
        this.name     = name;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public int getCmProgId() {
		return cmProgId;
	}

	public void setCmProgId(int cmProgId) {
		this.cmProgId = cmProgId;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCmProgName() {
		return cmProgName;
	}

	public void setCmProgName(String cmProgName) {
		this.cmProgName = cmProgName;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
    public String toString() {
        return "CmParallelProgram [id=" + id + ", adminId=" + adminId + ", cmProgId="
                + cmProgId + ", password=" + password + ", name=" + name + ", studentCount="
                + studentCount + ", isActive=" + isActive + "]";
    }
}
