package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class GroupDto implements Response {
    
    private int groupId;
    private String name;
    private String info;

    public GroupDto() {}
    
    public GroupDto(int groupId, String name, String info) {
        this.groupId = groupId;
        this.name = name;
        this.info = info;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "GroupDto [groupId=" + groupId + ", name=" + name + ", info=" + info + "]";
    }

}
