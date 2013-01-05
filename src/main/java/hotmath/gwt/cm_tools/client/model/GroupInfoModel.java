package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class GroupInfoModel implements Response {

	int adminId;
	String groupName; 
	int id;
	boolean isSelfReg;
	boolean isActive;
	int studentCount;
	String description;
	
	public static final String NEW_GROUP = "--- Create Group ---";
    public static final Integer CREATE_GROUP = -1;
    public static final Integer NO_FILTERING = -2;
    public static final Integer NONE_GROUP = 1;

	public GroupInfoModel() {
    }

    public GroupInfoModel(Integer adminId, Integer id, String groupName, Integer studentCount,	Boolean isActive, Boolean isSelfReg) {
        this.adminId = adminId;
        this.id = id;
        this.groupName = groupName;
        this.studentCount = studentCount;
        this.isSelfReg = isSelfReg;
        this.isActive = isActive;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelfReg() {
        return isSelfReg;
    }

    public void setSelfReg(boolean isSelfReg) {
        this.isSelfReg = isSelfReg;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
