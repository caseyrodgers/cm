package hotmath.gwt.cm_tools.client.model;

import java.io.Serializable;

public class GroupInfoModel extends BaseModel implements Serializable, GroupInfoModelI {

	private static final long serialVersionUID = 3835182712109469633L;
	
	public static String ADMIN_ID      = "admin_id";
	public static String GROUP_NAME    = "group_name"; 
	public static String ID            = "id";
	public static String IS_SELF_REG   = "is_self_reg";
	public static String STUDENT_COUNT = "student_count";

	public GroupInfoModel() {
    }

    public GroupInfoModel(Integer adminId, Integer id, String groupName, Integer studentCount,
    	Boolean isSelfReg) {
        setAdminId(adminId);
        setId(id);
        setGroupName(groupName);
        setStudentCount(studentCount);
        setIsSelfReg(isSelfReg);
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
