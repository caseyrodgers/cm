package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;

import java.util.List;

public class GroupManagerAssignResponse implements Response {

	List<StudentModelExt> inGroup;
	List<StudentModelExt> notInGroup;
	
	public GroupManagerAssignResponse() {
	}
	
	public List<StudentModelExt> getInGroup() {
		return inGroup;
	}

	public void setInGroup(List<StudentModelExt> inGroup) {
		this.inGroup = inGroup;
	}

	public List<StudentModelExt> getNotInGroup() {
		return notInGroup;
	}

	public void setNotInGroup(List<StudentModelExt> notInGroup) {
		this.notInGroup = notInGroup;
	}

	

}
