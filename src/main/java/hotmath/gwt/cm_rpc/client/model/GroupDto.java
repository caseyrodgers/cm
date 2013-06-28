package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class GroupDto implements Response {
    
	private static final long serialVersionUID = 4990841749746834603L;

	private int groupId;
	private int adminId;
    private String name;
    private String info;

    public GroupDto() {}
    
    public GroupDto(int groupId, String name, String info, int adminId) {
        this.groupId = groupId;
        this.name = name;
        this.info = info;
        this.adminId = adminId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
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
