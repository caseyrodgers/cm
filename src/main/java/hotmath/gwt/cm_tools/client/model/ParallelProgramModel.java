package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ParallelProgramModel extends BaseModel implements Response {
	
	public static final String IS_ACTIVE     = "isActive";
	public static final String NAME          = "name";
	public static final String STUDENT_COUNT = "studentCount";

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
    		Integer studentCount, Boolean isActive) {
        setName(name);
        setId(id);
        setAdminId(adminId);
        setPassword(password);
        setCmProgId(cmProgId);
        setStudentCount(studentCount);
        setIsActive(isActive);
    }

    public void setName(String name) {
        set(NAME, name);
    }

    public String getName() {
        return get(NAME);
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public Integer getId() {
        return get("id");
    }

    public void setAdminId(int adminId) {
        set("adminId", adminId);
    }

    public Integer getAdminId() {
        return get("adminid");
    }

    public void setCmProgId(Integer id) {
        set("cmProgId", id);
    }

    public Integer getCmProgId() {
        return get("cmProgId");
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public Integer getPassword() {
        return get("password");
    }

    public void setStudentCount(Integer count) {
        set(STUDENT_COUNT, count);
    }

    public Integer getStudentCount() {
        return get(STUDENT_COUNT);
    }

    public void setIsActive(Boolean isActive) {
        set(IS_ACTIVE, isActive);
    }

    public Boolean getIsActive() {
        return get(IS_ACTIVE);
    }

}