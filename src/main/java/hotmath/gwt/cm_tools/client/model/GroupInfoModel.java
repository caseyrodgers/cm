package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.io.Serializable;

public class GroupInfoModel extends BaseModel implements Serializable, GroupInfoModelI, Response {

	private static final long serialVersionUID = 3835182712109469633L;
	
	public static String ADMIN_ID      = "admin_id";
	public static String GROUP_NAME    = "group_name"; 
	public static String ID            = "id";
	public static String IS_SELF_REG   = "is_self_reg";
	public static String IS_ACTIVE = "is_active";
	public static String STUDENT_COUNT = "student_count";
	public static String DESCRIPTION = "group_description";
	public static final String NEW_GROUP = "--- Create Group ---";
    public static final Integer CREATE_GROUP = -1;
    public static final Integer NO_FILTERING = -2;
    public static final Integer NONE_GROUP = 1;

	public GroupInfoModel() {
    }

    public GroupInfoModel(Integer adminId, Integer id, String groupName, Integer studentCount,
    	Boolean isActive, Boolean isSelfReg) {
        setAdminId(adminId);
        setId(id);
        setGroupName(groupName);
        setStudentCount(studentCount);
        setIsSelfReg(isSelfReg);
        setIsActive(isActive);
    }
    
    
    public void setIsActive(Boolean isActive) {
        set(IS_ACTIVE, isActive);
    }
    
    public Boolean getIsActive() {
        return get(IS_ACTIVE);
    }
    
    public void setAdminId(Integer adminId) {
        set(ADMIN_ID, adminId);
    }
    
    public Integer getAdminId() {
        return get(ADMIN_ID);
    }
    public void setId(Integer id) {
        set(ID, id);
    }
    
    public Integer getId() {
        return get(ID);
    }

    public void setGroupName(String groupName) {
        set(GROUP_NAME, groupName);
    }
    
    public void setDescription(String desc) {
        set(DESCRIPTION, desc);
    }

    public String getDescription() {
        return get(DESCRIPTION);
    }
    
    public void setIsSelfReg(Boolean isSelfReg) {
    	set(IS_SELF_REG, isSelfReg);
    }
    
    public Boolean getIsSelfReg() {
    	return get(IS_SELF_REG);
    }

    public void setStudentCount(Integer studentCount) {
        set(STUDENT_COUNT, studentCount);
    }

    @Override
    public Integer getCount() {
        return get(STUDENT_COUNT);
    }

    @Override
    public String getName() {
        return get(GROUP_NAME);
    }
}
