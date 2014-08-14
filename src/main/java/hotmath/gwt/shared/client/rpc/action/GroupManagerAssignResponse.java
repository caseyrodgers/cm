package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class GroupManagerAssignResponse implements Response {

	List<StudentModelI> inGroup;
	List<StudentModelI> notInGroup;
	
	public GroupManagerAssignResponse() {
	}

    public List<StudentModelI> getInGroup() {
        return inGroup;
    }

    public void setInGroup(List<StudentModelI> inGroup) {
        this.inGroup = inGroup;
    }

    public List<StudentModelI> getNotInGroup() {
        return notInGroup;
    }

    public void setNotInGroup(List<StudentModelI> notInGroup) {
        this.notInGroup = notInGroup;
    }

}
