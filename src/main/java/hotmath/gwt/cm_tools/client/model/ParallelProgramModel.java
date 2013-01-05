package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ParallelProgramModel implements Response {
	
    int id;
    int adminId;
    boolean isActive;
    String name;
    int studentCount;
    String programName;
    String password;
    Integer progId;

	public ParallelProgramModel(){
        /** empty */
    }
	
	/**
	 * @param name
	 * @param id
	 * @param adminId
	 * @param password
	 * @param cmProgId
	 * @param cmProgId
	 * @param isActive
	 */

    public ParallelProgramModel(String name, Integer id, Integer adminId, String password, Integer cmProgId,
    		Integer studentCount, Boolean isActive, String programName) {
        this.name = name;
        this.id = id;
        this.adminId = adminId;
        this.password = password;
        this.progId = cmProgId;
        this.studentCount = studentCount;
        this.isActive = isActive;
        this.programName = programName;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getProgId() {
        return progId;
    }

    public void setProgId(int progId) {
        this.progId = progId;
    }

}