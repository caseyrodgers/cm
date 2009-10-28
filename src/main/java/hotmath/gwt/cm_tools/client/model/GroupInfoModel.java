package hotmath.gwt.cm_tools.client.model;

import java.io.Serializable;

public class GroupInfoModel extends BaseModel implements Serializable, GroupInfoModelI {

    public GroupInfoModel() {
    }

    public GroupInfoModel(Integer adminId, Integer id, String groupName, Integer studentCount) {
        setAdminId(adminId);
        setId(id);
        setGroupName(groupName);
        setStudentCount(studentCount);
    }
    
    public void setAdminId(Integer adminId) {
        set("admin_id", adminId);
    }
    
    public Integer getAdminId() {
        return get("admin_id");
    }
    public void setId(Integer id) {
        set("id", id);
    }
    
    public Integer getId() {
        return get("id");
    }

    public void setGroupName(String groupName) {
        set("group_name", groupName);
    }

    public void setStudentCount(Integer studentCount) {
        set("student_count", studentCount);
    }

    @Override
    public Integer getCount() {
        return get("student_count");
    }

    @Override
    public String getName() {
        return get("group_name");
    }
}
