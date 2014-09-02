package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc.client.model.GroupModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class AssignmentModel implements Response {
	private int assignKey;
	private String comments;
	private String name;
	private GroupModel group;

	public AssignmentModel() {}
	
	public AssignmentModel(int assignKey, String name, String comments, GroupModel group) {
		this.assignKey = assignKey;
		this.name = name; 
		this.comments = comments;
		this.group = group;
	}

	public int getAssignKey() {
		return assignKey;
	}

	public void setAssignKey(int assignKey) {
		this.assignKey = assignKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public GroupModel getGroup() {
		return group;
	}

	public void setGroup(GroupModel group) {
		this.group = group;
	}
}

