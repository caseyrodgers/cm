package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class GroupDto implements Response {
    
    private int groupId;
    private String name;

    public GroupDto() {}
    
    public GroupDto(int groupId, String name) {
        this.groupId = groupId;
        this.name = name;
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
    
    @Override
    public String toString() {
        return "GroupDto [groupId=" + groupId + ", name=" + name + "]";
    }

}
